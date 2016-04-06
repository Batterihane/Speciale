package Utilities.DataObjects;

import nlogn.GraphEdge;
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
    private List<GraphEdge> m;
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

    public List<GraphEdge> getM() {
        return m;
    }

    public void setM(List<GraphEdge> m) {
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

    private class ProperCrossing {
        private GraphEdge greenEdge;
        private GraphEdge redEdge;

        public ProperCrossing(GraphEdge greenEdge, GraphEdge redEdge) {
            this.greenEdge = greenEdge;
            this.redEdge = redEdge;
        }

        public GraphEdge getGreenEdge() {
            return greenEdge;
        }

        public GraphEdge getRedEdge() {
            return redEdge;
        }
    }
}
