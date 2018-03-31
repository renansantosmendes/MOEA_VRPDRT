/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the edito
r.
 */
package VRPDRT;

import InstanceReader.Instance;
import ProblemRepresentation.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author renansantos
 */
public class TestClassVRPDRT {

    private Instance instance = new Instance();
    private String path = "/home/renansantos/Área de Trabalho/Excel Instances/";
    private VRPDRT problem;

    public TestClassVRPDRT() {
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

    @Test
    public void testBuildGreedySolution() {
        ProblemSolution solution = problem.buildGreedySolution();
        System.out.println(solution);
        assertEquals(200026.0, solution.getObjectiveFunction(), 0.001);
    }
    
    @Test
    public void testRebuildSolution() {
        ProblemSolution solution = problem.buildGreedySolution();
        System.out.println(solution);
        System.out.println(solution.getLinkedRouteList());
        
        List<Integer> sequence = new ArrayList<>();
        sequence.addAll(solution.getLinkedRouteList());
        Collections.swap(sequence, 0, 4);
        System.out.println("new sequence = " + sequence);
        problem.rebuildSolution(sequence, problem.getData().getRequests());
       
        System.out.println(solution);
        System.out.println(solution.getLinkedRouteList());
        assertEquals(200026.0, solution.getObjectiveFunction(), 0.001);
    }

}
