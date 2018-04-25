/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.algorithm;

import InstanceReader.Instance;
import ProblemRepresentation.*;
import ReductionTechniques.*;
import VRPDRT.VRPDRT;
import java.util.*;
import org.moeaframework.core.*;
import org.moeaframework.core.comparator.*;
import org.moeaframework.core.operator.*;

/**
 *
 * @author renansantos
 */
public class CLNSGAII extends AbstractEvolutionaryAlgorithm implements
        EpsilonBoxEvolutionaryAlgorithm {

    /**
     * The selection operator. If {@code null}, this algorithm uses binary
     * tournament selection without replacement, replicating the behavior of the
     * original NSGA-II implementation.
     */
    private final Selection selection;
    private static int generation = 0;
    /**
     * The variation operator.
     */
    private final Variation variation;
    private final String instanceName;
    private Instance instance;
    private Parameters parameters;
    private final int numberOfReducedObjectives = 2;
    private String path = "/home/renansantos/Área de Trabalho/Excel Instances/";
    private VRPDRT problemTest;
    private RankedList rankedList;

    /**
     * Constructs the NSGA-II algorithm with the specified components.
     *
     * @param problem the problem being solved
     * @param population the population used to store solutions
     * @param archive the archive used to store the result; can be {@code null}
     * @param selection the selection operator
     * @param variation the variation operator
     * @param initialization the initialization method
     */
    public CLNSGAII(Problem problem, String instanceName, NondominatedSortingPopulation population,
            EpsilonBoxDominanceArchive archive, Selection selection,
            Variation variation, Initialization initialization) {
        super(problem, population, archive, initialization);
        this.selection = selection;
        this.variation = variation;
        this.instanceName = instanceName;
        this.instance = new Instance(this.instanceName);
        this.parameters = new Parameters(this.instance);
        generation = 0;

        instance.setNumberOfVehicles(250);

        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        problemTest = new VRPDRT(instance, path, rankedList);
    }

    @Override
    public void iterate() {
        generation++;
        System.out.println("Generation = " + generation);
        NondominatedSortingPopulation population = getPopulation();
        EpsilonBoxDominanceArchive archive = getArchive();
        Population offspring = new Population();
        int populationSize = population.size();

        HierarchicalCluster hc = new HierarchicalCluster(getMatrixOfObjetives(getSolutionListFromPopulation(population),
                parameters.getParameters()), this.numberOfReducedObjectives);
        hc.printDissimilarity();
        hc.setCorrelation(CorrelationType.KENDALL);
        hc.reduce().getTransfomationList().forEach(System.out::println);

        //population.forEach(System.out::println);
        population.forEach(s -> s.reduceNumberOfObjectives(parameters, hc.getTransfomationList(), 2));
        //population.forEach(System.out::println);

        if (selection == null) {
            // recreate the original NSGA-II implementation using binary
            // tournament selection without replacement; this version works by
            // maintaining a pool of candidate parents.
            LinkedList<Solution> pool = new LinkedList<Solution>();

            DominanceComparator comparator = new ChainedComparator(
                    new ParetoDominanceComparator(),
                    new CrowdingComparator());

            while (offspring.size() < populationSize) {
                // ensure the pool has enough solutions
                while (pool.size() < 2 * variation.getArity()) {
                    List<Solution> poolAdditions = new ArrayList<Solution>();

                    for (Solution solution : population) {
                        poolAdditions.add(solution);
                    }

                    PRNG.shuffle(poolAdditions);
                    pool.addAll(poolAdditions);
                }

                // select the parents using a binary tournament
                Solution[] parents = new Solution[variation.getArity()];

                for (int i = 0; i < parents.length; i++) {
                    parents[i] = TournamentSelection.binaryTournament(
                            pool.removeFirst(),
                            pool.removeFirst(),
                            comparator);
                }

                // evolve the children
                offspring.addAll(variation.evolve(parents));
            }
        } else {
            // run NSGA-II using selection with replacement; this version allows
            // using custom selection operators
            while (offspring.size() < populationSize) {
                Solution[] parents = selection.select(variation.getArity(),
                        population);

                offspring.addAll(variation.evolve(parents));
            }
        }
        evaluateAll(offspring);
        //offspring.forEach(System.out::println);
        offspring.forEach(s -> s.reduceNumberOfObjectives(parameters, hc.getTransfomationList(), 2));
        //offspring.forEach(System.out::println);
        if (archive != null) {
            archive.addAll(offspring);
        }

        population.addAll(offspring);
        population.truncate(populationSize);
        
        //population.forEach(System.out::println);
        population.forEach(s -> s.increaseNumberOfObjectives());
        //population.forEach(System.out::println);
    }

    @Override
    public EpsilonBoxDominanceArchive getArchive() {
        return (EpsilonBoxDominanceArchive) super.getArchive();
    }

    @Override
    public NondominatedSortingPopulation getPopulation() {
        return (NondominatedSortingPopulation) super.getPopulation();
    }

    public double[][] getMatrixOfObjetives(List<Solution> population, List<Double> parameters) {
        int rows = population.size();
        int columns = population.get(0).getObjectives().length;
        double[][] matrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = population.get(i).getObjectives()[j] * parameters.get(j);
            }
        }
        return matrix;
    }

    public double[][] getMatrixOfObjetives(List<Solution> population) {
        int rows = population.size();
        int columns = population.get(0).getObjectives().length;
        double[][] matrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = population.get(i).getObjectives()[j];
            }
        }
        return matrix;
    }
    
    private void printMatrix(double[][] matrix){
        int rows = population.size();
        int columns = population.get(0).getNumberOfObjectives();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public List<Solution> getSolutionList(NondominatedSortingPopulation population) {
        List<Solution> populationList = new ArrayList<>();
        for (Solution solution : population) {
            populationList.add(solution);
        }
        return populationList;
    }

    public List<Solution> getSolutionListFromPopulation(Population population) {
        List<Solution> populationList = new ArrayList<>();
        for (Solution solution : population) {
            populationList.add(solution);
        }
        return populationList;
    }

    public void evaluateAggregatedObjectiveFunctions(List<List<Integer>> matrix, Solution solution) {

        List<Double> parameters = this.parameters.getParameters();
//        ProblemSolution ps2 = 
        double[] objectives = new double[2];
        objectives[0] = parameters.get(0) * matrix.get(0).get(0) * solution.getObjective(0)
                + parameters.get(1) * matrix.get(0).get(1) * solution.getObjective(1)
                + parameters.get(2) * matrix.get(0).get(2) * solution.getObjective(2)
                + parameters.get(3) * matrix.get(0).get(3) * solution.getObjective(3)
                + parameters.get(4) * matrix.get(0).get(4) * solution.getObjective(4)
                + parameters.get(5) * matrix.get(0).get(5) * solution.getObjective(5)
                + parameters.get(6) * matrix.get(0).get(6) * solution.getObjective(6)
                + parameters.get(7) * matrix.get(0).get(7) * solution.getObjective(7)
                + parameters.get(8) * matrix.get(0).get(8) * solution.getObjective(8);

        objectives[1] = parameters.get(0) * matrix.get(1).get(0) * solution.getObjective(0)
                + parameters.get(1) * matrix.get(1).get(1) * solution.getObjective(1)
                + parameters.get(2) * matrix.get(1).get(2) * solution.getObjective(2)
                + parameters.get(3) * matrix.get(1).get(3) * solution.getObjective(3)
                + parameters.get(4) * matrix.get(1).get(4) * solution.getObjective(4)
                + parameters.get(5) * matrix.get(1).get(5) * solution.getObjective(5)
                + parameters.get(6) * matrix.get(1).get(6) * solution.getObjective(6)
                + parameters.get(7) * matrix.get(1).get(7) * solution.getObjective(7)
                + parameters.get(8) * matrix.get(1).get(8) * solution.getObjective(8);

        solution.setObjectives(objectives);

    }

    private List<Integer> copyArrayToListInteger(int[] array) {
        List<Integer> list = new ArrayList<>();
        int size = array.length;

        for (int i = 0; i < size; i++) {
            list.add(array[i]);
        }
        return list;
    }
}
