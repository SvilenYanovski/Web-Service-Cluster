## Solution

Implemented Load Balancer application to subsribe and unsubscribe server nodes from the node cluster.

The communication between the nodes and the LB is done via HTTP.

The Workflow for registration is as follows:
1. Constructs Node URL
2. Checks if there are existing Server Nodes
3. Retrieves any data from the existing Nodes
4. Saves the data to the new Node
5. Adds the Node URL into the Cluster list
 
For unsubscribing a Node from the Cluster we simply remove it from the Cluster list.

Workflow for data synchronization:
1. When a new KeyValue Pair is created the Load Balancer sends create call to all registered Server Nodes with the same KV Pair.
The calls are being executed asynchronous via `ExecutorService` with maximum allowable thread number being taken from the 
`application.properties` config. The same could be achieved by subscribing all Server Nodes to a `Topic` from a Messaging Broker like 
`ActiveMQ`.
2. When a KeyValue Pair is being deleted we do the same with async calling the `Delete` function for all Server Nodes.

Workflow for resolving merge conflicts:
1. When a `Create` or `Delete` call is being executed it checks if this KV Pair Key is currently in use by another Server Node. If yes 
we use very simple approach with Time Synchronization to wait untill the modification is completed.
2. The Server Node locks the key.
3. The Server Node releases the key on completion.
4. THe Server Node broadcasts modification on the cluster.

Another way to achieve this is by `Optimistic Locking` on DB level. There we should
implement versioning and every time we try to modify a entity/row we will check if the 
current version is lower than the DB version. In that case we abort the modification.

### How to Use

1. Start the Load Balancer (via the bat file, docker-compose or with the command `mvn spring-boot:run`)
2. Go to `localhost:9000`
3. Open `Register Controller` tab in the browser
4. Go to `/register` API endpoint and add Parameters `url` = `localhost` and `port` = 8080
5. After execution you will see the following logs:
`
    ...INFO 20596 ...RegisterController     : Trying to register KV Server Node with URL: http://localhost:8080
    ...INFO 20596 ...ClusterRegistrationServiceImpl : Started Registering Server Node URL: http://localhost:8080
    ...INFO 20596 ...ClusterRegistrationServiceImpl : First Server Node - no data for synchronization.
    ...INFO 20596 ...ClusterRegistrationServiceImpl : Completed Registering Server Node URL: http://localhost:8080
`

6. Add one more node (ensure you have running new instance of the Server Node at another port) and
verify the logs:
`
    ...INFO 29404 ...RegisterController     : Trying to register KV Server Node with URL: localhost:8081
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Started Registering Server Node URL: http://localhost:8081
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8080
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved existing KVPairs for synchronization: 0
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Sync completed - 0 rows of data, 0 errors.
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Completed Registering Server Node URL: http://localhost:8081
`
7. Verify the two Nodes are being listed in `/getAllServerNodes` API endpoint:
`
    [
      "http://localhost:8080",
      "http://localhost:8081"
    ]
`
8. Check if the Load Balancer is working fine by calling the `/getServerNode` API endpoint:
`
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8081
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8080
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8081
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8080
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8081
    ...INFO 29404 ...ClusterRegistrationServiceImpl : Retrieved Node URL: http://localhost:8080
    ...
`



