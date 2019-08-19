package com.wootion.alg;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/*
  无向图：路线之间距离为权重。图是连通的。
  寻找从起点出发，经过多点，到达终点的优化路径。

  最近距离贪婪路径: 缺点，不是最优。

  增加岛优先 2018.11.13
  （1) 计算所有的孤岛和对应的桥。
  （2）判断：模拟在最短路径上移动，如果在前往下一个目标的路径中，有指向岛的桥，则先过桥。即从出发点改变路线往岛上走。
  （3）对孤岛进行路径规划，处理完孤岛。
  （4）再从到上最后一点继续到下一个最近点。


  增加单线旁路优先计算：
  （1）下一点是三岔路口
  (2) 两个分支：一个是单线，另一个不论。先走单线的。

 */
public class GreedyRouteWithIsland {

    private EdgeWeightedGraph graph;
    private int zeroPoint; //地图的原点，任何需要达到的点都和zeroPoint连通的。

    private int pointC;//当前点
    private List<Edge> path; //全局路径

    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public List<Integer> route;
    public List<Path> pathList;
    private List<Map> bridges;

    public GreedyRouteWithIsland(){

    }

    public int getZeroPoint() {
        return zeroPoint;
    }

    public void setZeroPoint(int zeroPoint) {
        this.zeroPoint = zeroPoint;
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
    public GreedyRouteWithIsland(EdgeWeightedGraph G, int s) {
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
        if(start<0 || targets==null || targets.size()==0){
            return pathList;
        }
        java.util.Date date1=new java.util.Date();
        getNearestPoint(graph,start,targets);
        java.util.Date date2=new java.util.Date();
        //System.out.println(" spend time:"+(date2.getTime()-date1.getTime())/1000);
        //System.out.println(" route is:"+route);
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
        if(start<0 || targets==null || targets.size()==0){
            return pathList;
        }
        java.util.Date date1=new java.util.Date();
        bridges=findAllBridge(graph);
        getNearestPoint(graph,start,targets);
        java.util.Date date2=new java.util.Date();
        //System.out.println(" spend time:"+(date2.getTime()-date1.getTime())/1000);
        //System.out.println(" route is:"+route);
        //System.out.println(" route is:"+pathList);
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
            //全路径的序列
            ArrayList<Integer> pathPointArray=new ArrayList<>();
            pathPointArray.add(shortestPath.getFrom());
            Iterable<Edge> edges=shortestPath.edges;
            Iterator<Edge> it  = edges.iterator();
            int i=0;
            while (it.hasNext()) {
                Edge edge = it.next();
                int from = edge.either();
                int to = edge.other(from);
                if(pathPointArray.get(i)==from){
                    pathPointArray.add(to);
                }else{
                    pathPointArray.add(from);
                }
            }


            int shortestPoint=shortestPath.getTo();
            //int islandLastPos=dealBridge(graph,fromPoint,shortestPath,targets);
            int islandLastPos=dealBridge(graph,fromPoint,shortestPath.getTo(),pathPointArray,targets);
            //int singleLineLastPos=dealSingleLine(graph,fromPoint,shortestPath,targets);
            if(islandLastPos!=-1){
                //重新规划孤岛内的最后一个点 全图最近点
                fromPoint=islandLastPos;
                continue;
            }

            //System.out.println(" shortestPoint is:"+shortestPoint);
            targets.remove(new Integer(shortestPoint));
            route.add(shortestPoint);
            pathList.add(shortestPath);
            fromPoint=shortestPoint;
        }
        //route.add(targets.get(0));
    }

