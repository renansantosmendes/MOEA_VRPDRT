/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstanceReader;

import ProblemRepresentation.ProblemSolution;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * @author renansantos
 */
public class DataOutput {
    private String algorithmName;
    private String path;
    private String fileName;
    private PrintStream streamForTxt;
    private PrintStream streamForSolutions;
    private PrintStream streamForCombinedPareto;
    private PrintStream streamForCombinedParetoObjectives;
    private PrintStream streamForCombinedParetoReducedObjectives;
    private PrintStream streamForConvergence;
    private PrintStream streamForCsv;

    public DataOutput(String algorithmName, String instanceName, int execution) {
        this.algorithmName = algorithmName;
        this.path = "AlgorithmsResults//" + algorithmName + "//" + instanceName + "//";
        this.fileName = this.algorithmName + "_execution_" + execution;
        initalizePathAndFiles();
        initalizeStreams();
    }

    public DataOutput(String algorithmName, String instanceName) {
        this.algorithmName = algorithmName;
        this.path = "AlgorithmsResults//" + algorithmName + "//" + instanceName + "//";
        this.fileName = this.algorithmName;
        initalizePathAndFiles();
        initalizeStreams();
    }

    private void initalizePathAndFiles() {
        boolean success = (new File(this.path)).mkdirs();
        if (!success) {
            //System.out.println("Folder already exists!");
        }
    }

    private void initalizeStreams() {
        try {
            streamForTxt = new PrintStream(path + "/" + fileName + ".txt");
            streamForSolutions = new PrintStream(path + "/" + fileName + "_Solutions.txt");
            streamForCombinedPareto = new PrintStream(path + "/" + fileName + "_CoombinedPareto.txt");
            streamForCombinedParetoObjectives = new PrintStream(path + "/" + fileName + "_CombinedPareto_Objectives.txt");
            streamForCombinedParetoReducedObjectives = new PrintStream(path + "/" + fileName + "_CombinedPareto_ReducedObjectives.txt");
            streamForConvergence = new PrintStream(path + "/" + fileName + "_Convergence.txt");
            //streamForCsv  = new PrintStream(path + "/" + fileName + ".csv");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    public void saveBestSolutionInTxtFile(ProblemSolution solution, int currentIteration) {
        this.streamForTxt.print(currentIteration + "\t" + solution + "\n");
        this.streamForSolutions.print(solution + "\n");
        //this.streamForConvergence.print(solution.getEvaluationFunction() + "\n");
    }

    public void saveBestSolutionFoundInTxtFile(ProblemSolution solution, int currentIteration) {
        this.streamForTxt.print(currentIteration + "\t" + solution + "\n");
        this.streamForSolutions.print(solution + "\n");
        //this.streamForConvergence.print(solution.getEvaluationFunction() + "\n");
    }

    public void saveBestSolutionFoundInTxtFile(ProblemSolution solution) {
        this.streamForCombinedPareto.print(solution + "\n");
        this.streamForCombinedParetoObjectives.print(solution.getStringWithAllNonReducedObjectives() + "\n");
        this.streamForCombinedParetoReducedObjectives.print(solution.getObjectives() + "\n");
    }
    
    public void savePopulation(List<ProblemSolution> population){
        for(ProblemSolution solution: population){
            saveBestSolutionFoundInTxtFile(solution);
        }
    }
}
