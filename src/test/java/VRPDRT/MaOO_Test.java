/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.DataOutput;
import ProblemRepresentation.ProblemSolution;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 *
 * @author renansantos
 */
public class MaOO_Test {

    @Test
    public void maooTest() {
        String problemName = "ZDT3";
        List<NondominatedPopulation> result = new Executor()
                .withProblem(problemName)
                .withAlgorithm("CLNSGAII")
                .withMaxEvaluations(100000)
                .withProperty("populationSize", 100)
                .withProperty("sbx.rate", 0.7)
                .withProperty("pm.rate", 0.01)
                .runSeeds(1);

        DataOutput dataOutput = new DataOutput("CLNSGAII", problemName);
        NondominatedPopulation combinedPareto = new NondominatedPopulation();
        List<Solution> solutionPopulation = new ArrayList<>();

        for (NondominatedPopulation population : result) {
            for (Solution solution : population) {
                combinedPareto.add(solution);
//                System.out.println(solution.getObjective(0) + "," + solution.getObjective(1));
            }
        }

        System.out.println("combined pareto");
        for (Solution solution : combinedPareto) {
            System.out.println(solution.getObjective(0) + "," + solution.getObjective(1));
            solutionPopulation.add(solution);
        }
        dataOutput.savePopulationOfSolutions(solutionPopulation);
    }
}
