package Utilities.DataObjects;

import nlogn.Graph;
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
    private Graph graph; // only for nodes in T2 at the beginning of a centroid path

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

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public MASTNodeData() {
        super("");
    }
}
