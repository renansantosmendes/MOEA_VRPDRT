/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VRPDRT;

import InstanceReader.*;
import ProblemRepresentation.*;
import java.io.IOException;
import java.util.*;
import jxl.read.biff.BiffException;

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
    private ProblemData data;
    private Instance instance;
    private String instanceName;
    private String nodesInstanceName;
    private String adjacenciesInstanceName;
    private Integer numberOfVehicles;
    private Integer vehicleCapacity;
    private String excelDataFilesPath;
    private ProblemSolution solution = new ProblemSolution();
    private Long currentTime;
    private Integer lastNode;
    private List<Integer> loadIndex;
    private boolean feasibleNodeIsFound;
    private double max, min;

    public VRPDRT(Instance instance) {
        this.loadIndexList = new LinkedList<>();
        this.instance = instance;
        this.instanceName = instance.getInstanceName();
        this.nodesInstanceName = instance.getNodesData();
        this.adjacenciesInstanceName = instance.getAdjacenciesData();
        this.numberOfVehicles = instance.getNumberOfVehicles();
        this.vehicleCapacity = instance.getVehicleCapacity();
        //this.readInstance();
    }

    public VRPDRT(Instance instance, String excelDataFilesPath) {
        this.loadIndexList = new LinkedList<>();
        this.instance = instance;
        this.instanceName = instance.getInstanceName();
        this.nodesInstanceName = instance.getNodesData();
        this.adjacenciesInstanceName = instance.getAdjacenciesData();
        this.numberOfVehicles = instance.getNumberOfVehicles();
        this.vehicleCapacity = instance.getVehicleCapacity();
        this.excelDataFilesPath = excelDataFilesPath;
        this.readExcelInstance();
    }

    public void readExcelInstance() {
        try {
            data = new ProblemData(instance, this.excelDataFilesPath);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (BiffException ex) {
            ex.printStackTrace();
        }
    }

    public ProblemData getData() {
        return data;
    }

    public void setData(ProblemData data) {
        this.data = data;
    }

    public ProblemSolution getSolution() {
        return solution;
    }

    public void setSolution(ProblemSolution solution) {
        this.solution.setSolution(solution);
    }

    public ProblemSolution greedyConstructive(Double alphaD, Double alphaP, Double alphaV, Double alphaT) {

        requestList.clear();
        listOfNonAttendedRequests.clear();
        requestList.addAll(requests);

        //Step 1
        ProblemSolution solution = new ProblemSolution();
        String log = "";

        int currentVehicle;
        Map<Integer, Double> costRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> numberOfPassengersRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> deliveryTimeWindowRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> timeWindowRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> nodeRankingFunction = new HashMap<>(numberOfNodes);

        Iterator<Integer> vehicleIterator = setOfVehicles.iterator();
        listOfNonAttendedRequests.clear();
        while (hasRequestToAttend() && hasAvaibleVehicle(vehicleIterator)) {

            separateOriginFromDestination();

            //Step 2
            Route route = new Route();
            currentVehicle = vehicleIterator.next();
            log += "\tGROute " + (currentVehicle + 1) + " ";

            //Step 3
            route.addVisitedNodes(0);
            currentTime = (long) 0;
           
            lastNode = route.getLastNode();

            while (hasRequestToAttend()) {
                feasibleNodeIsFound = false;
                loadIndex.clear();
                for (int i = 0; i < numberOfNodes; i++) {
                    loadIndex.add(requestsWhichBoardsInNode.get(i).size() - requestsWhichLeavesInNode.get(i).size());
                }

                //Step 4
                Set<Integer> feasibleNode = new HashSet<>();
                List<Long> earliestTime = new ArrayList<>();

                findFeasibleNodes(numberOfNodes, lastNode, feasibleNodeIsFound, vehicleCapacity, route,
                        requestsWhichBoardsInNode, requestsWhichLeavesInNode, feasibleNode, timeBetweenNodes,
                        currentTime, timeWindows);

//                //System.out.println("FEASIBLE NODES = "+ FeasibleNode);			
//                if (feasibleNode.size() > 1) {
//                    //Step 4.1
//                    CalculaCRL(feasibleNode, costRankList, distanceBetweenNodes, lastNode);
//                    //Step 4.2
//                    CalculaNRL(feasibleNode, numberOfPassengersRankList, loadIndex, lastNode);
//                    //Step 4.3
//                    CalculaDRL(feasibleNode, deliveryTimeWindowRankList, requestsWhichLeavesInNode, lastNode,
//                            timeBetweenNodes, earliestTime);
//                    //Step 4.4
//                    CalculaTRL(feasibleNode, timeWindowRankList, requestsWhichBoardsInNode, lastNode, timeBetweenNodes,
//                            earliestTime);
//                } else {
//                    //Step 4.1
//                    CalculaListaSemNosViaveis(costRankList, feasibleNode);
//                    //Step 4.2
//                    CalculaListaSemNosViaveis(numberOfPassengersRankList, feasibleNode);
//                    //Step 4.3
//                    CalculaListaSemNosViaveis(deliveryTimeWindowRankList, feasibleNode);
//                    //Step 4.4
//                    CalculaListaSemNosViaveis(timeWindowRankList, feasibleNode);
//                }
//
//                //Step 5
//                CalculaNRF(nodeRankingFunction, costRankList, numberOfPassengersRankList, deliveryTimeWindowRankList,
//                        timeWindowRankList, alphaD, alphaP, alphaV, alphaT, feasibleNode);
//
//                //Step 6              
//                //System.out.println("Tamanho da NRF = " + NRF.size());              
//                max = Collections.max(nodeRankingFunction.values());
//
//                currentTime = AdicionaNo(nodeRankingFunction, costRankList, numberOfPassengersRankList,
//                        deliveryTimeWindowRankList, timeWindowRankList, max, lastNode, requestsWichBoardsInNode,
//                        timeBetweenNodes, earliestTime, currentTime, route);
//
//                lastNode = route.getLastNode();
//
//                //Step 7
//                //RETIRAR A LINHA DE BAIXO DEPOIS - inicialização de listRequestAux
//                List<Request> listRequestAux = new LinkedList<>();
//                //Desembarca as solicitações no nó 
//                Desembarca(requestsWhichBoardsInNode, requestsWhichLeavesInNode, lastNode, currentTime, requestList,
//                        listRequestAux, route, log);
//                //Embarca as solicitações sem tempo de espera
//                Embarca(requestsWhichBoardsInNode, lastNode, currentTime, requestList, listRequestAux, route, log, vehicleCapacity);
//                //Embarca agora as solicitações onde o veículo precisar esperar e guarda atualiza o tempo (currentTime)                               
//                currentTime = EmbarcaRelaxacao(requestsWhichBoardsInNode, lastNode, currentTime, requestList,
//                        listRequestAux, route, log, vehicleCapacity, timeWindows);
//
//                //---------- Trata as solicitações inviáveis -----------
//                RetiraSolicitacoesInviaveis(requestsWhichBoardsInNode, requestsWhichLeavesInNode, listRequestAux,
//                        currentTime, requestList, listOfNonAttendedRequests);
//                feasibleNodeIsFound = ProcuraSolicitacaoParaAtender(route, vehicleCapacity, requestsWhichBoardsInNode,
//                        requestsWhichLeavesInNode, currentTime, numberOfNodes, timeBetweenNodes, lastNode, timeWindows,
//                        feasibleNodeIsFound);
//                RetiraSolicitacaoNaoSeraAtendida(feasibleNodeIsFound, requestsWhichBoardsInNode, requestsWhichLeavesInNode,
//                        listRequestAux, currentTime, requestList, listOfNonAttendedRequests);
//
//                //Step 8
//                currentTime = FinalizaRota(requestList, route, currentTime, lastNode, timeBetweenNodes, solution);
            }
//
//            //Step 9
//            AnaliseSolicitacoesViaveisEmU(listOfNonAttendedRequests, requestList, vehicleIterator, timeBetweenNodes);
        }
//
//        solution.setNonAttendedRequestsList(listOfNonAttendedRequests);
//        evaluateSolution(solution, distanceBetweenNodes, vehicleCapacity, requests);
//        solution.setLogger(log);
//        solution.linkTheRoutes();

        return solution;
    }

    private static boolean hasAvaibleVehicle(Iterator<Integer> vehicleIterator) {
        return vehicleIterator.hasNext();
    }

    private boolean hasRequestToAttend() {
        return !requestList.isEmpty();
    }

    public void separateOriginFromDestination() {

        listOfNonAttendedRequests.clear();
        requestsWhichBoardsInNode.clear();
        requestsWhichLeavesInNode.clear();
        List<Request> origin = new LinkedList<Request>();
        List<Request> destination = new LinkedList<Request>();

        for (int j = 0; j < numberOfNodes; j++) {
            for (Request request : requestList) {
                if ((request.getOrigin() == j || request.getDestination() == j)) {
                    if (request.getOrigin() == j) {
                        origin.add((Request) request.clone());
                    } else {
                        destination.add((Request) request.clone());
                    }
                }
            }
            requestsWhichBoardsInNode.put(j, new LinkedList<Request>(origin));
            requestsWhichLeavesInNode.put(j, new LinkedList<Request>(destination));
            origin.clear();
            destination.clear();
        }
    }
    
    public void findFeasibleNodes( Integer lastNode, boolean feasibleNodeIsFound, Route route,  Set<Integer> feasibleNode, List<List<Long>> timeBetweenNodes,
            Long currentTime, Long timeWindows) {

        for (int i = 1; i < numberOfNodes; i++) {
            feasibilityConstraints(i, lastNode, feasibleNodeIsFound, route, vehicleCapacity, requestsWhichBoardsInNode,
                    requestsWhichLeavesInNode, feasibleNode, timeBetweenNodes, currentTime, timeWindows);
        }
    }
    
    public void feasibilityConstraints(Integer currentNode, Integer lastNode, boolean feasibleNodeFound, Route currentRoute, Integer Qmax, Map<Integer, List<Request>> requestsWhichBoardsInNode,
            Map<Integer, List<Request>> requestsWhichLeavesInNode, Set<Integer> FeasibleNode, List<List<Long>> d, Long currentTime, Long timeWindows) {
        if (currentNode != lastNode) {
            feasibleNodeFound = false;
            if (currentRoute.getActualOccupation() < Qmax) {
                for (Request request : requestsWhichBoardsInNode.get(currentNode)) {//retorna uma lista com as requisições que embarcam em i
                    if (lastNode == 0 && d.get(lastNode).get(currentNode) <= request.getPickupTimeWindowUpper()) { //d.get(lastNode).get(i) � o tempo de chegar de lastNode ate o no i?
                        FeasibleNode.add(currentNode);
                        feasibleNodeFound = true;
                        break;
                    }

                    if (!feasibleNodeFound && currentTime + d.get(lastNode).get(currentNode) >= request.getPickupTimeWIndowLower() - timeWindows
                            && currentTime + d.get(lastNode).get(currentNode) <= request.getPickupTimeWindowUpper()) {
                        FeasibleNode.add(currentNode);
                        feasibleNodeFound = true;
                        break;
                    }
                }
            }

          
            if (!feasibleNodeFound && currentRoute.getActualOccupation() > 0) {
                for (Request request : requestsWhichLeavesInNode.get(currentNode)) {
                    if (!requestsWhichBoardsInNode.get(request.getOrigin()).contains(request)) {
                        FeasibleNode.add(currentNode);
                        break;
                    }
                }
            }
        }
    }
}
