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
    private int lisNumber; // base case
    private int miNumber; // only for leaves in T1 except for u_p
    private int leafNumber; // only for leaves in T2
    private int pathNumber; // for node u_i in pi: i, for node v in pi(x_j): j
    private PhylogenyNode twin; // for all leaves
    private Graph graph; // only for nodes in T2 at the beginning of a centroid path
    private int subtreeMASTSize; // size of MAST(T1, T2(y)) for node y
    private PhylogenyNode siNode; // reference to corresponding si node - only for nodes in T2
    private PhylogenyNode t2Node; // reference from si node to corresponding t2 node

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

    public int getLisNumber() {
        return lisNumber;
    }

    public void setLisNumber(int lisNumber) {
        this.lisNumber = lisNumber;
    }

    public int getSubtreeMASTSize() {
        return subtreeMASTSize;
    }

    public void setSubtreeMASTSize(int subtreeMASTSize) {
        this.subtreeMASTSize = subtreeMASTSize;
    }

    public PhylogenyNode getSiNode() {
        return siNode;
    }

    public void setSiNode(PhylogenyNode siNode) {
        this.siNode = siNode;
    }

    public int getLeafNumber() {
        return leafNumber;
    }

    public void setLeafNumber(int leafNumber) {
        this.leafNumber = leafNumber;
    }

    public PhylogenyNode getT2Node() {
        return t2Node;
    }

    public void setT2Node(PhylogenyNode t2Node) {
        this.t2Node = t2Node;
    }

    public int getPathNumber() {
        return pathNumber;
    }

    public void setPathNumber(int pathNumber) {
        this.pathNumber = pathNumber;
    }

    public MASTNodeData() {
        super("");
    }
}
