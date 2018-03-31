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
    private Set<Integer> setOfNodes = new HashSet<>();
    private int numberOfNodes;
    private Map<Integer, List<Request>> requestsWhichBoardsInNode = new HashMap<>();
    private Map<Integer, List<Request>> requestsWhichLeavesInNode = new HashMap<>();
    private List<Integer> loadIndexList = new LinkedList<>();
    private Set<Integer> setOfVehicles = new HashSet<>();
    private List<Request> nonAttendedRequests = new ArrayList<>();
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
    private boolean feasibleRequestIsFound;
    private Set<Integer> feasibleNodes;
    private Route currentRoute;
    private double max, min;
    private RankedList rankedList;
    private String log;

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
        nonAttendedRequests.clear();
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

                rankedList.calculateNRF(feasibleNodes);
                max = Collections.max(rankedList.getValuesOfNRF());
                addBestNode(earliestTime);

                //Step 7
                landPassengers();
                boardPassengers();
                boardPassengersWithRelaxationTime();
                removesUnfeasibleRequests();
                findRequestToAttend();
                removeNonAttendeRequests();
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
        nonAttendedRequests.clear();
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

        nonAttendedRequests.clear();
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

    public void addBestNode(List<Long> earliestTime) {
        //-------------------------------------------------------------------------------------------------------------------------------------- 
        for (Map.Entry<Integer, Double> nrf : rankedList.getNodeRankingFunction().entrySet()) {
            Integer newNode = nrf.getKey();
            Double value = nrf.getValue();

            if (Objects.equals(max, value)) {
                if (lastNode == 0) {
                    for (Request request : requestsWhichBoardsInNode.get(newNode)) {
                        if (timeBetweenNodes.get(lastNode).get(newNode) <= request.getPickupTimeWindowUpper()) {
                            earliestTime.add(request.getPickupTimeWIndowLower());
                        }
                    }
                    currentTime = Math.max(Collections.min(earliestTime) - timeBetweenNodes.get(lastNode).get(newNode), 0);
                    currentRoute.setDepartureTimeFromDepot(currentTime);
                    earliestTime.clear();
                }

                currentTime += timeBetweenNodes.get(lastNode).get(newNode);

                currentRoute.addVisitedNodes(newNode);
                lastNode = currentRoute.getLastNode();
                break;
            }
        }
        rankedList.clear();
        lastNode = currentRoute.getLastNode();
        //return currentTime;
    }

    public void landPassengers() {
        List<Request> listRequestAux = new LinkedList<>();
        listRequestAux.addAll(requestsWhichLeavesInNode.get(lastNode));

        for (Request request : listRequestAux) {

            if (!requestsWhichBoardsInNode.get(request.getOrigin()).contains(request)) {
                requestsWhichLeavesInNode.get(lastNode).remove((Request) request.clone());
                requestList.remove((Request) request.clone());
                log += "ENTREGA: " + currentTime + ": " + (Request) request.clone() + " ";
                try {
                    currentRoute.leavePassenger((Request) request.clone(), currentTime);
                } catch (Exception e) {
                    //System.out.print("solucao vigente: " + S + " R problema\n");
                    System.out.println("L Atend (" + currentRoute.getRequestAttendanceList().size() + ") " + currentRoute.getRequestAttendanceList());
                    System.out.println("L Visit (" + currentRoute.getNodesVisitationList().size() + ") " + currentRoute.getNodesVisitationList());
                    System.out.println("Qik (" + currentRoute.getVehicleOccupationWhenLeavesNode().size() + ") " + currentRoute.getVehicleOccupationWhenLeavesNode());
                    System.out.println("Tempoik (" + currentRoute.getTimeListTheVehicleLeavesTheNode().size() + ") " + currentRoute.getTimeListTheVehicleLeavesTheNode());
                    System.exit(-1);
                }
                //EXTRA
                log += "Q=" + currentRoute.getActualOccupation() + " ";
            }
        }
        listRequestAux.clear();
    }

    public void boardPassengers() {
        List<Request> listRequestAux = new LinkedList<>();
        listRequestAux.addAll(requestsWhichBoardsInNode.get(lastNode));

        for (Request request : listRequestAux) {
            if (currentRoute.getActualOccupation() < vehicleCapacity && currentTime >= request.getPickupTimeWIndowLower() && currentTime <= request.getPickupTimeWindowUpper()) {
                requestsWhichBoardsInNode.get(lastNode).remove((Request) request.clone());
                log += "COLETA: " + currentTime + ": " + (Request) request.clone() + " ";
                currentRoute.boardPassenger((Request) request.clone(), currentTime);
                //EXTRA
                log += "Q =" + currentRoute.getActualOccupation() + " ";
            }
        }

        listRequestAux.clear();
    }

    public void boardPassengersWithRelaxationTime() {

        List<Request> listRequestAux = new LinkedList<>();
        listRequestAux.addAll(requestsWhichBoardsInNode.get(lastNode));

        long waitTime = timeWindows;
        long aux;

        for (Request request : listRequestAux) {
            if (currentRoute.getActualOccupation() < vehicleCapacity && currentTime + waitTime >= request.getPickupTimeWIndowLower() && currentTime + waitTime <= request.getPickupTimeWindowUpper()) {
                aux = currentTime + waitTime - request.getPickupTimeWIndowLower();
                currentTime = Math.min(currentTime + waitTime, request.getPickupTimeWIndowLower());
                waitTime = aux;
                requestsWhichBoardsInNode.get(lastNode).remove((Request) request.clone());
                log += "COLETAw: " + currentTime + ": " + (Request) request.clone() + " ";
                currentRoute.boardPassenger((Request) request.clone(), currentTime);
                log += "Q=" + currentRoute.getActualOccupation() + " ";
            }
        }
    }

    public void removesUnfeasibleRequests() {
        List<Request> listRequestAux = new LinkedList<>();
        listRequestAux.clear();
        for (Integer key : requestsWhichBoardsInNode.keySet()) {
            listRequestAux.addAll(requestsWhichBoardsInNode.get(key));
            Integer i;
            Integer n2;
            for (i = 0, n2 = listRequestAux.size(); i < n2; i++) {
                Request request = listRequestAux.get(i);
                if (currentTime > request.getPickupTimeWindowUpper()) {
                    nonAttendedRequests.add((Request) request.clone());
                    requestList.remove((Request) request.clone());
                    requestsWhichBoardsInNode.get(key).remove((Request) request.clone());
                    requestsWhichLeavesInNode.get(request.getDestination()).remove((Request) request.clone());
                }
            }
            listRequestAux.clear();
        }
    }

    public void findRequestToAttend() {
        feasibleRequestIsFound = false;
        for (int i = 1; !feasibleRequestIsFound && i < numberOfNodes; i++) {//varre todas as solicitações para encontrar se tem alguma viável
            if (i != lastNode) {

                //Procura solicitação para embarcar
                if (currentRoute.getActualOccupation() < vehicleCapacity) {//se tiver lugar, ele tenta embarcar alguem no veículo
                    for (Request request : requestsWhichBoardsInNode.get(i)) {//percorre todos os nós menos o nó que acabou de ser adicionado (por causa da restrição)
                        if (currentTime + timeBetweenNodes.get(lastNode).get(i) >= request.getPickupTimeWIndowLower() - timeWindows
                                && currentTime + timeBetweenNodes.get(lastNode).get(i) <= request.getPickupTimeWindowUpper()) {
                            feasibleRequestIsFound = true;
                            break;
                        }
                    }
                }
                //Procura solicitação para desembarcar
                if (!feasibleRequestIsFound && currentRoute.getActualOccupation() > 0) {
                    for (Request request : requestsWhichLeavesInNode.get(i)) {
                        if (!requestsWhichBoardsInNode.get(request.getOrigin()).contains(request)) {
                            feasibleRequestIsFound = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void removeNonAttendeRequests() {
        List<Request> listRequestAux = new LinkedList<>();
        if (!feasibleRequestIsFound) {
            for (Integer key : requestsWhichBoardsInNode.keySet()) {//bloco de comando que coloca as solicitações que nn embarcaram no conjunto de inviáveis (U)
                listRequestAux.addAll(requestsWhichBoardsInNode.get(key));
                Integer i, n2;
                for (i = 0, n2 = listRequestAux.size(); i < n2; i++) {
                    Request request = listRequestAux.get(i);
                    nonAttendedRequests.add((Request) request.clone());
                    requestList.remove((Request) request.clone());
                    requestsWhichBoardsInNode.get(key).remove((Request) request.clone());
                    requestsWhichLeavesInNode.get(request.getDestination()).remove((Request) request.clone());
                }
                listRequestAux.clear();
            }
        }
    }
}
