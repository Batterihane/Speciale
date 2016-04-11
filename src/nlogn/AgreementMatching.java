package nlogn;

import java.util.List;

/**
 * Created by Thomas on 07-04-2016.
 */
public class AgreementMatching {
    ProperCrossing properCrossing;
    List<GraphEdge> whiteEdges; // bottom to top order
    int weight;
    GraphEdge topmostEdge;

    public AgreementMatching(ProperCrossing properCrossing, List<GraphEdge> whiteEdges, int weight) {
        this.properCrossing = properCrossing;
        this.whiteEdges = whiteEdges;
        this.weight = weight;
        if(whiteEdges.isEmpty())
            topmostEdge = properCrossing.getGreenEdge();
        else
            topmostEdge = whiteEdges.get(whiteEdges.size()-1);
    }

    public ProperCrossing getProperCrossing() {
        return properCrossing;
    }

    public List<GraphEdge> getWhiteEdges() {
        return whiteEdges;
    }

    public int getWeight() {
        return weight;
    }

    public void addWhiteEdge(GraphEdge whiteEdge) {
        whiteEdges.add(whiteEdge);
        weight += whiteEdge.getWhiteWeight();
        topmostEdge = whiteEdge;
    }

    public GraphEdge getTopmostEdge(){
        return topmostEdge;
    }

    @Override
    public String toString() {
        String result = "White edges: ";
        for (GraphEdge whiteEdge : whiteEdges){
            result += whiteEdge.toString() + ", ";
        }
        result += " Proper crossing: " + properCrossing.toString();
        return result;
    }
}
