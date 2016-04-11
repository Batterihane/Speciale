package nlogn;

import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import Utilities.LCA;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Runner {
    public static void main(String[] args) {

        MAST mastFinder = new MAST();

        List<PhylogenyNode> leftSet = createLeftSet(7);

        List<PhylogenyNode> rightSet = createRightSet(6);
        Graph graph = new Graph(rightSet);

        addEdge(graph, leftSet.get(0), rightSet.get(0));
        addEdge(graph, leftSet.get(0), rightSet.get(1));
        addEdge(graph, leftSet.get(0), rightSet.get(3));
        addEdge(graph, leftSet.get(1), rightSet.get(3));
        addEdge(graph, leftSet.get(2), rightSet.get(2));
        addEdge(graph, leftSet.get(2), rightSet.get(4));
        addEdge(graph, leftSet.get(3), rightSet.get(0));
        addEdge(graph, leftSet.get(4), rightSet.get(0));
        addEdge(graph, leftSet.get(5), rightSet.get(0));
        addEdge(graph, leftSet.get(6), rightSet.get(5));

        mastFinder.computeMAST(graph, new Phylogeny[0][0]);
    }

    public static List<PhylogenyNode> createLeftSet(int size) {
        List<PhylogenyNode> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode node = new PhylogenyNode();
            NodeDataReference nodeData = new NodeDataReference();
            MASTNodeData nodeMastData = new MASTNodeData();
            nodeMastData.setPathNumber(i);
            nodeData.setMastNodeData(nodeMastData);
            node.getNodeData().setReference(nodeData);
            result.add(node);
        }
        return result;
    }

    public static List<PhylogenyNode> createRightSet(int size) {
        List<PhylogenyNode> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode node = new PhylogenyNode();
            NodeDataReference nodeData = new NodeDataReference();
            GraphNodeData nodeGraphData = new GraphNodeData();
            nodeGraphData.setIndex(i);
            nodeData.setGraphNodeData(nodeGraphData);
            node.getNodeData().setReference(nodeData);
            node.setName(i + "");
            result.add(node);
        }
        return result;
    }

    public static void addEdge(Graph graph, PhylogenyNode leftNode, PhylogenyNode rightNode) {
        GraphEdge edge = new GraphEdge(leftNode, rightNode);
        edge.setWhiteWeight(1);
        edge.setRedWeight(1);
        edge.setGreenWeight(1);
        graph.addEdge(edge);
    }
}
