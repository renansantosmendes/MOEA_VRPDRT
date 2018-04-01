/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.*;
import ProblemRepresentation.*;
import java.util.*;
import org.moeaframework.core.*;
import org.moeaframework.core.variable.*;

/**
 *
 * @author renansantos
 */
public class MOEAVRPDRT implements Problem {

    private Instance instance = new Instance();
    private String path = "/home/renansantos/Área de Trabalho/Excel Instances/";
    private VRPDRT problem;
    private RankedList rankedList;
    private int numberOfVariables = 1;
    private int numberOfObjectives;
    private int numberOfConstraints;

    public MOEAVRPDRT() {
        instance.setNumberOfRequests(50)
                .setRequestTimeWindows(10)
                .setInstanceSize("s")
                .setNumberOfNodes(12)
                .setNumberOfVehicles(250)
                .setVehicleCapacity(4);

        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        problem = new VRPDRT(instance, path, rankedList);
    }

    @Override
    public String getName() {
        return "MOEAVRPDRT";
    }

    @Override
    public int getNumberOfVariables() {
        return this.numberOfVariables;
    }

    @Override
    public int getNumberOfObjectives() {
        return this.numberOfObjectives;
    }

    @Override
    public int getNumberOfConstraints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void evaluate(Solution solution) {
        int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
//        for(int i=0;i<array.length;i++){
//            System.out.print(array[i] + " ");
//        }
        List<Integer> solutionRepresentation = copyArrayToList(array);
        System.out.println(solutionRepresentation);
        
        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        problem = new VRPDRT(instance, path, rankedList);
    }

    @Override
    public Solution newSolution() {
        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        problem = new VRPDRT(instance, path, rankedList);
        //System.out.println("Random Solution = " + problem.buildRandomSolution().getLinkedRouteList());

        ProblemSolution ps = problem.buildRandomSolution();
        System.out.println(ps);
        int arraySize = ps.getLinkedRouteList().size();
        int[] array = copyListToArray(ps.getLinkedRouteList());
        Permutation permutation = new Permutation(array);
        Solution solution = new Solution(1, this.numberOfObjectives, this.numberOfConstraints);

        //solution.setVariable(0, EncodingUtils.newPermutation(numberOfVariables));
        solution.setVariable(0, permutation);
        return solution;
    }

    private int[] copyListToArray(List<Integer> list) {
        int size = list.size();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private List<Integer> copyArrayToList(int[] array){
        List<Integer> list = new ArrayList<>();
        int size = array.length;
        
        for (int i = 0; i < size; i++) {
            list.add(array[i]);
        }
        return list;
    }
    @Override
    public void close() {

    }

}
