/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the edito
r.
 */
package VRPDRT;

import InstanceReader.Instance;
import ProblemRepresentation.RankedList;
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
        rankedList.setAlphaD(0.25)
                .setAlphaP(0.25)
                .setAlphaT(0.25)
                .setAlphaV(0.25);
        
        problem = new VRPDRT(instance, path,rankedList);
    }

    @Test
    public void testBuildGreedySolution() {
        problem.buildGreedySolution();
    }

}
