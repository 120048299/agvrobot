package com.wootion.alg;

public class Path
{
    int from ;
    int to;
    Iterable<Edge> edges;
    double distance;


    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Iterable<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Iterable<Edge> edges) {
        this.edges = edges;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Path{" +
                "from=" + from +
                ", to=" + to +
                ", edges=" + edges +
                ", distance=" + distance +
                '}';
    }
}
