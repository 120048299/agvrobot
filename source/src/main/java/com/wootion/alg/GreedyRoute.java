package com.wootion.alg;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/*
  无向图：路线之间距离为权重。图是连通的。
  寻找从起点出发，经过多点，到达终点的优化路径。

  最近距离贪婪路径: 缺点，不是最优。
 */
public class GreedyRoute {

    private EdgeWeightedGraph graph;
    private int pointC;//当前点
    private List<Edge> path; //全局路径

    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public List<Integer> route;
    public List<Path> pathList;
    private List<Edge> bridges;

    public GreedyRoute(){

    }

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every
     * other vertex in the edge-weighted graph {@code G}.
     *
     * @param  G the edge-weighted digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public GreedyRoute(EdgeWeightedGraph G, int s) {
        for (Edge e : G.edges()) {
            if (e.weight() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        distTo = new double[G.V()];
        edgeTo = new Edge[G.V()];

        validateVertex(s);

        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v))
                relax(e, v);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return the length of a shortest path between the source vertex {@code s} and
     *         the vertex {@code v}; {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path between the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path between the source vertex {@code s} and vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path between the source vertex {@code s} and vertex {@code v};
     *         {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Edge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e = v-w:            distTo[w] <= distTo[v] + e.weight()
    // (ii) for all edge e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(EdgeWeightedGraph G, int s) {

        // check that edge weights are nonnegative
        for (Edge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }

        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                int w = e.other(v);
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v-w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            if (w != e.either() && w != e.other(e.either())) return false;
            int v = e.other(w);
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }


    /**
     *  最近距离贪婪路径：
     1. 计算当前点到其他目标点的最短距离。用dij算法。
     2. 找出最近距离的C点，加入路径。
     3. 从C点计算1,找新的C点。
     4. 找完后，返回终点。
     * @return
     */

    public List<Path> findRoute(EdgeWeightedGraph graph,int start,List<Integer> targets){
        route = new ArrayList<>();
        pathList = new ArrayList<>();
        java.util.Date date1=new java.util.Date();
        getNearestPoint(graph,start,targets);
        java.util.Date date2=new java.util.Date();
        System.out.println(" spend time:"+(date2.getTime()-date1.getTime())/1000);
        System.out.println(" route is:"+route);
        return pathList;
    }


    /**
     *  最近距离贪婪路径+孤岛优先
     1. 计算当前点到其他目标点的最短距离。用dij算法。
     2. 找出最近距离的C点，加入路径。
     3. 从C点计算1,找新的C点。
     4. 找完后，返回终点。
     * @return
     */

    public List<Path> findRouteWithIland(EdgeWeightedGraph graph,int start,List<Integer> targets){
        route = new ArrayList<>();
        pathList = new ArrayList<>();
        java.util.Date date1=new java.util.Date();
        bridges=findAllBridge(graph);
        getNearestPoint(graph,start,targets);
        java.util.Date date2=new java.util.Date();
        System.out.println(" spend time:"+(date2.getTime()-date1.getTime())/1000);
        System.out.println(" route is:"+route);
        return pathList;
    }

    public void getNearestPoint(EdgeWeightedGraph graph,int start,List<Integer> targets){
        //System.out.println("----------getNearestPoint----from:"+start+" to :"+targets);
        int fromPoint=start;
        while (targets.size()>=1){
            if(targets.size()%100==0){
                System.out.print("----------size :"+targets.size());
            }
            //System.out.print("----------from :"+fromPoint);
            List<Path> list= calcAllShortestPath(graph,fromPoint,targets);
            if(list==null || list.size()==0){
                //到目标点位没有任何路径
                return ;
            }
            Path shortestPath=shortestPoint(list);
            int shortestPoint=shortestPath.getTo();

            //System.out.println(" nearest is:"+nearestPoint);
            targets.remove(new Integer(shortestPoint));
            route.add(shortestPoint);
            pathList.add(shortestPath);
            fromPoint=shortestPoint;
        }
        //route.add(targets.get(0));
    }

    public  Path shortestPoint(List<Path> list){
        double minDistance=99999;
        Path shortest=null;
        for (Path path: list) {
            //StdOut.println(path);
            if(path.distance<minDistance){
                minDistance=path.distance;
                shortest=path;
            }
        }
        return shortest;
    }


    /**
     *  计算当前点到其他目标点的最短距离。用dij算法。
     * @param
     * @return
     */
    public List<Path> calcAllShortestPath(EdgeWeightedGraph graph,int start,List<Integer> targets){
        List<Path>  list=calcAllShortestPath(graph,start);

        List<Path>  list2=new ArrayList<>();

        //由于目标点位可能包含当前点位 从targets中去掉start,构造一个从start到targets的path
        for(int i=0;i<targets.size();i++){
            if(targets.get(i)==start){
                Path path=new Path();
                path.setFrom(start);
                path.setTo(start);
                path.setDistance(0);
                List<Edge> edges = new ArrayList<>();
                edges.add(new Edge(start,start,0));
                path.setEdges(edges);
                list2.add(path);
            }
        }

        for (Path path: list) {
            for(int i=0;i<targets.size();i++){
                if(path.to==targets.get(i)){
                    list2.add(path);
                }
            }
        }
        return list2;
    }
        /**
         *  计算当前点到其他目标点的最短距离。用dij算法。
         * @param graph
         * @return
         */
    public List<Path> calcAllShortestPath(EdgeWeightedGraph graph,int start){
        List<Path> list=new ArrayList<>();
        if(start>graph.V()){
            System.out.println("input param error:s>v ");
            return null;
        }
        DijkstraUndirectedSP sp = new DijkstraUndirectedSP(graph, start);//s 起点

        // print shortest path
        for (int t = 0; t < graph.V(); t++) {
            if(t==start){
                continue;
            }

            if (sp.hasPathTo(t)) {
                Path path=new Path();
                path.from=start;
                path.to=t;
                path.edges=sp.pathTo(t);
                //StdOut.printf("%d to %d (%.2f)  ", start, t, sp.distTo(t));
                double distance=0;
                for (Edge e : sp.pathTo(t)) {
                    //StdOut.print(e + "   ");
                    distance+= e.weight();
                }
                path.distance=distance;
                list.add(path);
                //StdOut.println();
            }
            else {
                //StdOut.printf("%d to %d         no path\n", start, t);
            }
        }

        return list;


    }



    public  static double distance(double x1,double y1,double x2,double y2){
        return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1) );
    }

