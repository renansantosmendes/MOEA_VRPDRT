/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.Instance;
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
        
        problem = new VRPDRT(instance, path);
    }

    
    @Test
    public void testBuildGreedySolution() {
        problem.buildGreedySolution(0.25, 0.25, 0.25, 0.25);
    }

    
}
