import java.util.*;
import java.util.Scanner;

/**
 * A simple Network class to build a network
 *
 *
 */
public class Network {


    /**
     * computerConnections represents list of all inter-computer edges
     * Each edge is an Integer[] of size 3
     * edge[0] = source computer index ( Not IP, it's the Index !)
     * edge[1] = destination computer index ( Not IP, it's the Index !)
     * edge[2] = latency/edge weight
     */
    private LinkedList<Integer[]> computerConnections;
    /**
     * Adjacency List representing computer graph
     */
    private LinkedList<LinkedList<Integer>> computerGraph;
    /**
     * LinkedList of clusters where each cluster is represented as a LinkedList of computer IP addresses
     */
    private LinkedList<LinkedList<Integer>> cluster;
    /**
     * Adjacency List representing router graph
     */
    private LinkedList<LinkedList<Integer[]>> routerGraph;

    Scanner s; // Scanner to read Stdin input

    //Add your own field variables as required
    ArrayList<Integer> list;
    ArrayList<Router> router_list;

    /**
     * Default Network constructor, initializes data structures
     * @param s Provided Scanner to be used throughout program
     */
    public Network(Scanner s) {
        //TODO
        this.s = s;
        computerConnections = new LinkedList<Integer[]>();
        computerGraph = new LinkedList<LinkedList<Integer>>();
        cluster = new LinkedList<LinkedList<Integer>>();
        routerGraph = new LinkedList<LinkedList<Integer[]>>();

    }

    /**
     * Method to parse Stdin input and generate inter-computer edges
     * Edges are stored within computerConnections
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of comp 1] [IP address of comp 2] [latency of connection]
     */
    public void buildComputerNetwork() {
        int nos_edges = s.nextInt();
        list = new ArrayList<Integer>();

        for (int i = 0; i < nos_edges; i++) {
            int first_ip = 0;
            int second_ip = 0;
            int latencey = 0;

            Integer[] unique_index_latencey = new Integer[3];

            first_ip = s.nextInt();
            second_ip = s.nextInt();
            latencey = s.nextInt();


            if (list.contains(first_ip) == false) {
                list.add(first_ip);
            }

            if (list.contains(second_ip) == false) {
                list.add(second_ip);
            }


            unique_index_latencey[0] = list.indexOf(first_ip);
            unique_index_latencey[1] = list.indexOf(second_ip);
            unique_index_latencey[2] = latencey;
            computerConnections.add(unique_index_latencey);

        }
    }



    /**
     * Method to generate clusters from computer graph
     * Throws Exception when cannot create required clusters
     * @param k number of clusters to be created
     */
    public void buildCluster(int k) throws Exception {

        // Sort the computerConnections according to their latencies.
        Collections.sort(computerConnections, new sort_by_Latencey());

        // Make a unionFind class object passing size of unique indices which we read before as the parameter.
        UnionFind union_object = new UnionFind(list.size());

        // Initialize the computerGraph
        for (int i = 0; i < list.size(); i++) {
            computerGraph.add(new LinkedList<Integer>());
        }

        // Initially total number of unions will be equal to number of vertices therefore size of unique indices.
        int nos_unions = list.size();
        for (int i = 0;  i < computerConnections.size(); i++) {

            // Total nos of edges initially is the size of computerConnections as we stored all the edges in it initially.
            Integer[] edges = computerConnections.get(i);

            /*
             * If for each edge if the edge is not connected and total nos of unions is still greater than k,
             * we do the code below-
             * Do union of the edges and add them to adjancey list.
             */

            if ((!union_object.connected(edges[0], edges[1])) && (nos_unions > k)) {
                union_object.union(edges[0], edges[1]);
                nos_unions--;
                computerGraph.get(edges[0]).add(edges[1]);
                computerGraph.get(edges[1]).add(edges[0]);
            }
        }

        // Initialize cluster
        for (int i = 0; i < k; i++) {
            cluster.add(new LinkedList<Integer>());
        }

        // Adding elements to cluster.
        ArrayList<Integer> clusters = new ArrayList<Integer>();
        for (int i = 0; i < computerGraph.size(); i++) {
            if (clusters.contains(union_object.find(i)) == false) {
                clusters.add(union_object.find(i));
            }
            if (clusters.contains(union_object.find(i)) == true) {
                int max = clusters.indexOf(union_object.find(i));
                if (cluster.get(max) == null) {
                    cluster.set(max, new LinkedList<>());
                }
                cluster.get(max).add(list.get(i));
            }
        }

        router_list = new ArrayList<>();
        for (int i = 0; i < cluster.size(); i++) {
            Router new_router = new Router();
            new_router.setIPPrefix(Collections.max(cluster.get(i)));
            for (int j = 0; j < cluster.get(i).size(); j++) {
                new_router.addComp(cluster.get(i).get(j));

            }
            router_list.add(new_router);
        }
    }