    private int dealBridge(EdgeWeightedGraph graph,int fromPoint,int toPoint,ArrayList<Integer> pathPointArray,List<Integer> targets){
        //如果start出发的到最近点线路shortestPathd的路线旁边有桥，先判断桥的另外一边的子图是否有目标点

        //对路线的每一步处理
        int lastPoint=-1;
        int newStart=-1;
        Map bridge;
        Edge bridgeEdge=null;
        //岛上的目标点
        List<Integer> targets2 = new ArrayList<>();
        boolean needToWalkIn=false;
        int prePoint=-1;
        prePoint =pathPointArray.get(0);
        newStart=pathPointArray.get(1);
        for (int k=1;k<pathPointArray.size();k++,prePoint=newStart){
            //作为起点
            //如果newStart本来就在一个串行点中，没有分支
            newStart=pathPointArray.get(k);
            List<Edge>  adj=graph.adjEdgeList(newStart);
            if(adj.size()<=2){
                continue;
            }
            bridge = getBridge(newStart);
            if (bridge == null) {
                continue;
            }
            bridgeEdge = (Edge) bridge.get("edge");
            //岛上包括的断桥之后从门前点不可到达的点
            List<Integer> noPath = (List<Integer>) bridge.get("noPathVectors");
            // 目标点是走向这个子图的点，就不用处理了。本来就是要到这个岛上
            if (noPath.contains(toPoint)) {
                continue;
            }

            //岛上多点加入，优先走。岛上
            Iterator<Integer> it2 = targets.iterator();
            while (it2.hasNext()) {
                Integer target = it2.next();
                System.out.print("target:" + target);
                if (noPath.contains(target)) {
                    targets2.add(target);
                }
            }
            if (targets2.size() == 0) {
                continue;
            }
            needToWalkIn=true;
            break;//路途中找到一个需要走的孤岛
        }

        if(needToWalkIn){
            //此时优先处理子图//处理子图.复制一份图，删除其他边，留下桥
            EdgeWeightedGraph graph2=graph.clone();
            Iterator<Edge> it3=graph2.adj(newStart).iterator();
            List<Integer> list3= new ArrayList<>();
            while(it3.hasNext()) {//此点所有的边
                Edge e=it3.next();
                int v=e.either();
                int w=e.other(v);
                if(v!=newStart){
                    list3.add(v);
                }else{
                    list3.add(w);
                }
            }
            for (int temp:list3){
                //保留分支前的边和桥。让路径规划从上一个点到孤岛内某一点
                if(temp!=bridgeEdge.other(newStart) && temp!=prePoint){
                    Edge e=new Edge(newStart,temp,0);
                    graph2.deleteEdge(e);
                }
            }

            //至此从newStart只有到到孤岛的桥
            //全图计算从newStart开始到桥上点位的路线。至于孤岛后还有孤岛则不管效率问题了
            newStart=fromPoint;
            while (targets2.size()>=1){
                List<Path> list2= calcAllShortestPath(graph2,newStart,targets2);
                if(list2==null || list2.size()==0){
                    //到目标点位没有任何路径
                    return -1;
                }
                Path shortestPath2=shortestPoint(list2);
                int shortestPoint2=shortestPath2.getTo();
                System.out.println(" shortestPoint2 is:"+shortestPoint2+" shortestPath2 "+shortestPath2 );
                targets2.remove(new Integer(shortestPoint2));
                targets.remove(new Integer(shortestPoint2));
                route.add(shortestPoint2);
                pathList.add(shortestPath2);
                newStart=shortestPoint2;
                lastPoint=newStart;
            }
            //岛内最后一个目标点
            return lastPoint;
        }
        return -1;
    }

