/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.Instance;
import ProblemRepresentation.ProblemSolution;
import ProblemRepresentation.RankedList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.moeaframework.*;
import org.moeaframework.core.*;
import org.moeaframework.core.variable.EncodingUtils;

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

    @Test
    public void nsgaiiTest() {
//        NondominatedPopulation result = new Executor()
//                .withProblemClass(MOEAVRPDRT.class)
//                .withAlgorithm("NSGAII")
//                .withMaxEvaluations(10)
//                .withProperty("populationSize", 10000)
//                .withProperty("operator", "2X+swap")
//                .withProperty("swap.rate", 0.02)
//                .withProperty("2X.rate", 0.7)
//                .runExperiment();
//
//        System.out.format("Objective1  Objective2%n");
//        for (Solution solution : result) {
//            System.out.format("%.4f      %.4f%n",
//                    solution.getObjective(0),
//                    solution.getObjective(1));
//
//            int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
//            List<Integer> solutionRepresentation = copyArrayToListInteger(array);
//            initializeData();
//            ProblemSolution ps = subProblem.rebuildSolution(solutionRepresentation, subProblem.getData().getRequests());
//            System.out.println("after rebuilding = " + ps);
//
//        }
//        
//        for (Solution solution : result) {
//            System.out.format("%.4f      %.4f%n",
//                    solution.getObjective(0),
//                    solution.getObjective(1));

//            int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
//            List<Integer> solutionRepresentation = copyArrayToListInteger(array);
//            initializeData();
//            ProblemSolution ps = subProblem.rebuildSolution(solutionRepresentation, subProblem.getData().getRequests());
//            System.out.println("after rebuilding = " + ps);
//        }
    }

    @Test
    public void moeadTest() {
        NondominatedPopulation result = new Executor()
                .withProblemClass(MOEAVRPDRT.class)
                .withAlgorithm("MOEAD")
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
                    + solution.getObjective(1) + ","
                    + solution.getObjective(2) + ","
                    + solution.getObjective(3) + ","
                    + solution.getObjective(4) + ","
                    + solution.getObjective(5) + ","
                    + solution.getObjective(6) + ","
                    + solution.getObjective(7) + ","
                    + solution.getObjective(8));

            int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
            List<Integer> solutionRepresentation = copyArrayToListInteger(array);
            //initializeData();
            //ProblemSolution ps = subProblem.rebuildSolution(solutionRepresentation, subProblem.getData().getRequests());
            //System.out.println("after rebuilding = " + ps);

        }

//        for (Solution solution : result) {
            //System.out.println(solution.getObjectives());

//            int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
//            List<Integer> solutionRepresentation = copyArrayToListInteger(array);
//            initializeData();
//            ProblemSolution ps = subProblem.rebuildSolution(solutionRepresentation, subProblem.getData().getRequests());
//            System.out.println("after rebuilding = " + ps);
//        }
    }

}
