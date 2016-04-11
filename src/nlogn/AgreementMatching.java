package nlogn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 07-04-2016.
 */
public class AgreementMatching {
    ProperCrossing properCrossing;
    List<GraphEdge> whiteEdges; // bottom to top order
    int weight;
    int numberOfWhiteEdges;
    GraphEdge topmostEdge;

    public AgreementMatching(ProperCrossing properCrossing, List<GraphEdge> whiteEdges, int weight) {
        this.properCrossing = properCrossing;
        this.whiteEdges = whiteEdges;
        this.numberOfWhiteEdges = whiteEdges.size();
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

//    public void addWhiteEdge(GraphEdge whiteEdge) {
//        whiteEdges.add(whiteEdge);
//        weight += whiteEdge.getWhiteWeight();
//        topmostEdge = whiteEdge;
//    }

    public GraphEdge getTopmostEdge(){
        return topmostEdge;
    }

    @Override
    public String toString() {
        String result = "White edges: ";
        for (int i = 0; i < numberOfWhiteEdges; i++) {
            result += whiteEdges.get(i).toString() + ", ";
        }
        result += " Proper crossing: " + properCrossing.toString();
        return result;
    }
}
