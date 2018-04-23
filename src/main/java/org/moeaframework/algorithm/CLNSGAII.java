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
import org.moeaframework.core.variable.EncodingUtils;

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
        hc.setCorrelation(CorrelationType.KENDALL).reduce()
                .getTransfomationList().forEach(System.out::println);

        Solution solutionTest = population.get(0);
        int[] array = EncodingUtils.getPermutation(solutionTest.getVariable(0));
        List<Integer> solutionRepresentation = copyArrayToListInteger(array);
        ProblemSolution ps = problemTest.rebuildSolution(solutionRepresentation, problemTest.getRequestListCopy());
        
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

        if (archive != null) {
            archive.addAll(offspring);
        }

        population.addAll(offspring);
        population.truncate(populationSize);
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

    public void evaluateAggregatedObjectiveFunctions(List<Double> parameters, List<List<Integer>> matrix, Solution solution, ProblemSolution ps) {

//        ProblemSolution ps2 = 
        double[] objectives = new double[2];
        objectives[0] = parameters.get(0) * matrix.get(0).get(0) * ps.getTotalDistance()
                + parameters.get(1) * matrix.get(0).get(1) * ps.getTotalDeliveryDelay()
                + parameters.get(2) * matrix.get(0).get(2) * ps.getTotalRouteTimeChargeBanlance()
                + parameters.get(3) * matrix.get(0).get(3) * ps.getNumberOfNonAttendedRequests()
                + parameters.get(4) * matrix.get(0).get(4) * ps.getNumberOfVehicles()
                + parameters.get(5) * matrix.get(0).get(5) * ps.getTotalTravelTime()
                + parameters.get(6) * matrix.get(0).get(6) * ps.getTotalWaintingTime()
                + parameters.get(7) * matrix.get(0).get(7) * ps.getDeliveryTimeWindowAntecipation()
                + parameters.get(8) * matrix.get(0).get(8) * ps.getTotalOccupationRate();

        objectives[1] = parameters.get(0) * matrix.get(1).get(0) * ps.getTotalDistance()
                + parameters.get(1) * matrix.get(1).get(1) * ps.getTotalDeliveryDelay()
                + parameters.get(2) * matrix.get(1).get(2) * ps.getTotalRouteTimeChargeBanlance()
                + parameters.get(3) * matrix.get(1).get(3) * ps.getNumberOfNonAttendedRequests()
                + parameters.get(4) * matrix.get(1).get(4) * ps.getNumberOfVehicles()
                + parameters.get(5) * matrix.get(1).get(5) * ps.getTotalTravelTime()
                + parameters.get(6) * matrix.get(1).get(6) * ps.getTotalWaintingTime()
                + parameters.get(7) * matrix.get(1).get(7) * ps.getDeliveryTimeWindowAntecipation()
                + parameters.get(8) * matrix.get(1).get(8) * ps.getTotalOccupationRate();

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