    /**
     * Method to parse Stdin input and generate inter-router edges
     * Graph is stored within routerGraph as an adjacency list
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of Router 1] [IP address of Router 2] [latency of connection]
     */
    public void connectCluster() {
        int nos_router_edges = s.nextInt();

        for (int i = 0; i < cluster.size(); i++) {
            routerGraph.add(new LinkedList<>());
        }

        for (int i = 0; i < nos_router_edges; i++) {
            int first_router_IPprefix = s.nextInt();
            int second_router_IPprefix = s.nextInt();
            int router_latencey = s.nextInt();

            Integer[] links_one = new Integer[2];
            Integer[] links_two = new Integer[2];
            links_one[1] = router_latencey;
            links_two[1] = router_latencey;

            Router router_one = new Router();
            int unique_index_src = 0;
            router_one.setIPPrefix(first_router_IPprefix);

            for (int j = 0; j < router_list.size(); j++) {
                if (router_list.get(j).getIPPrefix() == router_one.getIPPrefix()) {

                    unique_index_src = j;
                    links_two[0] = unique_index_src;
                }
            }

            Router router_two = new Router();
            router_two.setIPPrefix(second_router_IPprefix);
            int unique_index_dest = 0;
            for (int j = 0; j < router_list.size(); j++) {
                if (router_list.get(j).getIPPrefix() == router_two.getIPPrefix()) {

                    unique_index_dest = j;
                    links_one[0] = unique_index_dest;
                }
            }
            routerGraph.get(unique_index_src).add(links_one);
            routerGraph.get(unique_index_dest).add(links_two);

        }
    }

    /**
     * Method to take a traversal request and find the shortest path for that traversal
     * Traversal request passed in through parameter test
     * Format of Request => [IP address of Source Router].[IP address of Source Computer] [IP address of Destination Router].[IP address of Destination Computer]
     * Eg. 123.456 128.192
     *  123 = IP address of Source Router
     *  456 = IP address of Source Computer
     *  128 = IP address of Destination Router
     *  192 = IP address of Destination Computer
     * @param test String containing traversal input
     * @return Shortest traversal distance between Source and Destination Computer
     */
    public int traversNetwork(String test) {
        int source_router_ip = 0;
        int start = -1;
        int end = -1;
        String[] src_dest = test.split(" ");
        LinkedList<Integer> ips_router = new LinkedList<>();

        for (int i = 0; i < src_dest.length; i++) {
            String[] news = src_dest[i].split("\\.");
            int[] nos = new int[news.length];
            for (int j = 0; j < news.length; j++) {
                nos[j] = Integer.parseInt(news[j]);
                source_router_ip = nos[0];
            }
            ips_router.add(source_router_ip);
        }
        System.out.println("    ip rouer size = " + ips_router.size());

        for (int i = 0; i < router_list.size(); i++) {
            if (router_list.get(i).getIPPrefix() == ips_router.get(0)) {
                System.out.print(" First = " + router_list.get(i).getIPPrefix());
                System.out.println(" 0 router = " + ips_router.get(0));
                start = i;
            }
            if (router_list.get(i).getIPPrefix() == ips_router.get(1)) {
                System.out.print(" Second = " + router_list.get(i).getIPPrefix());
                System.out.println(" 1 router = " + ips_router.get(1));
                end = i;
            }
        }

        if ((start == -1) || (end == -1)) {
            return -1;
        }

        int vertices = routerGraph.size();
        List<List<Edge> > adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            List<Edge> item = new ArrayList<>();
            adj.add(item);
        }

