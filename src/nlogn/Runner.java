package nlogn;

import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree1 = foresterNewickParser.parseNewickFile("treess\\T1.new");
        Phylogeny tree2 = foresterNewickParser.parseNewickFile("treess\\T2.new");

        foresterNewickParser.displayPhylogeny(tree1);
        foresterNewickParser.displayPhylogeny(tree2);

        MAST mastFinder = new MAST();
        MAST.TreeAndSizePair mast = mastFinder.getMAST(tree1, tree2);

        foresterNewickParser.displayPhylogeny(mast.getTree());

    }

    private static void testLWAM() {
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

        mastFinder.computeLWAMsAndMastSizes(graph, new MAST.TreeAndSizePair[leftSet.size()][1]);
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
            MASTNodeData mastNodeData = new MASTNodeData();
            mastNodeData.setPathNumber(0);
            nodeData.setMastNodeData(mastNodeData);
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
