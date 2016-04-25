package nlogn;

import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {
    public static void main(String[] args) {
        Phylogeny mast = getMASTFromNewickFiles("trees\\random\\testTree1.new", "trees\\random\\testTree2.new");
        Archaeopteryx.createApplication(mast);
    }

    private static Phylogeny getMASTFromNewickFiles(String tree1Path, String tree2Path){
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree1 = foresterNewickParser.parseNewickFile(tree1Path);
        Phylogeny tree2 = foresterNewickParser.parseNewickFile(tree2Path);

        Archaeopteryx.createApplication(tree1);
        Archaeopteryx.createApplication(tree2);

        Map<String, Integer> namesToNumbers = new HashMap<>();
        List<String> numbersToNames = new ArrayList<>();

        // Change names to numbers
        PhylogenyNodeIterator tree2LeafIterator = tree2.iteratorExternalForward();
        for (int i = 0 ; tree2LeafIterator.hasNext() ; i++){
            PhylogenyNode currentLeaf = tree2LeafIterator.next();
            String name = currentLeaf.getName();
            namesToNumbers.put(name, i);
            numbersToNames.add(name);
            currentLeaf.setName(i + "");
        }
        PhylogenyNodeIterator tree1LeafIterator = tree1.iteratorExternalForward();
        while (tree1LeafIterator.hasNext()){
            PhylogenyNode currentLeaf = tree1LeafIterator.next();
            String name = currentLeaf.getName();
            currentLeaf.setName(namesToNumbers.get(name) + "");
        }

        MAST mastFinder = new MAST();
        MAST.TreeAndSizePair mastPair = mastFinder.getMAST(tree1, tree2, false);

        Phylogeny mast = mastPair.getTree();
        PhylogenyNodeIterator mastLeafIterator = mast.iteratorExternalForward();
        while (mastLeafIterator.hasNext()){
            PhylogenyNode currentLeaf = mastLeafIterator.next();
            int number = Integer.parseInt(currentLeaf.getName());
            currentLeaf.setName(numbersToNames.get(number));
        }

        return mast;
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

        mastFinder.computeLWAMsAndMastSizes(graph, new AgreementMatching[leftSet.size()][1]);
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
