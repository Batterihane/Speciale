package nlogn;

public class ProperCrossing {
    private GraphEdge greenEdge;
    private GraphEdge redEdge;
    int weight;

    public ProperCrossing(GraphEdge greenEdge, GraphEdge redEdge) {
        this.greenEdge = greenEdge;
        this.redEdge = redEdge;

        weight = greenEdge.getGreenWeight() + redEdge.getRedWeight();
    }

    public GraphEdge getGreenEdge() {
        return greenEdge;
    }

    public GraphEdge getRedEdge() {
        return redEdge;
    }

    public int getWeight() {
        return weight;
    }
}
