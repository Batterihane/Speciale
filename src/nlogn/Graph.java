package nlogn;

import org.forester.phylogeny.PhylogenyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 15-03-2016.
 */
public class Graph {
    private List<PhylogenyNode> leftSet;
    private List<PhylogenyNode> rightSet;
    private List<GraphEdge> edges;

    public Graph(List<PhylogenyNode> rightSet) {
        leftSet = new ArrayList<>();
        this.rightSet = rightSet;
        edges = new ArrayList<>();
    }

    public List<PhylogenyNode> getLeftSet() {
        return leftSet;
    }

    public List<PhylogenyNode> getRightSet() {
        return rightSet;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void addNodeToLeftSet(PhylogenyNode node){
        leftSet.add(node);
    }

    public void addEdge(GraphEdge edge){ // Should be added consecutively w.r.t. the centroid path of T1
        if(leftSet.size() == 0 || edge.getLeft() != leftSet.get(leftSet.size()-1)){
            leftSet.add(edge.getLeft());
        }
        edges.add(edge);
    }
}
