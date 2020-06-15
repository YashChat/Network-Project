import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class NetworkTestPublic {

    class Solution {

        private LinkedList<Integer[]> computerConnections;
        private LinkedList<LinkedList<Integer>> computerGraph;
        private LinkedList<LinkedList<Integer>> cluster;
        private LinkedList<LinkedList<Integer[]>> routerGraph;

        Solution(){

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

    /*
     * Test BasicPublic Graph
     */
    @Test
    public void testBasic() {
        System.out.println("Testing Basic Public Graph...");
        String path = "//Users//yashchaturvedi//IdeaProjects//Network//src//Basic//";
        testGraph(path,"Basic", 5);
    }

    /*
     * Test IntermediatePublic Graph
     */
    @Test
    public void testIntermediate() {
        System.out.println("Testing Intermediate Public Graph...");
        String path = "//Users//yashchaturvedi//IdeaProjects//Network//src//Intermediate//";
        testGraph(path, "Intermediate", 9);
    }

    /*
     * Test AdvancedPublic Graph
     */
    @Test
    public void testAdvanced() {
        System.out.println("Testing Advanced Public Graph...");
        String path = "//Users//yashchaturvedi//IdeaProjects//Network//src//Advanced//";
        testGraph(path,"Advanced", 15);
    }


    private void testGraph(String path, String filename, int clusterNum) {
        Scanner readerSolution = null;
        Scanner readerTest = null;


        try {
            readerSolution = new Scanner(new File(path + filename+"PublicSol.txt"));
            readerTest = new Scanner(new File(path + filename+"Public.txt"));
        } catch (IOException e) {
            System.out.println("Reading Oops");
        }


        InputStream stdin = System.in;
        try {

            Solution solution = new Solution();
            Network test = new Network(readerTest);


            readerSolution.nextLine();
            solution.computerConnections = new LinkedList<>();
            while(true){
                String[] in  = readerSolution.nextLine().split("\\{");

                if(in[0].equals("")) break;

                in[1] = in[1].substring(0,in[1].length()-1);
                in = in[1].split(",");

                Integer[] edge = {Integer.parseInt(in[0]),Integer.parseInt(in[1]),Integer.parseInt(in[2])};
                solution.computerConnections.add(edge);

            }

            test.buildComputerNetwork();


            testBuildComputerNetwork(solution.getComputerConnections(), test.getComputerConnections());


            readerSolution.nextLine();
            solution.computerGraph = new LinkedList<>();
            while(true){
                String[] in  = readerSolution.nextLine().split("\\{");

                if(in[0].equals("")) break;

                in[1] = in[1].replace("}","");
                in = in[1].split(",");

                LinkedList<Integer> edges = new LinkedList<>();

                for(var x:in){

                    if(x.equals("")) continue;
                    edges.add(Integer.parseInt(x));
                }
                solution.computerGraph.add(edges);

            }

            readerSolution.nextLine();
            solution.cluster = new LinkedList<>();
            while(true){
                String[] in  = readerSolution.nextLine().split("\\{");

                if(in[0].equals("")) break;

                in[1] = in[1].replace("}","");
                in = in[1].split(",");

                LinkedList<Integer> edges = new LinkedList<>();

                for(var x:in){

                    if(x.equals("")) continue;
                    edges.add(Integer.parseInt(x));
                }
                solution.cluster.add(edges);

            }

            test.buildCluster(clusterNum);


            testClusterOrComputerGraph(solution.getComputerGraph(), test.getComputerGraph(),"computerGraph");
            testClusterOrComputerGraph(solution.getCluster(), test.getCluster(),"cluster");


            readerSolution.nextLine();
            solution.routerGraph = new LinkedList<>();
            while(true){
                String[] in  = readerSolution.nextLine().split("\\{");

                if(in[0].equals("")) break;

                in[1] = in[1].replace("}","");
                in[1] = in[1].replaceAll("]","");
                in = in[1].split("\\[");

                LinkedList<Integer[]> nodes = new LinkedList<>();

                for(var x:in){
                    if(x.equals("")) continue;
                    String[] y = x.split(",");
                    Integer[] edge = {Integer.parseInt(y[0]),Integer.parseInt(y[1])};

                    nodes.add(edge);
                }
                solution.routerGraph.add(nodes);

            }
            test.connectCluster();

            testRouterGraph(solution.getRouterGraph(), test.getRouterGraph());

            readerTest.nextLine();
            int testNum = Integer.parseInt(readerTest.nextLine());
            readerSolution.nextLine();

            while (testNum > 0) {
                String testDijkstra = readerTest.nextLine();
                Assert.assertEquals("Ensure you return the shortest distance in traverseNetwork !", Integer.parseInt(readerSolution.nextLine()), test.traversNetwork(testDijkstra));
                testNum--;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            System.setIn(stdin);
        }
    }


    /**
     * Helper function to test buildComputerNetwork()
     *
     * @param expected Expected computerConnections
     * @param actual   Actual computerConnections
     */
    private void testBuildComputerNetwork(LinkedList<Integer[]> expected, LinkedList<Integer[]> actual) {

        Assert.assertEquals("Ensure buildComputerNetwork adds all edges into computerConnections (computerConnections.size() is incorrect) !", expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            if (actual.get(i).length != 3) {
                System.out.println("Ensure edge consists of [source] [destination] and [latency] !");
                Assert.fail();
                break;
            }

            for (int j = 0; j < 3; j++) {
                if (!expected.get(i)[j].equals(actual.get(i)[j])) {
                    System.out.println("Ensure all edges are added correctly and in the sequence given to you !");

                    System.out.print("Expected:[ ");
                    for(var x: expected.get(i)){
                        System.out.print(x+" ");
                    }
                    System.out.println("]");

                    System.out.print("Actual:[ ");
                    for(var x: actual.get(i)){
                        System.out.print(x+" ");
                    }
                    System.out.println("]");

                    Assert.fail();

                }
            }
        }
    }

    /**
     * Helper function to test either cluster or computerGraph of Network
     *
     * @param expectedCluster Expected cluster/computerGraph
     * @param actualCluster   Actual cluster/computerGraph
     */
    private void testClusterOrComputerGraph(LinkedList<LinkedList<Integer>> expectedCluster, LinkedList<LinkedList<Integer>> actualCluster,String what) {

        Assert.assertEquals("Ensure that "+what+" is constructed correctly ("+what+".size() is incorrect) !", expectedCluster.size(), actualCluster.size());

        HashSet<String> set = new HashSet<>();

        for (var i : expectedCluster) {
            String s = "";
            Collections.sort(i);
            for (var j : i) {
                s += j+"||";
            }
            set.add(s);
        }

        for (var i : actualCluster) {
            String a = "";
            Collections.sort(i);
            for (var j : i) {
                a += j+"||";
            }
            if (!set.contains(a)) {

                System.out.println("Ensure that "+what+" has all the correct collections (Try printing it out) !");
                System.out.println("Unexpected Collection: "+a);
                Assert.fail();
            }
        }
    }

    /**
     * Helper function to test routerGraph of Network
     *
     * @param expectedGraph Expected routerGraph
     * @param actualGraph   Actual routerGraph
     */
    private void testRouterGraph(LinkedList<LinkedList<Integer[]>> expectedGraph, LinkedList<LinkedList<Integer[]>> actualGraph) {

        Assert.assertEquals("Ensure that routerGraph is constructed correctly (routerGraph.size() is incorrect) !", expectedGraph.size(),actualGraph.size());


        int n = expectedGraph.size();


        HashMap<Integer,LinkedList<Integer>> edgeToPoint = new HashMap<>();

        for (int i=0;i<expectedGraph.size();i++) {

            LinkedList<Integer[]> temp = expectedGraph.get(i);
            edgeToPoint.put(i,new LinkedList<>());
            LinkedList<Integer> list = edgeToPoint.get(i);

            for (int j=0;j<temp.size();j++) {
                list.add(temp.get(j)[1]);
            }
        }

        HashMap<String,Integer> edCol = new HashMap<>();

        for(var i:edgeToPoint.keySet()){
            LinkedList<Integer> edges = edgeToPoint.get(i);
            Collections.sort(edges);
            String a = "";

            for(var j:edges){
                a += j+" || ";
            }

            edCol.put(a,i);
        }

        HashMap<Integer,LinkedList<Integer>> edgeToPoint2 = new HashMap<>();

        for (int i=0;i<actualGraph.size();i++) {

            LinkedList<Integer[]> temp = actualGraph.get(i);
            edgeToPoint2.put(i,new LinkedList<>());
            LinkedList<Integer> list = edgeToPoint2.get(i);

            for (int j=0;j<temp.size();j++) {
                list.add(temp.get(j)[1]);
            }
        }

        HashMap<String,Integer> edCol2 = new HashMap<>();

        for(var i:edgeToPoint2.keySet()){
            LinkedList<Integer> edges = edgeToPoint2.get(i);
            Collections.sort(edges);
            String a = "";

            for(var j:edges){
                a += j+" || ";
            }

            edCol2.put(a,i);
        }


        for(var i:edCol.keySet()){
            if(!edCol2.containsKey(i)){
                System.out.println("Ensure Router Graph is formed correctly (incorrect edges or edge weights found) !");
                System.out.println("Unexpected edge connections of a router: "+i);
                Assert.fail();
            }else{
                edCol2.remove(i);
            }
        }


    }
}
