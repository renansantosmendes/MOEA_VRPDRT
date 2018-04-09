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
        OperatorFactory.getInstance().addProvider(new OperatorProvider() {
            @Override
            public String getMutationHint(Problem problem) {
                return null;
            }

            @Override
            public String getVariationHint(Problem problem) {
                return null;
            }

            @Override
            public Variation getVariation(String name, Properties properties, Problem problem) {
                if (name.equalsIgnoreCase("Shuffle2")) {
                    TypedProperties typedProperties = new TypedProperties(properties);
                    return new Shuffle2(typedProperties.getDouble("Shuffle.rate", 1.0));
                } else {
                    return null;
                }
            }
        });

        NondominatedPopulation result = new Executor()
                .withProblemClass(MOEAVRPDRT.class)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(2000)
                .withProperty("populationSize", 100)
                .withProperty("operator", "2x+Shuffle2")
//                .withProperty("operator", "Shuffle2")
                .withProperty("Shuffle2.rate", 0.02)
                .withProperty("2x.rate", 0.7)
                .runExperiment();

        System.out.format("Objective1  Objective2%n");
        for (Solution solution : result) {
            System.out.println(solution.getObjective(0) + "," + solution.getObjective(1));

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
