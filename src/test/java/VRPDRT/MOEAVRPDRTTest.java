/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 *
 * @author renansantos
 */
public class MOEAVRPDRTTest {

    MOEAVRPDRT problem;

    public MOEAVRPDRTTest() {
        problem = new MOEAVRPDRT()
                .setNumberOfObjectives(2)
                .setNumberOfVariables(1)
                .setNumberOfConstraints(0);
    }

    @Test
    public void mainTest() {
        NondominatedPopulation result = new Executor()
                .withProblemClass(MOEAVRPDRT.class)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(100)
                .withProperty("populationSize", 100)
                .withProperty("operator", "2X+swap")
                .withProperty("swap.rate", 0.02)
                .withProperty("2X.rate", 0.7)
                .runExperiment();

        System.out.format("Objective1  Objective2%n");
        for (Solution solution : result) {
            System.out.format("%.4f      %.4f%n",
                    solution.getObjective(0),
                    solution.getObjective(1));
            
           
        }
        
        for (Solution solution : result) {
            System.out.format("%.4f      %.4f%n",
                    solution.getObjective(0),
                    solution.getObjective(1));
            
            System.out.println(solution.getVariable(0));
            System.out.println(solution.getObjectives());
        }
    }

}
