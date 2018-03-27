package Main;

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
                .withProblem("UF1")
                .withAlgorithm("MOEAD")
                .withMaxEvaluations(10000)
                .run();

        //display the results
        System.out.format("Objective1  Objective2%n");

        for (Solution solution : result) {
            System.out.format("%.4f      %.4f%n",
                    solution.getObjective(0),
                    solution.getObjective(1));
        }
    }
}