    //图的一条边是否为桥。桥：删除此边之后，原图分割图未两个不连接的图，1个点也是图
    public static boolean isBridge(EdgeWeightedGraph graph,int from ,int to ){
        for (Edge e : graph.adj(from)) {//所有的边
            EdgeWeightedGraph graph2=graph.clone();
            //删除此边
            int w=e.other(from);
            if(w==to) {
                graph2.deleteEdge(e);
                graph2.depthFirstPaths(from);
                boolean ret=graph2.hasPathTo(to);
                System.out.println(" has path from " + from +" to "+ to +": "+ret);
                return ret;
            }
        }
        return false;
    }

    //查找所有的桥
    public static List<Edge> findAllBridge(EdgeWeightedGraph graph){
        List<Edge> bridges=new ArrayList<Edge>();
        for (int v = 0; v < graph.V(); v++) {//所有顶点
            for (Edge e : graph.adj(v)) {//所有的边
                int w=e.other(v);
                EdgeWeightedGraph graph2=graph.clone();
                //删除此边
                graph2.deleteEdge(e);
                graph2.depthFirstPaths(v);
                boolean ret=graph2.hasPathTo(w);
                if(ret){
                    bridges.add(e);
                }
                System.out.println(v+" to "+ w +": "+!ret);

            }
        }
        return bridges;
    }


    public static List<Object> splitGraph(EdgeWeightedGraph graph,Edge edge){
        List<Object> list= new ArrayList<Object>();

        return list;
    }


    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the cmd-line arguments
     */
    public static void main(String[] args) {
        GreedyRoute greedyRoute=new GreedyRoute();
        greedyRoute.test1();
    }



