package Utilities.DataObjects;

import nlogn.GraphEdge;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;

import java.util.List;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTNodeData extends Reference {
    private int miNumber;
    private PhylogenyNode twin;
    private List<GraphEdge> graph; // only for nodes in T2 at the beginning of a centroid path

    public PhylogenyNode getTwin() {
        return twin;
    }

    public void setTwin(PhylogenyNode twin) {
        this.twin = twin;
    }

    public int getMiNumber() {
        return miNumber;
    }

    public void setMiNumber(int miNumber) {
        this.miNumber = miNumber;
    }

    public List<GraphEdge> getGraph() {
        return graph;
    }

    public void setGraph(List<GraphEdge> graph) {
        this.graph = graph;
    }

    public MASTNodeData() {
        super("");
    }
}
