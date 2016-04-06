package Utilities.DataObjects;

import nlogn.GraphEdge;
import nlogn.MatchingWithWhiteEdge;
import nlogn.ProperCrossing;
import org.forester.phylogeny.data.Reference;

import java.util.List;

/**
 * Created by Thomas on 05-04-2016.
 */
public class SearchTreeNodeData extends Reference {
    private int index;
    private int lowIndex; // lowest index in subtree
    private GraphEdge g;
    private ProperCrossing x;
    private MatchingWithWhiteEdge m;
    private ProperCrossing y;
    private GraphEdge r;

    public GraphEdge getG() {
        return g;
    }

    public void setG(GraphEdge g) {
        this.g = g;
    }

    public ProperCrossing getX() {
        return x;
    }

    public void setX(ProperCrossing x) {
        this.x = x;
    }

    public MatchingWithWhiteEdge getM() {
        return m;
    }

    public void setM(MatchingWithWhiteEdge m) {
        this.m = m;
    }

    public ProperCrossing getY() {
        return y;
    }

    public void setY(ProperCrossing y) {
        this.y = y;
    }

    public GraphEdge getR() {
        return r;
    }

    public void setR(GraphEdge r) {
        this.r = r;
    }

    public int getIndex() {
        return index;
    }

    public int getLowIndex() {
        return lowIndex;
    }

    public SearchTreeNodeData(int index, int lowIndex) {
        super("");
        this.index = index;
        this.lowIndex = lowIndex;
    }
}
