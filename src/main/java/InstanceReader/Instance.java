/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstanceReader;

/**
 *
 * @author renansantos
 */
public class Instance {

    private int numberOfRequests;
    private int requestTimeWindows;
    private Integer vehicleCapacity;
    private String instanceSize;
    private int numberOfNodes;
    private String nodesData;
    private String adjacenciesData;
    String instanceName;
    private int numberOfVehicles = numberOfRequests;

     
    
    public Instance setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
        return this;
    }

    public Instance setRequestTimeWindows(int requestTimeWindows) {
        this.requestTimeWindows = requestTimeWindows;
        return this;
    }

    public void setVehicleCapacity(Integer vehicleCapacity) {
        this.vehicleCapacity = vehicleCapacity;
    }

    public void setInstanceSize(String instanceSize) {
        this.instanceSize = instanceSize;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public void setNodesData(String nodesData) {
        this.nodesData = nodesData;
    }

    public void setAdjacenciesData(String adjacenciesData) {
        this.adjacenciesData = adjacenciesData;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }
    
    
}
