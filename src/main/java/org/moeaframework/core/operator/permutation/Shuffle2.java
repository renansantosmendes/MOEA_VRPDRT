/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.core.operator.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.PermutationMOEA;

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
                    && (variable instanceof PermutationMOEA)) {
                evolve((PermutationMOEA) variable);
            }
        }

        return new Solution[]{result};
    }

    /**
     * Evolves the specified permutation using the swap mutation operator.
     *
     * @param permutation the permutation to be mutated
     */
    public static void evolve(PermutationMOEA permutation) {
        int i = PRNG.nextInt(permutation.size());
        int j = PRNG.nextInt(permutation.size() - 1);

        if (i == j) {
            j = permutation.size() - 1;
        }

        int[] array = EncodingUtils.getPermutation(permutation);
        List<Integer> indexes = new ArrayList<>();
        indexes.add(i);
        indexes.add(j);
//        indexes.stream().sorted(Comparator.naturalOrder());
        List<Integer> solutionRepresentation = copyArrayToListInteger(array);

        int min = Collections.min(indexes);
        int max = Collections.max(indexes);

        List<Integer> aux = new ArrayList<>(solutionRepresentation.subList(min, max));

        Collections.shuffle(aux);

        solutionRepresentation.subList(min, max).clear();
        solutionRepresentation.addAll(min, aux);
        
        int[] newArray = copyListToArrayInteger(solutionRepresentation);
        
        permutation = new PermutationMOEA(newArray);
    }

    @Override
    public int getArity() {
        return 1;
    }

    private static int[] copyListToArrayInteger(List<Integer> list) {
        int size = list.size();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static double[] copyListToArrayDouble(List<Double> list) {
        int size = list.size();
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static List<Integer> copyArrayToListInteger(int[] array) {
        List<Integer> list = new ArrayList<>();
        int size = array.length;

        for (int i = 0; i < size; i++) {
            list.add(array[i]);
        }
        return list;
    }

}