    private int dealSingleLine(EdgeWeightedGraph graph,int fromPoint,Path shortestPath,List<Integer> targets){
        //如果start出发的到最近点线路shortestPathd的路线旁边有单线，先判断旁路单线上是否有目标点
        int shortestPoint=shortestPath.getTo();
        Iterable<Edge> edges=shortestPath.edges;
        Iterator<Edge> it  = edges.iterator();
        //对路线的每一步处理
        int lastPoint=-1;
        int newStart=-1;
        Map bridge;
        Edge bridgeEdge=null;
        //旁路单线的目标点
        List<Integer> targets2 = new ArrayList<>();
        boolean needToWalkIn=false;
        int prePoint=-1;
        List<Integer> pathPointList = new ArrayList<>();
        int last=-1;
        while (it.hasNext()) {
            Edge edge = it.next();
            int from = edge.either();
            int to = edge.other(from);
            if (from != last) {
                pathPointList.add(from);
            }
            if (to != last) {
                pathPointList.add(to);
            }
            last = to;
        }



            /*if (bridge == null) {
                continue;
            }
            bridgeEdge = (Edge) bridge.get("edge");
            List<Integer> noPath = (List<Integer>) bridge.get("noPathVectors");
            // 最近点是走向这个子图的点，
            if (noPath.contains(shortestPoint)) {
                continue;
            }

            Iterator<Integer> it2 = targets.iterator();
            while (it2.hasNext()) {
                Integer target = it2.next();
                System.out.print("target:" + target);
                if (noPath.contains(target)) {
                    targets2.add(target);
                }
            }
            if (targets2.size() == 0) {
                continue;
            }*/
            needToWalkIn=true;
           // break;//路途中找到一个需要走的孤岛

        //findSimpleSideWay(graph,prePoint,newStart,to);


/*
        if(needToWalkIn){
            //此时优先处理子图//处理子图.复制一份图，删除其他边，留下桥
            EdgeWeightedGraph graph2=graph.clone();
            Iterator<Edge> it3=graph2.adj(newStart).iterator();
            List<Integer> list3= new ArrayList<>();
            while(it3.hasNext()) {//此点所有的边
                Edge e=it3.next();
                int v=e.either();
                int w=e.other(v);
                if(v!=newStart){
                    list3.add(v);
                }else{
                    list3.add(w);
                }
            }
            for (int temp:list3){
                //保留分支前的边和桥。让路径规划从上一个点到孤岛内某一点
                if(temp!=bridgeEdge.other(newStart) && temp!=prePoint){
                    Edge e=new Edge(newStart,temp,0);
                    graph2.deleteEdge(e);
                }
            }

            //至此从newStart只有到到孤岛的桥
            //全图计算从newStart开始到桥上点位的路线。至于孤岛后还有孤岛则不管效率问题了
            newStart=fromPoint;
            while (targets2.size()>=1){
                List<Path> list2= calcAllShortestPath(graph2,newStart,targets2);
                if(list2==null || list2.size()==0){
                    //到目标点位没有任何路径
                    return -1;
                }
                Path shortestPath2=shortestPoint(list2);
                int shortestPoint2=shortestPath2.getTo();
                System.out.println(" shortestPoint2 is:"+shortestPoint2+" shortestPath2 "+shortestPath2 );
                targets2.remove(new Integer(shortestPoint2));
                targets.remove(new Integer(shortestPoint2));
                route.add(shortestPoint2);
                pathList.add(shortestPath2);
                newStart=shortestPoint2;
                lastPoint=newStart;
            }
            //岛内最后一个目标点
            return lastPoint;
        }
*/
        return -1;
    }



    /**
     *查找所有的 简单旁路:
     *
     */
    public  List<Map> findSimpleSideWay(EdgeWeightedGraph graph,int from,int current,int to){

            List<Edge> list=graph.adjEdgeList(from);
            if(list.size()!=3){
                return null;
            }
            System.out.println(" start from "+from);
            int found0=-1;
            int found1=-1;
            int found2=-1;

            Edge edgeFrom=new Edge(from,current,1);
            Edge edgeTo=new Edge(current,to,1);
            //发现需要判断的另一个线路：来的线路和去的线路不需要判断了
            for (int i=0;i<list.size();i++){
                Edge e=list.get(i);
                if(e.equals(edgeFrom) || e.equals(edgeTo)){
                    break;
                }
                int pre= from;
                int next= e.other(from);

                List<Edge> list2;
                while (true){
                    list2=graph.adjEdgeList(next);
                    if(list2.size()!=2){
                        break;
                    }
                    int temp=next;
                    next = nextOutPointOfLine(list2,pre,next);
                    pre = temp;
                    System.out.println("next="+next);
                }
                if(list2.size()==3) {
                    if(i==0){
                        found0=next;
                    }else if(i==1){
                        found1=next;
                    }else{
                        found2=next;
                    }
                    System.out.println("found sideway point B"+next);
                }
            }

            if(found0>=0 && found0==found1){
                System.out.println(list.get(0)+"  :  "+list.get(1));
            }
            if(found1>=0 && found1==found2){
                System.out.println(list.get(1)+"  :  "+list.get(2));
            }
            if(found2>=0 && found2==found0){
                System.out.println(list.get(2)+"  :  "+list.get(0));
            }

        return null;
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

        //由于目标点位可能包含当前点位 从targets中去掉start,构造一个从start到start的path
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

    public  static double distance(Point p1,Point p2){
        return Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y) );
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