    public   void test1(){

        Point points[]=new Point[8];
        points[0]=new Point("a",0,0);
        points[1]=new Point("b",1,0);
        points[2]=new Point("c",1,2);
        points[3]=new Point("d",1,3);
        points[4]=new Point("e",2,0);
        points[5]=new Point("f",3,0);
        points[6]=new Point("g",3,-1);
        points[7]=new Point("h",0,-1);


        EdgeWeightedGraph graph = new EdgeWeightedGraph(points.length);//顶点数
        Edge e=new Edge(0,1,distance( points[0].x, points[0].y, points[1].x, points[1].y));
        graph.addEdge(e);
        e=new Edge(1,2,distance( points[0].x, points[0].y, points[2].x, points[2].y));
        graph.addEdge(e);
        e=new Edge(2,3,distance( points[2].x, points[2].y, points[3].x, points[3].y));
        graph.addEdge(e);
        e=new Edge(1,4,distance( points[1].x, points[1].y, points[4].x, points[4].y));
        graph.addEdge(e);
        e=new Edge(4,5,distance( points[4].x, points[4].y, points[5].x, points[5].y));
        graph.addEdge(e);
        e=new Edge(5,6,distance( points[5].x, points[5].y, points[6].x, points[6].y));
        graph.addEdge(e);
        e=new Edge(4,5,distance( points[6].x, points[6].y, points[7].x, points[7].y));
        graph.addEdge(e);
        e=new Edge(4,5,distance( points[7].x, points[7].y, points[0].x, points[0].y));
        graph.addEdge(e);


        List<Integer>targets =new ArrayList<>();
        targets.add(1);
        targets.add(2);
        targets.add(3);
        targets.add(4);
        targets.add(5);
        findRoute(graph,0,targets);
    }

    public  void  test2(){
        int V=2000;
        EdgeWeightedGraph graph = new EdgeWeightedGraph(V);//顶点数
        Point points[]=new Point[V];
        for (int i=0;i<V;i++){
            int x= (int)(Math.random()*100);
            int y= (int)(Math.random()*100);
            points[i]=new Point("p"+i,x,y);
        }
        int E=V-1;
        for(int i=0;i<E;i++){
            int pos=(int)(Math.random()*(E-1));
            Edge e=new Edge(i,pos,distance( points[i].x, points[i].y, points[pos].x, points[pos].y));
            graph.addEdge(e);
        }
        for(int i=0;i<E-1;i++){
            int pos=(int)Math.random()*(E-1);
            Edge e=new Edge(i,pos,distance( points[i].x, points[i].y, points[pos].x, points[pos].y));
            graph.addEdge(e);
        }
        for(int i=10;i<E-3;i++){
            int pos=(int)Math.random()*(E-1);
            Edge e=new Edge(i,pos,distance( points[i].x, points[i].y, points[pos].x, points[pos].y));
            graph.addEdge(e);
        }

        for(int i=10;i<E-4;i++){
            int pos=(int)Math.random()*(E-1);
            Edge e=new Edge(i,pos,distance( points[i].x, points[i].y, points[pos].x, points[pos].y));
            graph.addEdge(e);
        }

        List<Integer>targets =new ArrayList<>();
        for(int i=1;i<graph.V();i++){
            targets.add(i);
        }
        findRoute(graph,0,targets);

    }


    public   void testFile(){
        //In in = new In(args[0]);

        BufferedReader br = null;
        FileReader fb = null;

        StringBuffer sb = new StringBuffer();
        try
        {
            File file = new File("file/dijUSP.txt");
            fb = new FileReader(file);
            br = new BufferedReader(fb);
            String strV=br.readLine();
            String line;


            //graph.deleteEdge(e);
            //isBridge(graph,3,5);
            //findAllBridge(graph);
            /*EdgeWeightedGraph G = new EdgeWeightedGraph(Integer.parseInt(strV));//顶点数
            line=br.readLine();
            while (null!=line && !"".equals(line)){
                String arry[]=line.split(" ");

                Edge e=new Edge(Integer.parseInt(arry[0]),Integer.parseInt(arry[1]),Double.parseDouble(arry[2]));
                G.addEdge(e);

                line=br.readLine();
            }
*/
            //int s = Integer.parseInt(args[1]);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }

    }
}

