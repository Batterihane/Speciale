package nlogn;

import java.util.List;

public class MatchingWithWhiteEdge {
    List<GraphEdge> edges;
    int weight;

    public MatchingWithWhiteEdge(List<GraphEdge> edges, int weight) {
        this.edges = edges;
        this.weight = weight;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public int getWeight() {
        return weight;
    }
}
