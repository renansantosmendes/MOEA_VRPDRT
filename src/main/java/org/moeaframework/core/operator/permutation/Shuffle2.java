/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.core.operator.permutation;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Permutation;

/**
 *
 * @author renansantos
 */
public class Shuffle2 implements Variation {

    /**
     * The probability of mutating a variable.
     */
    private final double probability;

    /**
     * Constructs a swap mutation operator with the specified probability of
     * mutating a variable.
     *
     * @param probability the probability of mutating a variable
     */
    public Shuffle2(double probability) {
        super();
        this.probability = probability;
    }

    @Override
    public Solution[] evolve(Solution[] parents) {
        Solution result = parents[0].copy();

        for (int i = 0; i < result.getNumberOfVariables(); i++) {
            Variable variable = result.getVariable(i);

            if ((PRNG.nextDouble() <= probability)
                    && (variable instanceof Permutation)) {
                evolve((Permutation) variable);
            }
        }

        return new Solution[]{result};
    }

    /**
     * Evolves the specified permutation using the swap mutation operator.
     *
     * @param permutation the permutation to be mutated
     */
    public static void evolve(Permutation permutation) {
        int i = PRNG.nextInt(permutation.size());
        int j = PRNG.nextInt(permutation.size() - 1);

        if (i == j) {
            j = permutation.size() - 1;
        }

        permutation.swap(i, j);
    }

    @Override
    public int getArity() {
        return 1;
    }

}