    //查找所有的 桥和对应不可连通的点（从桥的起点看不可达）
    public  List<Map> findAllBridge(EdgeWeightedGraph graph){
        List<Map> bridges=new ArrayList<>();
        for (int v = 0; v < graph.V(); v++) {//所有顶点
            for (Edge e : graph.adj(v)) {//所有的边
                int w=e.other(v);
                EdgeWeightedGraph graph2=graph.clone();
                //删除此边
                graph2.deleteEdge(e);
                graph2.depthFirstPaths(v);
                boolean ret=graph2.hasPathTo(w);
                if(!ret){
                    List noPathVectors=graph2.getNoPathVectors();
                    //如果noPathVectors包含原点 则认为不是岛
                    if(noPathVectors.contains(this.zeroPoint)){
                        continue;
                    }
                    Map map = new HashMap();
                    map.put("edge",e);
                    map.put("noPathVectors",noPathVectors);
                    bridges.add(map);
                }
                //System.out.println(v+" to "+ w +": "+!ret);
            }
        }
        return bridges;
    }


    /**
     *查找所有的 简单旁路:
     * 符合条件:A点为三岔路口，从A点的一个分支，纯粹一条线，走到另一个三岔路口B。
     * 然后从B点的另外两个分支中，可以找到一条到A点的路，此线路也是纯粹的单线。即AB为岔路口，AB之间是一个简单的回路。
     */
    public  List<Map> findSimpleSideWay(EdgeWeightedGraph graph){
        List<Map> bridges=new ArrayList<>();
        for (int v = 0; v < graph.V(); v++) {//所有顶点
            List<Edge> list=graph.adjEdgeList(v);
            if(list.size()!=3){
                continue;
            }
            System.out.println(" start A"+v);
            int found0=-1;
            int found1=-1;
            int found2=-1;

            //3个边都走一趟
            for (int i=0;i<list.size();i++){
                Edge e=list.get(i);
                int pre= v;
                int next= e.other(v);
                List<Edge> list2;
                while (true){
                    list2=graph.adjEdgeList(next);
                    if(list2.size()!=2){
                        break;
                    }
                    int temp=next;
                    next = nextOutPointOfLine(list2,pre,next);
                    pre = temp;
                    System.out.println("next="+next);
                }
                if(list2.size()==3) {
                    if(i==0){
                        found0=next;
                    }else if(i==1){
                        found1=next;
                    }else{
                        found2=next;
                    }
                    System.out.println("found sideway point B"+next);
                }
            }

            if(found0>=0 && found0==found1){
                System.out.println(list.get(0)+"  :  "+list.get(1));
            }
            if(found1>=0 && found1==found2){
                System.out.println(list.get(1)+"  :  "+list.get(2));
            }
            if(found2>=0 && found2==found0){
                System.out.println(list.get(2)+"  :  "+list.get(0));
            }
        }
        return null;
    }

    /**
     * 求只有两个连线的出线
     * @param list2
     * @return
     */
    private int nextOutPointOfLine( List<Edge> list2,int pre,int current){
        Edge e0=list2.get(0);
        Edge e1=list2.get(1);
        int temp[]=new int[3];
        temp[0]=pre;
        temp[0]=current;

        if(e0.either()==pre ){
            //肯定不是
        }else if(e0.either()==current){
            if(e0.other(current)!=pre){
                return e0.other(current);
            }
        }else {
            return e0.either();
        }
        if(e1.either()==pre ){
            //肯定不是
        }else if(e1.either()==current){
            if(e1.other(current)!=pre){
                return e1.other(current);
            }
        }else {
            return e1.either();
        }
        return -1;
    }

    private Map getBridge(int v){
        for (Map bridge :bridges){
            Edge edge =(Edge)bridge.get("edge");
            List<Integer>  noPathVectors =(List<Integer>)bridge.get("noPathVectors");
            //if(edge.either()==v ){
            if(edge.either()==v || edge.other(edge.either())==v  ){
                return bridge;
            }
        }
        return null;
    }


    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args the cmd-line arguments
     */
    public static void main(String[] args) {
        GreedyRouteWithIsland greedyRoute=new GreedyRouteWithIsland();
        greedyRoute.setZeroPoint(0);
        greedyRoute.test2();

    }



