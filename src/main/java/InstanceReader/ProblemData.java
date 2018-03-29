/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstanceReader;

import ProblemRepresentation.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author renansantos
 */
public class ProblemData {
    private int numberOfNodes;
    private List<Integer> nodes;
    private List<Request> requests;
    private List<Request> instanceRequests = new ArrayList<>();
    private List<List<Integer>> listOfAdjacencies = new LinkedList<>();
    private List<List<Long>> distanceBetweenNodes = new LinkedList<>();
    private List<List<Long>> timeBetweenNodes = new LinkedList<>();
    private long[][] distance;
    private String instanceName;
    private String nodesInstanceName;
    private String adjacenciesInstanceName;
    
    private int numberOfVehicles;
    private int vehicleCapacity;
    private Instance instance;
    private String excelDataFilesPath;
    
    

    
    
    
    
}
