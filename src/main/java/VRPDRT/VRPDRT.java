/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

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
public class VRPDRT {
    private final Long timeWindows = (long) 3;
    private List<Request> requests = new ArrayList<>();
    private List<List<Integer>> listOfAdjacencies = new LinkedList<>();
    private List<List<Long>> distanceBetweenNodes = new LinkedList<>();
    private List<List<Long>> timeBetweenNodes = new LinkedList<>();
    private Set<Integer> Pmais = new HashSet<>();
    private Set<Integer> Pmenos = new HashSet<>();
    private Set<Integer> setOfNodes = new HashSet<>();
    private int numberOfNodes;
    private Map<Integer, List<Request>> requestsWhichBoardsInNode = new HashMap<>();
    private Map<Integer, List<Request>> requestsWhichLeavesInNode = new HashMap<>();
    private List<Integer> loadIndexList = new LinkedList<>();
    private Set<Integer> setOfVehicles = new HashSet<>();
    private List<Request> listOfNonAttendedRequests = new ArrayList<>();
    private List<Request> requestList = new ArrayList<>();
    
}