    public   void test1(){


        Point points[]=new Point[18];
        points[0]=new Point("a",0,0);
        points[1]=new Point("b",1,0);
        points[2]=new Point("c",2,0);
        points[3]=new Point("d",3,0);
        points[4]=new Point("e",4,0);
        points[5]=new Point("f",5,0);
        points[6]=new Point("g",6,0);
        points[7]=new Point("h",7,0);
        points[8]=new Point("i",8,0);
        points[9]=new Point("j",9,0);
        points[10]=new Point("A",2,1);
        points[11]=new Point("B",2.5,1);
        points[12]=new Point("C",2.5,2);
        points[13]=new Point("D",2,2);
        points[14]=new Point("E",3,1);
        points[15]=new Point("F",5,1);
        points[16]=new Point("G",5,2);
        points[17]=new Point("H",2,-0.5);




        EdgeWeightedGraph graph = new EdgeWeightedGraph(points.length);//顶点数
        Edge e;
        e=new Edge(0,1,distance( points[0],points[1]));graph.addEdge(e);
        e=new Edge(1,2,distance( points[1],points[2]));graph.addEdge(e);
        e=new Edge(2,3,distance( points[2],points[3]));graph.addEdge(e);
        e=new Edge(3,4,distance( points[3],points[4]));graph.addEdge(e);
        e=new Edge(4,5,distance( points[4],points[5]));graph.addEdge(e);
        e=new Edge(5,6,distance( points[5],points[6]));graph.addEdge(e);
        e=new Edge(6,7,distance( points[6],points[7]));graph.addEdge(e);
        e=new Edge(7,8,distance( points[7],points[8]));graph.addEdge(e);
        e=new Edge(8,9,distance( points[8],points[9]));graph.addEdge(e);
        e=new Edge(9,0,distance( points[9],points[0]));graph.addEdge(e);

        e=new Edge(2,10,distance( points[2],points[10]));graph.addEdge(e);
        e=new Edge(10,11,distance( points[10],points[11]));graph.addEdge(e);
        e=new Edge(11,12,distance( points[11],points[12]));graph.addEdge(e);
        e=new Edge(12,13,distance( points[12],points[13]));graph.addEdge(e);
        e=new Edge(13,10,distance( points[13],points[10]));graph.addEdge(e);

        e=new Edge(3,14,distance( points[3],points[14]));graph.addEdge(e);
        e=new Edge(5,15,distance( points[5],points[15]));graph.addEdge(e);
        e=new Edge(15,16,distance( points[15],points[16]));graph.addEdge(e);

        e=new Edge(2,17,distance( points[17],points[17]));graph.addEdge(e);



        List<Integer>targets =new ArrayList<>();
        targets.add(11);
        targets.add(17);

        targets.add(7);

        //findRouteWithIland(graph,0,targets);

        findSimpleSideWay(graph);


    }

    public   void test2(){
        Point points[]=new Point[12];
        points[0]=new Point("a",0,0);
        points[1]=new Point("b",1,0);
        points[2]=new Point("c",2,0);
        points[3]=new Point("d",3,0);
        points[4]=new Point("e",4,0);
        points[5]=new Point("f",5,0);
        points[6]=new Point("g",6,0);
        points[7]=new Point("h",7,0);
        points[8]=new Point("i",8,0);
        points[9]=new Point("j",9,0);
        points[10]=new Point("A",2,1);
        points[11]=new Point("B",2.5,1);


        EdgeWeightedGraph graph = new EdgeWeightedGraph(points.length);//顶点数
        Edge e;
        e=new Edge(0,1,distance( points[0],points[1]));graph.addEdge(e);
        e=new Edge(1,2,distance( points[1],points[2]));graph.addEdge(e);
        e=new Edge(2,3,distance( points[2],points[3]));graph.addEdge(e);
        e=new Edge(3,4,distance( points[3],points[4]));graph.addEdge(e);
        e=new Edge(4,5,distance( points[4],points[5]));graph.addEdge(e);
        e=new Edge(5,6,distance( points[5],points[6]));graph.addEdge(e);
        e=new Edge(6,7,distance( points[6],points[7]));graph.addEdge(e);
        e=new Edge(7,8,distance( points[7],points[8]));graph.addEdge(e);
        e=new Edge(8,9,distance( points[8],points[9]));graph.addEdge(e);
        e=new Edge(9,0,distance( points[9],points[0]));graph.addEdge(e);

        e=new Edge(2,10,distance( points[2],points[10]));graph.addEdge(e);
        e=new Edge(10,11,distance( points[10],points[11]));graph.addEdge(e);
        e=new Edge(11,3,distance( points[11],points[3]));graph.addEdge(e);





        List<Integer>targets =new ArrayList<>();
        targets.add(11);
        targets.add(17);

        targets.add(7);

        //findRouteWithIland(graph,0,targets);

        findSimpleSideWay(graph);


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

