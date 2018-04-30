/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.*;
import ProblemRepresentation.*;
import ReductionTechniques.HierarchicalCluster;
import java.util.*;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;



/**
 *
 * @author renansantos
 */
public class MOEAVRPDRT implements Problem {

    private Instance instance = new Instance();
    private String path = "/home/renansantos/Área de Trabalho/Excel Instances/";
//    private String path = "/home/rmendes/VRPDRT/";
    private VRPDRT problem;
    private RankedList rankedList;
    private int numberOfVariables = 1;
    private int numberOfObjectives = 9;
    private int numberOfConstraints = 0;
    private DataOutput dataOutput;

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
        problem.startSeed();
        dataOutput = new DataOutput("MOEAVRPDRT", instance.getInstanceName());
    }

    public MOEAVRPDRT setNumberOfObjectives(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        return this;
    }

    public MOEAVRPDRT setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
        return this;
    }

    public MOEAVRPDRT setNumberOfConstraints(int numberOfConstraints) {
        this.numberOfConstraints = numberOfConstraints;
        return this;
    }
    
    public MOEAVRPDRT setPath(String path){
        this.path = path;
        return this;
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
        return this.numberOfConstraints;
    }

    public VRPDRT getProblem(){
        return this.problem;
    }
    @Override
    public void evaluate(Solution solution) {
        int[] array = EncodingUtils.getPermutation(solution.getVariable(0));
        List<Integer> solutionRepresentation = copyArrayToListInteger(array);
        ProblemSolution ps = problem.rebuildSolution(solutionRepresentation, problem.getRequestListCopy());
        solution.setObjectives(copyListToArrayDouble(ps.getObjectives()));
        solution.setProblemSolution(ps);
//        System.out.println("solution " + ps.getLinkedRouteList());
//        System.out.println("informations " + ps);
    }

    @Override
    public Solution newSolution() {
        RankedList rankedList = new RankedList(instance.getNumberOfNodes());
        rankedList.setAlphaD(0.20)
                .setAlphaP(0.15)
                .setAlphaT(0.10)
                .setAlphaV(0.55);

        ProblemSolution ps = problem.buildRandomSolution();
//        int arraySize = ps.getLinkedRouteList().size();
        int[] array = copyListToArrayInteger(ps.getLinkedRouteList());
        Permutation permutation = new Permutation(array);

        Solution solution = new Solution(1, this.numberOfObjectives, this.numberOfConstraints);
        solution.setVariable(0, permutation);
        return solution;
    }

    public void setHierarchicalCluster(HierarchicalCluster hc){
        
    }
    
    private int[] copyListToArrayInteger(List<Integer> list) {
        int size = list.size();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private double[] copyListToArrayDouble(List<Double> list) {
        int size = list.size();
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private List<Integer> copyArrayToListInteger(int[] array) {
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