        for (int i = 0; i < routerGraph.size(); i++) {
            for (int j = 0; j <routerGraph.get(i).size(); j++) {
                Integer[] edges;
                edges = routerGraph.get(i).get(j);
                Edge new_vertex = new Edge();
                new_vertex.vertex = edges[0];
                new_vertex.weight = edges[1];

                adj.get(i).add(new_vertex);
            }
        }

        dijkstras_priority_queue dpq = new dijkstras_priority_queue(vertices);
        dpq.dijkstra(adj, start);

        return dpq.dist[end];

    }

    private void printGraph(LinkedList< LinkedList<Integer[]>> graph){
        for (var i:graph) {
            for (var j: i){
                System.out.print(j[0]+" "+j[1]);
            }
            System.out.println();
        }
    }

    public LinkedList<Integer[]> getComputerConnections() {
        return computerConnections;
    }

    public LinkedList<LinkedList<Integer>> getComputerGraph() {
        return computerGraph;
    }

    public LinkedList<LinkedList<Integer>> getCluster() {
        return cluster;
    }

    public LinkedList<LinkedList<Integer[]>> getRouterGraph() {
        return routerGraph;
    }

}

class sort_by_Latencey implements Comparator<Integer[]> {
    public int compare(Integer[] first, Integer[] second) {
        return (first[first.length - 1] - second[second.length - 1]);
    }
}

class dijkstras_priority_queue {
    int dist[];
    int vertices;
    List<List<Edge> > adj;
    Set<Integer> settled;
    PriorityQueue<Edge> pq;


    public dijkstras_priority_queue(int vertices)
    {
        this.vertices = vertices;
        dist = new int[vertices];
        settled = new HashSet<Integer>();
        pq = new PriorityQueue<Edge>(vertices, new Edge());
    }

    public void dijkstra(List<List<Edge> > adj, int source)
    {
        this.adj = adj;
        for (int i = 0; i < vertices; i++)
            dist[i] = Integer.MAX_VALUE;
        pq.add(new Edge(source, 0));
        dist[source] = 0;

        while (settled.size() != vertices) {
            int u = pq.remove().vertex;
            settled.add(u);
            int oldEdgeDistance = -1;
            int newEdgeDistance = -1;

            for (int i = 0; i < adj.get(u).size(); i++) {
                Edge vertex = adj.get(u).get(i);

                if (!settled.contains(vertex.vertex)) {
                    oldEdgeDistance = vertex.weight;
                    newEdgeDistance = dist[u] + oldEdgeDistance;

                    if (newEdgeDistance < dist[vertex.vertex]) {
                        dist[vertex.vertex] = newEdgeDistance;
                    }

                    pq.add(new Edge(vertex.vertex, dist[vertex.vertex]));
                }
            }
        }
    }
}

class Edge implements Comparator<Edge> {
    public int vertex;
    public int weight;

    public Edge()
    {
    }

    public Edge(int node, int weight)
    {
        this.vertex = node;
        this.weight = weight;
    }

    public int compare(Edge vertex_one, Edge vertex_two)
    {
        if (vertex_one.weight < vertex_two.weight) {
            return -1;
        }
        if (vertex_one.weight > vertex_two.weight) {
            return 1;
        }
        return 0;
    }
}
