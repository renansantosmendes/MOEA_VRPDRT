/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MaOP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 *
 * @author renansantos
 */
public class WFGTest {

    @Test
    public void instancesTest() throws FileNotFoundException {

        String path = "QualificationResults//";
        String algorithm = "NSGA-II";
//        String problem = "WFG1_2";
        String problem = "DTLZ2_3";
        int reducedDimensionality = 4;
        List<NondominatedPopulation> result = new Executor()
                .withProblem(problem)
                .withAlgorithm(algorithm)
                .withMaxEvaluations(5000)
                .runSeeds(10);
//
        boolean success = (new File(path + algorithm + "//" + problem)).mkdirs();
        if (!success) {
            System.out.println("Folder already exists!");
        }
        PrintStream outPutInTxt = new PrintStream(path + algorithm + "//" + problem + "//CombinedPareto.txt");
        PrintStream outPutInCsv = new PrintStream(path + algorithm + "//" + problem + "//CombinedPareto.csv");
//        
        NondominatedPopulation combinedPareto = new NondominatedPopulation();

        for (NondominatedPopulation population : result) {
            for (Solution solution : population) {
                combinedPareto.add(solution);
            }
        }
//        
//        
        for (Solution solution : combinedPareto) {
            double[] objectives = solution.getObjectives();
            for (int i = 0; i < objectives.length; i++) {
                System.out.print(objectives[i] + " ");
                outPutInTxt.print(objectives[i] + " ");
                outPutInCsv.print(objectives[i] + ",");
            }
            System.out.println("");
            outPutInTxt.print("\n");
            outPutInCsv.print("\n");
        }
    }
}
