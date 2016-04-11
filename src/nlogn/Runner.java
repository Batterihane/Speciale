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
        PhylogenyNode leftSetNode = new PhylogenyNode();
        PhylogenyNode rightSetNode1 = new PhylogenyNode();
        PhylogenyNode rightSetNode2 = new PhylogenyNode();

        NodeDataReference leftSetNodeData = new NodeDataReference();
        MASTNodeData leftSetNodeMastData = new MASTNodeData();
        leftSetNodeMastData.setPathNumber(0);
        leftSetNodeData.setMastNodeData(leftSetNodeMastData);
        leftSetNode.getNodeData().setReference(leftSetNodeData);

        NodeDataReference rightSetNode1Data = new NodeDataReference();
        GraphNodeData rightSetNode1GraphNodeData = new GraphNodeData();
        rightSetNode1GraphNodeData.setIndex(0);
        rightSetNode1Data.setGraphNodeData(rightSetNode1GraphNodeData);
        rightSetNode1.getNodeData().setReference(rightSetNode1Data);
        rightSetNode1.setName("0");

        NodeDataReference rightSetNode2Data = new NodeDataReference();
        GraphNodeData rightSetNode2GraphNodeData = new GraphNodeData();
        rightSetNode2GraphNodeData.setIndex(1);
        rightSetNode2Data.setGraphNodeData(new GraphNodeData());
        rightSetNode2.getNodeData().setReference(rightSetNode2Data);
        rightSetNode2.setName("1");

        List<PhylogenyNode> rightSet = new ArrayList<>();
        rightSet.add(rightSetNode1);
        rightSet.add(rightSetNode2);
        Graph graph = new Graph(rightSet);

        GraphEdge edge1 = new GraphEdge(leftSetNode, rightSetNode1);
        edge1.setWhiteWeight(1);
        edge1.setRedWeight(1);
        edge1.setGreenWeight(1);
        graph.addEdge(edge1);

        GraphEdge edge2 = new GraphEdge(leftSetNode, rightSetNode2);
        edge2.setWhiteWeight(1);
        edge2.setRedWeight(1);
        edge2.setGreenWeight(1);
        graph.addEdge(edge2);

        mastFinder.computeMAST(graph, new Phylogeny[0][0]);
    }
}
