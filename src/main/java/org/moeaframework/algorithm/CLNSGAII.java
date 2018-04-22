/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.algorithm;

import static Algorithms.EvolutionaryAlgorithms.getMatrixOfObjetives;
import ProblemRepresentation.ProblemSolution;
import ReductionTechniques.*;
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
    public CLNSGAII(Problem problem, NondominatedSortingPopulation population,
            EpsilonBoxDominanceArchive archive, Selection selection,
            Variation variation, Initialization initialization) {
        super(problem, population, archive, initialization);
        this.selection = selection;
        this.variation = variation;
        generation = 0;
    }

    @Override
    public void iterate() {
        generation++;
        System.out.println("Generation = " + generation);
        NondominatedSortingPopulation population = getPopulation();
        EpsilonBoxDominanceArchive archive = getArchive();
        Population offspring = new Population();
        int populationSize = population.size();

        HierarchicalCluster hc = new HierarchicalCluster(getMatrixOfObjetives(getSolutionList(population)), 2);
                hc.setCorrelation(CorrelationType.KENDALL).reduce()
                        .getTransfomationList().forEach(System.out::println);
        
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

    public double[][] getMatrixOfObjetives(List<ProblemSolution> population, List<Double> parameters) {
        int rows = population.size();
        int columns = population.get(0).getObjectives().size();
        double[][] matrix = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = population.get(i).getObjectives().get(j) * parameters.get(j);
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
    
    public List<Solution> getSolutionList(NondominatedSortingPopulation population){
        List<Solution> populationList = new ArrayList<>();
        for(Solution solution: population){
            populationList.add(solution);
        }
        return populationList;
    }
}
