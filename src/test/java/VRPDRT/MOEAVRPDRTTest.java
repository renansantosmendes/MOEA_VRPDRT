/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.DataOutput;
import InstanceReader.Instance;
import ProblemRepresentation.ProblemSolution;
import ProblemRepresentation.RankedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.permutation.Shuffle2;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;

import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.TypedProperties;

/**
 *
 * @author renansantos
 */
public class MOEAVRPDRTTest {

    private MOEAVRPDRT problem;
    private VRPDRT subProblem;
    private String path = "/home/renansantos/Área de Trabalho/Excel Instances/";
    private RankedList rankedList;
    private Instance instance = new Instance();

    public MOEAVRPDRTTest() {
        problem = new MOEAVRPDRT()
                .setNumberOfObjectives(9)
                .setNumberOfVariables(1)
                .setNumberOfConstraints(0);

        initializeData();

    }

    private void initializeData() {
        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        instance.setNumberOfRequests(50)
                .setRequestTimeWindows(10)
                .setInstanceSize("s")
                .setNumberOfNodes(12)
                .setNumberOfVehicles(250)
                .setVehicleCapacity(4);

        subProblem = new VRPDRT(instance, path, rankedList);
    }

    private List<Integer> copyArrayToListInteger(int[] array) {
        List<Integer> list = new ArrayList<>();
        int size = array.length;

        for (int i = 0; i < size; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public ProblemSolution convertSolution(Solution solution) {
        initializeData();
        ProblemSolution ps = subProblem
                .rebuildSolution(copyArrayToListInteger(EncodingUtils.getPermutation(solution.getVariable(0))),
                        subProblem.getData().getRequests());
        return ps;
    }

    @Test
    public void moeadTest() {
        List<NondominatedPopulation> result = new Executor()
                .withProblemClass(MOEAVRPDRT.class)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(2000)
                .withProperty("populationSize", 200)
                .withProperty("operator", "2x+swap")
                .withProperty("swap.rate", 0.1)
                .withProperty("2x.rate", 0.7)
                .runSeeds(3);

        DataOutput dataOutput = new DataOutput("MOEAVRPDRT", instance.getInstanceName());
        NondominatedPopulation combinedPareto = new NondominatedPopulation();
        List<ProblemSolution> solutionPopulation = new ArrayList<>();
        
        for (NondominatedPopulation population : result) {
            for (Solution solution : population) {
                combinedPareto.add(solution);
                System.out.println(solution.getObjective(0) + "," + solution.getObjective(1));
            }
        }
        
        System.out.println("combined pareto");
        for (Solution solution : combinedPareto) {
            System.out.println(solution.getObjective(0) + "," + solution.getObjective(1));
            solutionPopulation.add(convertSolution(solution));
        }
        dataOutput.savePopulation(solutionPopulation);
    }

}
