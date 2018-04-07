package Main;

import VRPDRT.MOEAVRPDRT;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author renansantos
 */
public class Main {

    public static void main(String[] args) {
        NondominatedPopulation result = new Executor()
                .withProblemClass(MOEAVRPDRT.class)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(100)
                .withProperty("populationSize", 100)
                .withProperty("operator", "2X+swap")
                .withProperty("swap.rate", 0.02)
                .withProperty("2X.rate", 0.7)
                .run();

        System.out.format("Objective1  Objective2%n");
        for (Solution solution : result) {
            System.out.println(
                    solution.getObjective(0) + ","
                    + solution.getObjective(1));
        }
    }
}
