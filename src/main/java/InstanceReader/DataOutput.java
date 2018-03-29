/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstanceReader;

import ProblemRepresentation.ProblemSolution;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author renansantos
 */
public class DataOutput {
    private String algorithmName;
    private String path = "AlgorithmsResults//";
    private String fileName;
    private PrintStream streamForTxt  = new PrintStream(path + "/" + fileName + ".txt");
    private PrintStream streamForCsv  = new PrintStream(path + "/" + fileName + ".csv");

    public DataOutput(String algorithmName) throws FileNotFoundException {
        this.algorithmName = algorithmName;
        this.fileName = this.algorithmName + ".txt";
    }
    
    public void saveBestSolutionFoundInTxtFile(ProblemSolution solution, int currentIteration){
        this.streamForTxt.print(currentIteration + "\t" + solution);
    }
    
     public void saveBestSolutionFoundInCsvFile(ProblemSolution solution, int currentIteration){
        
    }
}
