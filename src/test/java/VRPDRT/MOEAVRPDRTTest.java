/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author renansantos
 */
public class MOEAVRPDRTTest {

    MOEAVRPDRT problem;

    public MOEAVRPDRTTest() {
        problem = new MOEAVRPDRT()
                .setNumberOfObjectives(9)
                .setNumberOfVariables(1);
    }

    @Test
    public void mainTest() {
        for (int i = 0; i < 10; i++) {
            problem.evaluate(problem.newSolution());
        }
    }

}
