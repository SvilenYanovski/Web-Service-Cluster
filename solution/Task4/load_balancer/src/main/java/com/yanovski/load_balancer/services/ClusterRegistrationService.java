package com.yanovski.load_balancer.services;

import java.util.List;

/**
 * Registration process of Server Nodes
 *
 * @author SV
 */
public interface ClusterRegistrationService {

    /**
     * Registers a Server Node into a cluster
     *
     * Workflow:
     * 1. Constructs Node URL
     * 2. Checks if there are existing Server Nodes
     * 2.1 retrieves any data from the existing Nodes
     * 2.2 saves the data to the new Node
     * 3. Adds the Node URL into the Cluster list
     *
     * @param url Server Node URL
     * @param port Server Node port
     */
    void registerServiceNode(String url, String port);

    /**
     * Remove Server Node from the cluster
     * @param url
     * @param port
     */
    void removeServiceNode(String url, String port);

    /**
     * Gets the next available Server Node.
     * The method automatically balances the load between all existing Nodes from the Cluster.
     *
     * @return Server Node URL with port mapped
     */
    String getAvailableNode();

    /**
     * Gets a Read Only list of the existing Cluster
     *
     * @return Cluster List
     */
    List<String> getAllRegisteredNodes();
}
