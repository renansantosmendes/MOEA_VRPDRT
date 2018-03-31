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
    private Set<Integer> feasibleNodes;
    private Route currentRoute;
    private double max, min;
    private RankedList rankedList;

    public VRPDRT(Instance instance) {
        this.loadIndexList = new LinkedList<>();
        this.instance = instance;
        this.instanceName = instance.getInstanceName();
        this.nodesInstanceName = instance.getNodesData();
        this.adjacenciesInstanceName = instance.getAdjacenciesData();
        this.numberOfVehicles = instance.getNumberOfVehicles();
        this.vehicleCapacity = instance.getVehicleCapacity();
        rankedList = new RankedList(numberOfNodes);
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
        rankedList = new RankedList(numberOfNodes);
    }

    public VRPDRT(Instance instance, String excelDataFilesPath, RankedList rankedList) {
        this.loadIndexList = new LinkedList<>();
        this.instance = instance;
        this.instanceName = instance.getInstanceName();
        this.nodesInstanceName = instance.getNodesData();
        this.adjacenciesInstanceName = instance.getAdjacenciesData();
        this.numberOfVehicles = instance.getNumberOfVehicles();
        this.vehicleCapacity = instance.getVehicleCapacity();
        this.excelDataFilesPath = excelDataFilesPath;
        this.readExcelInstance();
        this.rankedList = rankedList;
    }

    public void readExcelInstance() {
        try {
            data = new ProblemData(instance, this.excelDataFilesPath);
            requests.addAll(data.getRequests());
            numberOfNodes = data.getNumberOfNodes();
            timeBetweenNodes.addAll(data.getTimeBetweenNodes());
            distanceBetweenNodes.addAll(data.getDistanceBetweenNodes());
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

    public ProblemSolution buildGreedySolution() {

        initializeData();
        ProblemSolution solution = new ProblemSolution();
        String log = "";

        int currentVehicle;
        rankedList.initialize();

        Iterator<Integer> vehicleIterator = setOfVehicles.iterator();
        listOfNonAttendedRequests.clear();
        while (hasRequestToAttend() && hasAvaibleVehicle(vehicleIterator)) {

            separateOriginFromDestination();

            //Step 2
            currentRoute = new Route();
            currentVehicle = vehicleIterator.next();
            log += "\tGROute " + (currentVehicle + 1) + " ";

            //Step 3
            currentRoute.addVisitedNodes(0);
            currentTime = (long) 0;

            lastNode = currentRoute.getLastNode();

            while (hasRequestToAttend()) {
                feasibleNodeIsFound = false;
                calculateLoadIndex();

                //Step 4
                feasibleNodes = new HashSet<>();
                List<Long> earliestTime = new ArrayList<>();

                findFeasibleNodes();

                System.out.println("FEASIBLE NODES = " + feasibleNodes);
                if (hasFeasibleNode()) {
                    rankedList.calculateCRL(feasibleNodes, distanceBetweenNodes, lastNode)
                            .calculateNRL(feasibleNodes, loadIndex, lastNode)
                            .calculateDRL(feasibleNodes, requestsWhichLeavesInNode, lastNode, timeBetweenNodes, earliestTime)
                            .calculateTRL(feasibleNodes, requestsWhichBoardsInNode, lastNode, timeBetweenNodes, earliestTime);
                } else {
                    rankedList.calculateListWithoutFeasibleNodes(feasibleNodes);
                }
//
                rankedList.calculateNRF(feasibleNodes);
                max = Collections.max(rankedList.getValuesOfNRF());
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

    private boolean hasFeasibleNode() {
        return feasibleNodes.size() > 1;
    }

    private void calculateLoadIndex() {
        loadIndex.clear();
        for (int i = 0; i < numberOfNodes; i++) {
            loadIndex.add(requestsWhichBoardsInNode.get(i).size() - requestsWhichLeavesInNode.get(i).size());
        }
    }

    private void initializeData() {
        requestList.clear();
        listOfNonAttendedRequests.clear();
        requestList.addAll(requests);
        loadIndex = new ArrayList<>();
        initializeFleetOfVehicles();
    }

    private static boolean hasAvaibleVehicle(Iterator<Integer> vehicleIterator) {
        return vehicleIterator.hasNext();
    }

    private boolean hasRequestToAttend() {
        return !requestList.isEmpty();
    }

    public void initializeFleetOfVehicles() {
        for (int i = 0; i < numberOfVehicles; i++) {
            setOfVehicles.add(i);
        }
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

    public void findFeasibleNodes() {
        for (int i = 1; i < numberOfNodes; i++) {
            evaluateFeasibilityForNode(i);
        }
    }

    public void evaluateFeasibilityForNode(Integer currentNode) {
        if (currentNode != lastNode) {
            feasibleNodeIsFound = false;
            if (currentRoute.getActualOccupation() < vehicleCapacity) {
                for (Request request : requestsWhichBoardsInNode.get(currentNode)) {//retorna uma lista com as requisições que embarcam em i
                    if (lastNode == 0 && timeBetweenNodes.get(lastNode).get(currentNode) <= request.getPickupTimeWindowUpper()) { //d.get(lastNode).get(i) � o tempo de chegar de lastNode ate o no i?
                        feasibleNodes.add(currentNode);
                        feasibleNodeIsFound = true;
                        break;
                    }

                    if (!feasibleNodeIsFound && currentTime + timeBetweenNodes.get(lastNode).get(currentNode) >= request.getPickupTimeWIndowLower() - timeWindows
                            && currentTime + timeBetweenNodes.get(lastNode).get(currentNode) <= request.getPickupTimeWindowUpper()) {
                        feasibleNodes.add(currentNode);
                        feasibleNodeIsFound = true;
                        break;
                    }
                }
            }

            if (!feasibleNodeIsFound && currentRoute.getActualOccupation() > 0) {
                for (Request request : requestsWhichLeavesInNode.get(currentNode)) {
                    if (!requestsWhichBoardsInNode.get(request.getOrigin()).contains(request)) {
                        feasibleNodes.add(currentNode);
                        break;
                    }
                }
            }
        }
    }
}
