/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.algorithm;

import ReductionTechniques.HierarchicalCluster;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;

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
        //System.out.println("Generation = " + generation);
        NondominatedSortingPopulation population = getPopulation();
        EpsilonBoxDominanceArchive archive = getArchive();
        Population offspring = new Population();
        int populationSize = population.size();

//        HierarchicalCluster hc = new HierarchicalCluster(, 2);
        
        
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

    
}
