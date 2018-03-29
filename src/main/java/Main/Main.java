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
    }

}
