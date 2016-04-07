package nlogn;

import Utilities.*;
import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.DataObjects.SearchTreeNodeData;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MAST {

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
//        Phylogeny tree = foresterNewickParser.parseNewickFile("treess\\Tree2.new");

        Phylogeny tree1 = PhylogenyGenerator.generateTree(11);
        Phylogeny tree2 = PhylogenyGenerator.generateTree(11);
        PhylogenyGenerator.renameTreeLeavesLeftToRight(tree2);
        MAST mast = new MAST();
//        List<PhylogenyNode> firstDecomposition = mast.computeFirstDecomposition(tree);
//        for (PhylogenyNode node : firstDecomposition){
//            System.out.println(node.getName());
//        }
        foresterNewickParser.displayPhylogeny(tree1);
        foresterNewickParser.displayPhylogeny(tree2);
        foresterNewickParser.displayPhylogeny(mast.getMAST(tree1, tree2));
    }

    public Phylogeny getMAST(Phylogeny tree1, Phylogeny tree2){
        addNodeDataReferences(tree1);
        addNodeDataReferences(tree2);

        tree1.recalculateNumberOfExternalDescendants(true);
        tree2.recalculateNumberOfExternalDescendants(true);
        int numberOfLeaves = tree1.getRoot().getNumberOfExternalNodes();

        // simple base case
        if(numberOfLeaves == 1){
            getMASTNodeDataFromNode(tree2.getRoot()).setSubtreeMASTSize(1);
            return tree1; //TODO: copy tree?
        }
        // base case for test
        if(numberOfLeaves == 2){
            getMASTNodeDataFromNode(tree2.getRoot().getChildNode1()).setSubtreeMASTSize(1);
            getMASTNodeDataFromNode(tree2.getRoot().getChildNode2()).setSubtreeMASTSize(1);
            getMASTNodeDataFromNode(tree2.getRoot()).setSubtreeMASTSize(2);
            return tree1;
        }

        setTwins(tree1, tree2);
        List<PhylogenyNode> tree1Decomposition = computeFirstDecomposition(tree1);
        List<List<PhylogenyNode>> tree2Decomposition = computeSecondDecomposition(tree2);

        // lis base case
//        if(tree1Decomposition.size() == numberOfLeaves && tree2Decomposition.size() == 1){
//            return baseCaseModified(tree2, tree1);
//        }

        List<Phylogeny> siSubtrees = induceSubtrees(tree1Decomposition, tree1, tree2);

        computeMiSiMASTs(tree1Decomposition, siSubtrees);

        createGraphsAndComputeMASTs(tree1Decomposition, tree2Decomposition, siSubtrees);

//        throw new NotImplementedException();
        return new Phylogeny();
    }

    // Initial setup
    private void addNodeDataReferences(Phylogeny tree){
        if(tree.getRoot().getNodeData().getReferences() != null) return; // Node data has already been added.

        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            NodeDataReference nodeDataReference = new NodeDataReference();
            nodeDataReference.setMastNodeData(new MASTNodeData());
            currentNode.getNodeData().addReference(nodeDataReference);
        }
    }
    public void setTwins(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree2Leaves = tree2.getExternalNodes();
        PhylogenyNodeIterator iterator = tree1.iteratorPreorder();
        while (iterator.hasNext()) {
            PhylogenyNode currentNode = iterator.next();
            if (currentNode.isExternal()) {
                MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);

                if(mastNodeData.getTwin() != null) return; // Twins have already been set.

                int name = Integer.parseInt(currentNode.getName());
                PhylogenyNode twin = tree2Leaves.get(name);
                mastNodeData.setTwin(twin);

                MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(twin);
                twinMastNodeData.setTwin(currentNode);
            }
        }
    }

    // Base case
    private Phylogeny baseCase(Phylogeny tree1, Phylogeny tree2) {
        PhylogenyNode[] tree1LeavesTopDown = getLeavesTopDown(tree1);
        PhylogenyNode[] tree2LeavesTopDown = getLeavesTopDown(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesTopDown.length; i++) {
            PhylogenyNode currentNode = tree1LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesTopDown);
        int[] lis = LongestIncreasingSubsequence.findLIS(numbers);

        int i = 0;
        Phylogeny tree = new Phylogeny();
        PhylogenyNode currentBottomMostNode = new PhylogenyNode();
        tree.setRoot(currentBottomMostNode);

        boolean hasFoundAdditionalLeaf = false;
        PhylogenyNode lastLeaf = new PhylogenyNode();
        for (PhylogenyNode currentLeaf : tree2LeavesTopDown){
            int currentLeafLisNumber = getMASTNodeDataFromNode(currentLeaf).getLisNumber();
            if(currentLeafLisNumber == lis[i]){
                if(i == lis.length-1){
                    lastLeaf.setName(currentLeaf.getName());
                }
                else {
                    PhylogenyNode newLeaf = new PhylogenyNode();
                    newLeaf.setName(currentLeaf.getName());
                    currentBottomMostNode.setChild1(newLeaf);
                    PhylogenyNode newNode = new PhylogenyNode();
                    currentBottomMostNode.setChild2(newNode);
                    currentBottomMostNode = newNode;

                    i++;
                }
            }
            else if(i == lis.length-1 && !hasFoundAdditionalLeaf && currentLeafLisNumber > lis[lis.length-2]){
                PhylogenyNode additionalLeaf = new PhylogenyNode();
                additionalLeaf.setName(currentLeaf.getName());
                currentBottomMostNode.setChild1(additionalLeaf);
                currentBottomMostNode = additionalLeaf;
                hasFoundAdditionalLeaf = true;
            }
        }
        currentBottomMostNode.getParent().setChild2(lastLeaf);

        return tree;
    }
    private Phylogeny baseCaseModified(Phylogeny tree1, Phylogeny tree2) {
        PhylogenyNode[] tree1LeavesBottomUp = getLeavesBottomUp(tree1);
        PhylogenyNode[] tree2LeavesBottomUp = getLeavesBottomUp(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesBottomUp.length; i++) {
            PhylogenyNode currentNode = tree1LeavesBottomUp[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesBottomUp);
        LongestIncreasingSubsequence lisFinder = new LongestIncreasingSubsequence();
        int[] lis = lisFinder.findLISModified(numbers);
        int[] lisLengths = lisFinder.getLisLengths();

        PhylogenyNodeIterator t2Iterator = tree2.iteratorLevelOrder();
        int j = numbers.length - 1;
        while (t2Iterator.hasNext()){
            PhylogenyNode currentNode = t2Iterator.next();
            MASTNodeData currentNodeMastNodeData = getMASTNodeDataFromNode(currentNode);
            if(currentNode.isExternal()){
                currentNodeMastNodeData.setSubtreeMASTSize(1);
            }
            else {
                currentNodeMastNodeData.setSubtreeMASTSize(lisLengths[j]);
                j--;
            }
        }

        int i = 0;
        Phylogeny tree = new Phylogeny();
        PhylogenyNode currentBottomMostNode = new PhylogenyNode();
        tree.setRoot(currentBottomMostNode);

        for (PhylogenyNode currentLeaf : tree2LeavesBottomUp){
            int currentLeafLisNumber = getMASTNodeDataFromNode(currentLeaf).getLisNumber();
            if(currentLeafLisNumber == lis[i]){
                if(i == lis.length-1){
                    currentBottomMostNode.setName(currentLeaf.getName());
                }
                else {
                    PhylogenyNode newLeaf = new PhylogenyNode();
                    newLeaf.setName(currentLeaf.getName());
                    currentBottomMostNode.setChild1(newLeaf);
                    PhylogenyNode newNode = new PhylogenyNode();
                    currentBottomMostNode.setChild2(newNode);
                    currentBottomMostNode = newNode;

                    i++;
                }
            }
        }

        return tree;
    }
    private int[] getLisNumbersFromLeaves(PhylogenyNode[] tree2LeavesTopDown) {
        int[] numbers = new int[tree2LeavesTopDown.length];
        for (int i = 0 ; i < tree2LeavesTopDown.length-2 ; i++){
            PhylogenyNode currentLeaf = tree2LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentLeaf);
            numbers[i] = mastNodeData.getLisNumber();
        }

        //TODO: remove all this
        PhylogenyNode secondLastLeaf = tree2LeavesTopDown[tree2LeavesTopDown.length-2];
        PhylogenyNode lastLeaf = tree2LeavesTopDown[tree2LeavesTopDown.length-1];
        int secondLastLisNumber = getMASTNodeDataFromNode(secondLastLeaf).getLisNumber();
        int lastLisNumber = getMASTNodeDataFromNode(lastLeaf).getLisNumber();
        if(secondLastLisNumber < lastLisNumber){
            numbers[tree2LeavesTopDown.length-2] = secondLastLisNumber;
            numbers[tree2LeavesTopDown.length-1] = lastLisNumber;
        }
        else {
            numbers[tree2LeavesTopDown.length-2] = lastLisNumber;
            numbers[tree2LeavesTopDown.length-1] = secondLastLisNumber;
        }
        return numbers;
    }
    private PhylogenyNode[] getLeavesTopDown(Phylogeny tree) {
        int numberOfLeaves = tree.getRoot().getNumberOfExternalNodes();
        PhylogenyNode[] treeLeavesTopDown = new PhylogenyNode[numberOfLeaves];
        PhylogenyNodeIterator iteratorLevelOrder = tree.iteratorLevelOrder();
        int i = 0;
        while (iteratorLevelOrder.hasNext()){
            PhylogenyNode currentNode = iteratorLevelOrder.next();
            if(currentNode.isExternal()){
                treeLeavesTopDown[i] = currentNode;
                i++;
            }
        }
        return treeLeavesTopDown;
    }
    private PhylogenyNode[] getLeavesBottomUp(Phylogeny tree) {
        int numberOfLeaves = tree.getRoot().getNumberOfExternalNodes();
        PhylogenyNode[] treeLeavesBottomUp = new PhylogenyNode[numberOfLeaves];
        PhylogenyNodeIterator iteratorLevelOrder = tree.iteratorLevelOrder();
        int i = numberOfLeaves-1;
        while (iteratorLevelOrder.hasNext()){
            PhylogenyNode currentNode = iteratorLevelOrder.next();
            if(currentNode.isExternal()){
                treeLeavesBottomUp[i] = currentNode;
                i--;
            }
        }
        return treeLeavesBottomUp;
    }

    // Decompositions
    public List<PhylogenyNode> computeFirstDecomposition(Phylogeny tree){
        List<PhylogenyNode> result = new ArrayList<>();

        PhylogenyNode currentNode = tree.getRoot();
        while (!currentNode.isExternal()){
            result.add(currentNode);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();
            currentNode = firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()
                    ? firstChild : secondChild;
        }
        result.add(currentNode);

        return result;
    }
    public List<List<PhylogenyNode>> computeSecondDecomposition(Phylogeny tree){
        List<List<PhylogenyNode>> result = new ArrayList<>();
        Stack<PhylogenyNode> remainingStartNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        if(root.isExternal()) return result;
        remainingStartNodes.add(root);

        while (!remainingStartNodes.isEmpty()){
            PhylogenyNode firstNode = remainingStartNodes.pop();
            List<PhylogenyNode> newPath = new ArrayList<>();
            PhylogenyNode currentNode = firstNode;

            while (!currentNode.isExternal()){
                newPath.add(currentNode);
                currentNode.setLink(firstNode);
                PhylogenyNode firstChild = currentNode.getChildNode1();
                PhylogenyNode secondChild = currentNode.getChildNode2();
                if(firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()){
                    currentNode = firstChild;
                    if(!secondChild.isExternal()) remainingStartNodes.push(secondChild);
                }
                else {
                    currentNode = secondChild;
                    if(!firstChild.isExternal()) remainingStartNodes.push(firstChild);
                }
            }
            newPath.add(currentNode);
            currentNode.setLink(firstNode);
            result.add(newPath);
        }

        return result;
    }

    // Induce Si subtrees
    public List<Phylogeny> induceSubtrees(List<PhylogenyNode> t1CentroidPath, Phylogeny tree1, Phylogeny tree2){
        int numberOfExternalNodes = tree1.getNumberOfExternalNodes();
        updateMiNumbers(t1CentroidPath);

        // Set T2 leaf numbers
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPreorder();
        int i = 0;
        while (tree2Iterator.hasNext()){
            PhylogenyNode currentNode = tree2Iterator.next();
            if(currentNode.isExternal()){
                getMASTNodeDataFromNode(currentNode).setLeafNumber(i);
                i++;
            }
        }

        PhylogenyNode[] sortedTree1Leaves = sortTree1Leaves(tree1);

        List<PhylogenyNode>[] sortedSiLeaves = new List[t1CentroidPath.size()];
        for (PhylogenyNode leaf : sortedTree1Leaves){
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(leaf);
            int miNumber = mastNodeData.getMiNumber();
            if (miNumber == 0) continue;
            if(sortedSiLeaves[miNumber] == null) sortedSiLeaves[miNumber] = new ArrayList<>();
            sortedSiLeaves[miNumber].add(mastNodeData.getTwin());
        }

        List<Phylogeny> result = new ArrayList<>();
        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree2);
        long time = System.nanoTime();
        for (List<PhylogenyNode> siLeaves : sortedSiLeaves){
            if(siLeaves == null) continue;
            result.add(subtreeProcessor.induceSubtree(siLeaves));
        }
        System.out.println((int)((System.nanoTime() - time)/(numberOfExternalNodes*(Math.log(numberOfExternalNodes)/Math.log(2)))));
        return result;
    }
    public void updateMiNumbers(List<PhylogenyNode> centroidPath){
        for (int i = 0; i < centroidPath.size() - 1; i++)
        {
            PhylogenyNode currentNode = centroidPath.get(i);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();

            PhylogenyNode miRootNode = (centroidPath.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;

            if (miRootNode.isExternal()) {
                MASTNodeData mastNodeData = ((NodeDataReference) miRootNode.getNodeData().getReference()).getMastNodeData();
                mastNodeData.setMiNumber(i+1);
            }
            else {
                for (PhylogenyNode sChild : miRootNode.getAllExternalDescendants()) {
                    MASTNodeData mastNodeData = ((NodeDataReference) sChild.getNodeData().getReference()).getMastNodeData();
                    mastNodeData.setMiNumber(i+1);
                }
            }
        }
    }
    public PhylogenyNode[] sortTree1Leaves(Phylogeny tree1){
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree1.getRoot().getNumberOfExternalNodes()];
        PhylogenyNodeIterator iterator = tree1.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            if(currentNode.isExternal()){
//                int name = Integer.parseInt(currentNode.getName());
                int leafNumber = getMASTNodeDataFromNode(getMASTNodeDataFromNode(currentNode).getTwin()).getLeafNumber();
                sortedTree1Leaves[leafNumber] = currentNode;
            }
        }
        return sortedTree1Leaves;
    }
    public PhylogenyNode[] sortTree1LeavesAndSetTwins_old(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree2Leaves = new ArrayList<>();
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPreorder();
        while (tree2Iterator.hasNext()){
            PhylogenyNode currentNode = tree2Iterator.next();
            if(currentNode.isExternal()) tree2Leaves.add(currentNode);
        }

        int[] tree2LeavesOrdering = getLeavesOrdering(tree2Leaves);
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree2LeavesOrdering.length];
        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPreorder();
        while (tree1Iterator.hasNext()){
            PhylogenyNode currentNode = tree1Iterator.next();
            if(currentNode.isExternal()){
                int name = Integer.parseInt(currentNode.getName());
                int tree2Index = tree2LeavesOrdering[name];
                sortedTree1Leaves[tree2Index] = currentNode;

                // Set twin
                MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
                mastNodeData.setTwin(tree2Leaves.get(tree2Index));
            }


        }
        return sortedTree1Leaves;
    }
    private int[] getLeavesOrdering(List<PhylogenyNode> tree2Leaves) {

        int[] treeLeavesOrdering = new int[tree2Leaves.size()];

        for (int i = 0; i < tree2Leaves.size(); i++) {
            PhylogenyNode currentNode = tree2Leaves.get(i);
            int index = Integer.parseInt(currentNode.getName());
            treeLeavesOrdering[index] = i;
        }
        return treeLeavesOrdering;
    }

    // MAST(Mi, Si)
    private void computeMiSiMASTs(List<PhylogenyNode> tree1Decomposition, List<Phylogeny> siSubtrees) {
        for (int i = 0; i < siSubtrees.size(); i++) {
            PhylogenyNode currentTree1DecompositionNode = tree1Decomposition.get(i);
            PhylogenyNode firstChild = currentTree1DecompositionNode.getChildNode1();
            PhylogenyNode secondChild = currentTree1DecompositionNode.getChildNode2();

            PhylogenyNode miRoot = (tree1Decomposition.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;
            Phylogeny mi = new Phylogeny();
            mi.setRoot(miRoot);
            mi = copyMiTreeAndSetTwins(mi);

            Phylogeny si = siSubtrees.get(i);
            getMAST(mi, si);
        }
    }
    private Phylogeny copyMiTreeAndSetTwins(Phylogeny tree){
        Phylogeny result = new Phylogeny();

        Stack<PhylogenyNodePair> remainingNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        PhylogenyNode newRoot = new PhylogenyNode();
        PhylogenyNodePair rootPair = new PhylogenyNodePair(root, newRoot);
        remainingNodes.push(rootPair);

        while(!remainingNodes.isEmpty()){
            PhylogenyNodePair nodePair = remainingNodes.pop();
            PhylogenyNode oldNode = nodePair.firstNode;
            PhylogenyNode newNode = nodePair.secondNode;

            // Add node data
            NodeDataReference nodeDataReference = new NodeDataReference();
            MASTNodeData newNodeMastNodeData = new MASTNodeData();
            nodeDataReference.setMastNodeData(newNodeMastNodeData);
            newNode.getNodeData().addReference(nodeDataReference);

            if(oldNode.isExternal()){
                newNode.setName(oldNode.getName());

                // set twins
                PhylogenyNode siTwin = getMASTNodeDataFromNode(getMASTNodeDataFromNode(oldNode).getTwin()).getSiNode();
                newNodeMastNodeData.setTwin(siTwin);
                getMASTNodeDataFromNode(siTwin).setTwin(newNode);

                continue;
            }
            PhylogenyNode newChild1 = new PhylogenyNode();
            PhylogenyNode newChild2 = new PhylogenyNode();
            newNode.setChild1(newChild1);
            newNode.setChild2(newChild2);
            remainingNodes.push(new PhylogenyNodePair(oldNode.getChildNode1(), newChild1));
            remainingNodes.push(new PhylogenyNodePair(oldNode.getChildNode2(), newChild2));
        }
        result.setRoot(newRoot);
        return result;
    }

    // Create graphs
    private Graph[] createGraphsAndComputeMASTs(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees) {
        Graph[] graphs = findAndAddGraphEdges(tree1Decomposition, tree2Decomposition, siSubtrees);
        setPathNumbers(tree1Decomposition, tree2Decomposition);

        // masts[i,j] = MAST(T1(x),T2(y)) where x is the i'th node of pi and y is the j'th node of X
        Phylogeny[][] masts = new Phylogeny[tree1Decomposition.size()-1][tree2Decomposition.size()];
        for (int i = graphs.length-1; i >= 0; i--) {
            Graph graph = graphs[i];
            setGraphEdgesWeights(graph, masts);
            computeMAST(graph, masts);
        }
        return graphs;
    }
    private Graph[] findAndAddGraphEdges(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees) {
        Graph[] graphs = new Graph[tree2Decomposition.size()];
        // add graphs and references to graphs
        for (int i = 0; i < tree2Decomposition.size(); i++) {
            List<PhylogenyNode> tree2CentroidPath = tree2Decomposition.get(i);
            Graph graph = new Graph(tree2CentroidPath);
            PhylogenyNode startNode = tree2CentroidPath.get(0);
            MASTNodeData startNodeData = getMASTNodeDataFromNode(startNode);
            startNodeData.setGraph(graph);
            graphs[i] = graph;
        }

        // add u_i edges to graphs
        for (int i = 0; i < siSubtrees.size(); i++) {
            Phylogeny si = siSubtrees.get(i);
            PhylogenyNode u_i = tree1Decomposition.get(i);

            PhylogenyNodeIterator siIterator = si.iteratorPreorder();
            while (siIterator.hasNext()){
                PhylogenyNode currentSiNode = siIterator.next();
                PhylogenyNode currentSiParent = currentSiNode.getParent();
                PhylogenyNode startOfSiParentCentroidPath;
                PhylogenyNode currentSiParentT2Node;
                if(currentSiParent != null){
                    currentSiParentT2Node = getMASTNodeDataFromNode(currentSiParent).getT2Node();
                    startOfSiParentCentroidPath = currentSiParentT2Node.getLink();
                }
                else {
                    startOfSiParentCentroidPath = null;
                    currentSiParentT2Node = null;
                }

                PhylogenyNode currentT2Node = getMASTNodeDataFromNode(currentSiNode).getT2Node();

                // walk up through tree2
                while (currentT2Node != null){
                    if(currentT2Node == currentSiParentT2Node) break; // parent has done it from here
                    PhylogenyNode startOfCentroidPath = currentT2Node.getLink();
                    if(startOfCentroidPath == null){ // currentT2Node is not on a path
                        currentT2Node = currentT2Node.getParent();
                        continue;
                    }
                    MASTNodeData startOfCentroidPathNodeData = getMASTNodeDataFromNode(startOfCentroidPath);
                    Graph currentGraph = startOfCentroidPathNodeData.getGraph();

                    GraphEdge newEdge = new GraphEdge(u_i, currentT2Node);
                    newEdge.setMapNode(currentSiNode);
                    currentGraph.addEdge(newEdge);
                    if(startOfCentroidPath == startOfSiParentCentroidPath) break; // parent has done it from here
                    currentT2Node = startOfCentroidPath.getParent();
                }
            }
        }

        // add u_p edges to graphs
        PhylogenyNode u_p = tree1Decomposition.get(tree1Decomposition.size()-1);
        findAndAddGraphEdgesFromLeaf(u_p);

        return graphs;
    }
    private void findAndAddGraphEdgesFromLeaf(PhylogenyNode leaf) {
        MASTNodeData mastNodeData = getMASTNodeDataFromNode(leaf);
        PhylogenyNode twin = mastNodeData.getTwin();
        PhylogenyNode currentT2Node = twin;

        while (currentT2Node != null){
            PhylogenyNode startOfCentroidPath = currentT2Node.getLink();
            if(startOfCentroidPath == null){ // if currentT2Node is not on a path
                currentT2Node = currentT2Node.getParent();
                continue;
            }
            MASTNodeData startOfCentroidPathNodeData = getMASTNodeDataFromNode(startOfCentroidPath);
            Graph currentGraph = startOfCentroidPathNodeData.getGraph();
            GraphEdge newEdge = new GraphEdge(leaf, currentT2Node);
            // TODO: set map node??
            currentGraph.addEdge(newEdge);
            currentT2Node = startOfCentroidPath.getParent();
        }
    }
    private void setPathNumbers(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition) {
        for (int i = 0 ; i < tree1Decomposition.size() ; i++){
            PhylogenyNode node = tree1Decomposition.get(i);
            getMASTNodeDataFromNode(node).setPathNumber(i);
        }
        for (int i = 0; i < tree2Decomposition.size(); i++) {
            PhylogenyNode node = tree2Decomposition.get(i).get(0);
            getMASTNodeDataFromNode(node).setPathNumber(i);
        }
    }
    private void setGraphEdgesWeights(Graph graph, Phylogeny[][] masts){
        for(GraphEdge edge : graph.getEdges()){
            PhylogenyNode leftNode = edge.getLeft();
            PhylogenyNode rightNode = edge.getRight();
            PhylogenyNode mapNode = edge.getMapNode();

            if(leftNode.isExternal() || rightNode.isExternal()){
                edge.setWhiteWeight(1);
                edge.setGreenWeight(1);
                edge.setRedWeight(1);
                continue;
            }

            int whiteWeight = computeWhiteWeight(rightNode, mapNode);
            edge.setWhiteWeight(whiteWeight);

            int greenWeight = getMASTNodeDataFromNode(mapNode).getSubtreeMASTSize();
            edge.setGreenWeight(greenWeight);

            int redWeight = computeRedWeight(leftNode, rightNode, masts);
            edge.setRedWeight(redWeight);

        }
    }
    private int computeWhiteWeight(PhylogenyNode rightNode, PhylogenyNode mapNode) {
        int whiteWeight = -1;
        if(getMASTNodeDataFromNode(mapNode).getT2Node() != rightNode){ // map(i,j) != v_j
            whiteWeight = getMASTNodeDataFromNode(mapNode).getSubtreeMASTSize();
        }
        else {
            PhylogenyNode mapNodeFirstChild = mapNode.getChildNode1();
            PhylogenyNode mapNodeSecondChild = mapNode.getChildNode2();
            PhylogenyNode rightNodeFirstChild = rightNode.getChildNode1();
//            PhylogenyNode rightNodeSecondChild = rightNode.getChildNode2();

//            Phylogeny n_j = new Phylogeny();

            // child is not on the same path as rightNode, i.e. root of N_j
            if(rightNodeFirstChild.getLink() != rightNode.getLink()){
                whiteWeight = getMASTNodeDataFromNode(mapNodeFirstChild).getSubtreeMASTSize();
//                n_j.setRoot(rightNodeFirstChild);
            }
            else {
                whiteWeight = getMASTNodeDataFromNode(mapNodeSecondChild).getSubtreeMASTSize();
//                n_j.setRoot(rightNodeSecondChild);
            }
            /*
            PhylogenyNodeIterator n_jIterator = n_j.iteratorPreorder();
            while (n_jIterator.hasNext()){
                PhylogenyNode currentNode = n_jIterator.next();
                if(currentNode == getMASTNodeDataFromNode(mapNodeFirstChild).getT2Node()){
                    whiteWeight = getMASTNodeDataFromNode(mapNodeFirstChild).getSubtreeMASTSize();
                    break;
                }
                if(currentNode == getMASTNodeDataFromNode(mapNodeSecondChild).getT2Node()){
                    whiteWeight = getMASTNodeDataFromNode(mapNodeSecondChild).getSubtreeMASTSize();
                    break;
                }
            }
            */
        }

        return whiteWeight;
    }
    private int computeRedWeight(PhylogenyNode leftNode, PhylogenyNode rightNode, Phylogeny[][] masts) {
        PhylogenyNode rightNodeFirstChild = rightNode.getChildNode1();
        PhylogenyNode rightNodeSecondChild = rightNode.getChildNode2();
        PhylogenyNode n_jRoot;
        if(rightNodeFirstChild.getLink() == null || rightNodeFirstChild.getLink() == rightNodeFirstChild){
            n_jRoot = rightNodeFirstChild;
        }
        else n_jRoot = rightNodeSecondChild;
        if(n_jRoot.isExternal()) return 1;
        int leftNodePathNumber = getMASTNodeDataFromNode(leftNode).getPathNumber();
        int n_jRootPathNumber = getMASTNodeDataFromNode(n_jRoot).getPathNumber();
        // TODO: return masts[leftNodePathNumber][n_jRootPathNumber].getRoot().getNumberOfExternalNodes();
        return 1;
    }

    // Compute agreement matchings
    private void computeMAST(Graph graph, Phylogeny[][] masts) {
        List<PhylogenyNode> rightSet = graph.getRightSet();
        double[] weights = setIndexNumbersAndGetWeights(graph, rightSet);
        Phylogeny searchTree = new WeightBalancedBinarySearchTree().constructTree(weights);
        MainFrame application = Archaeopteryx.createApplication(searchTree);

        List<GraphEdge> edges = graph.getEdges();
        PhylogenyNode previousLeftNode = new PhylogenyNode();
        PhylogenyNode previousSearchTreeNode = new PhylogenyNode();
        List<PhylogenyNode> previousAncestors = new ArrayList<>();
        for (int i = edges.size()-1; i >= 0; i--) {
            GraphEdge edge = edges.get(i);
            PhylogenyNode leftNode = edge.getLeft();
            PhylogenyNode rightNode = edge.getRight();
            int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

            // find node in search tree and get ancestors
            List<PhylogenyNode> ancestors = new ArrayList<>();
            PhylogenyNode currentSearchTreeNode;
            if(leftNode == previousLeftNode) {
                currentSearchTreeNode = previousSearchTreeNode;
                ancestors = previousAncestors;

                while (rightNodeIndex < getSearchTreeNodeData(currentSearchTreeNode).getLowIndex()){
                    currentSearchTreeNode = currentSearchTreeNode.getParent();
                    ancestors.remove(ancestors.size()-1);
                }
                currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
            }
            else
                currentSearchTreeNode = searchTree.getRoot();
            while (true){
                ancestors.add(currentSearchTreeNode);
                if(currentSearchTreeNode.isExternal()) break;
                SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                if (rightNodeIndex < searchTreeNodeData.getIndex())
                    currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
            }

            processWhiteEdge(ancestors, edge);
            processRedEdge(ancestors, edge);
            processGreenEdge(ancestors, edge);

            previousLeftNode = leftNode;
            previousSearchTreeNode = currentSearchTreeNode;
            previousAncestors = ancestors;
        }
        application.dispose();
    }
    private double[] setIndexNumbersAndGetWeights(Graph graph, List<PhylogenyNode> rightSet) {
        double[] weights = new double[rightSet.size()];

        // Set index numbers and weights
        int rightSetSubtreeSize = rightSet.get(0).getNumberOfExternalNodes();
        for (int j = 0; j < rightSet.size(); j++) {
            PhylogenyNode currentNode = rightSet.get(j);
            GraphNodeData graphNodeData = getGraphNodeData(currentNode);
            graphNodeData.setIndex(j);

            int nj;
            if(currentNode.isExternal()){
                nj = 1;
            }
            else {
                PhylogenyNode firstChild = currentNode.getChildNode1();
                PhylogenyNode secondChild = currentNode.getChildNode2();
                if(firstChild == rightSet.get(j+1)){
                    nj = secondChild.getNumberOfExternalNodes();
                }
                else nj = firstChild.getNumberOfExternalNodes();
            }
            if(graphNodeData.hasNonSingletonEdge()){
                weights[j] = nj + (double)rightSetSubtreeSize/graph.getNsav();
            }
            else weights[j] = nj;

        }
        return weights;
    }
    private GraphNodeData getGraphNodeData(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getGraphNodeData();
    }
    private SearchTreeNodeData getSearchTreeNodeData(PhylogenyNode node){
        return (SearchTreeNodeData) node.getNodeData().getReference();
    }
    private void processWhiteEdge(List<PhylogenyNode> ancestors, GraphEdge edge){
        List<PhylogenyNode> rfringe = getFringe(ancestors, false);

        // find largest m, largest x and largest y
        AgreementMatching maxM = null;
        int maxMWeight = 0;
        ProperCrossing maxX = null;
        int maxXWeight = 0;
        ProperCrossing maxY = null;
        int maxYWeight = 0;
        for (PhylogenyNode currentNode : rfringe){
            SearchTreeNodeData nodeData = getSearchTreeNodeData(currentNode);
            AgreementMatching m = nodeData.getM();
            // TODO: exclude matchings whith topmost edge from u_i
            if(m != null){
                int mWeight = m.getWeight();
                if(mWeight > maxMWeight){
                    maxM = m;
                    maxMWeight = mWeight;
                }
            }

            ProperCrossing x = nodeData.getX();
            // TODO: exclude proper crossings whith topmost edge from u_i
            if(x != null){
                int xWeight = x.getWeight();
                if(xWeight > maxXWeight){
                    maxX = x;
                    maxXWeight = xWeight;
                }
            }

            ProperCrossing y = nodeData.getY();
            // TODO: exclude proper crossings whith topmost edge from u_i
            if(y != null){
                int yWeight = y.getWeight();
                if(yWeight > maxYWeight){
                    maxY = y;
                    maxYWeight = yWeight;
                }
            }
        }

        // find largest g,r crossing
        ProperCrossing maxGR = findLargestGRCrossing(ancestors);


        // find largst agreement matching with 'edge' as topmost white edge
        ProperCrossing largestProperCrossing = null;
        int largestProperCrossingWeight = 0;
        // largest proper crossing
        int maxGRWeight = maxGR == null ? 0 : maxGR.getWeight();
        if(maxXWeight > maxGRWeight){
            largestProperCrossing = maxX;
            largestProperCrossingWeight = maxXWeight;
        }
        else {
            largestProperCrossing = maxGR;
            largestProperCrossingWeight = maxGRWeight;
        }
        if(maxYWeight > largestProperCrossingWeight){
            largestProperCrossing = maxY;
            largestProperCrossingWeight = maxYWeight;
        }
        // largest agreement matching
        AgreementMatching largestAgreementMatching;
        if(maxMWeight > largestProperCrossingWeight){
            largestAgreementMatching = maxM;
            largestAgreementMatching.addWhiteEdge(edge);
        }
        else if(largestProperCrossing == null) largestAgreementMatching = null;
        else {
            List<GraphEdge> whiteEdges = new ArrayList<>();
            whiteEdges.add(edge);
            int matchingWeight = largestProperCrossingWeight + edge.getWhiteWeight();
            largestAgreementMatching = new AgreementMatching(largestProperCrossing, whiteEdges, matchingWeight);
        }

        // add agreement matching to graph
        if(largestAgreementMatching != null){
            updateM(ancestors, largestAgreementMatching);
        }
    }
    private ProperCrossing findLargestGRCrossing(List<PhylogenyNode> ancestors) {
        // TODO: exclude proper crossings whith topmost edge from u_i
        ProperCrossing maxGR = null;
        int maxGRWeight = 0;
        int currentMaxAncestorGWeight = 0;
        GraphEdge currentMaxAncestorG = null;
        for (int i = 0; i < ancestors.size()-1; i++) {
            PhylogenyNode currentAncestor = ancestors.get(i);
            SearchTreeNodeData nodeData = getSearchTreeNodeData(currentAncestor);
            GraphEdge ancestorG = nodeData.getG();
            if(ancestorG != null){
                int ancestorGWeight = ancestorG.getGreenWeight();
                if(ancestorGWeight > currentMaxAncestorGWeight){
                    currentMaxAncestorGWeight = ancestorGWeight;
                    currentMaxAncestorG = ancestorG;
                }
            }
            PhylogenyNode child = currentAncestor.getChildNode2();
            if(child != ancestors.get(i+1)){
                SearchTreeNodeData childData = getSearchTreeNodeData(child);
                GraphEdge r = childData.getR();
                if(r == null) continue;

                GraphEdge g = childData.getG();
                GraphEdge maxG;
                int maxGWeight;
                if(g == null || g.getGreenWeight() <= currentMaxAncestorGWeight){
                    if(currentMaxAncestorG == null) continue;
                    maxG = currentMaxAncestorG;
                    maxGWeight = currentMaxAncestorGWeight;
                }
                else {
                    maxG = g;
                    maxGWeight = g.getGreenWeight();
                }

                int gRWeight = maxGWeight + r.getRedWeight();
                if(gRWeight > maxGRWeight){
                    maxGRWeight = gRWeight;
                    maxGR = new ProperCrossing(maxG, r);
                }
            }
        }
        return maxGR;
    }
    private void updateM(List<PhylogenyNode> ancestors, AgreementMatching largestAgreementMatching) {
        int largestAgreementMatchingWeight = largestAgreementMatching.getWeight();
        for (PhylogenyNode currentNode : ancestors){
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            AgreementMatching currentM = currentNodeData.getM();
            if(currentM == null || largestAgreementMatchingWeight > currentM.getWeight()) currentNodeData.setM(largestAgreementMatching);
        }
    }
    private void processRedEdge(List<PhylogenyNode> ancestors, GraphEdge edge){
        // update y(z) for z in ancestors
        // update g(y) for y in lfringe(z) and rfringe(z), z in ancestors
        // update r(z) for z in ancestors
        updateYAndGAndR(ancestors, edge);

        // remove g(z) for z in ancestors
        for (PhylogenyNode currentNode : ancestors){
            getSearchTreeNodeData(currentNode).setG(null);
        }
    }
    private void updateYAndGAndR(List<PhylogenyNode> ancestors, GraphEdge edge) {
        int currentMaxAncestorGWeight = 0;
        GraphEdge currentMaxAncestorG = null;
        for (int i = 0; i < ancestors.size(); i++) {
            PhylogenyNode currentNode = ancestors.get(i);
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            GraphEdge g = currentNodeData.getG();
            if(g != null && g.getGreenWeight() > currentMaxAncestorGWeight){
                currentMaxAncestorG = g;
                currentMaxAncestorGWeight = g.getGreenWeight();
            }
            if(currentMaxAncestorG != null){
                // update g
                if(i != ancestors.size()-1){
                    PhylogenyNode leftChild = currentNode.getChildNode1();
                    PhylogenyNode rightChild = currentNode.getChildNode2();
                    PhylogenyNode childToUpdate;
                    if(leftChild != ancestors.get(i+1)) childToUpdate = leftChild;
                    else childToUpdate = rightChild;
                    SearchTreeNodeData childToUpdateData = getSearchTreeNodeData(childToUpdate);
                    GraphEdge childG = childToUpdateData.getG();
                    if(childG == null || currentMaxAncestorGWeight > childG.getGreenWeight())
                        currentNodeData.setG(currentMaxAncestorG);
                }

                // update y
                GraphEdge r = currentNodeData.getR();
                if(r != null){
                    int gRWeight = currentMaxAncestorGWeight + r.getRedWeight();
                    ProperCrossing currentY = currentNodeData.getY();
                    if(currentY == null || gRWeight > currentY.getWeight())
                        currentNodeData.setY(new ProperCrossing(currentMaxAncestorG, r));
                }
            }

            // update r
            GraphEdge r = currentNodeData.getR();
            if(r == null || edge.getRedWeight() > r.getRedWeight())
                currentNodeData.setR(edge);
        }
    }
    private void processGreenEdge(List<PhylogenyNode> ancestors, GraphEdge edge){
        // update g(z) for z in lfringe
        updateG(ancestors, edge);

        // update x(z) for z in ancestors
        updateX(ancestors, edge);
    }
    private void updateG(List<PhylogenyNode> ancestors, GraphEdge edge) {
        List<PhylogenyNode> lfringe = getFringe(ancestors, true);
        for (PhylogenyNode currentNode : lfringe){
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            GraphEdge g = currentNodeData.getG();
            if(g == null || edge.getGreenWeight() > g.getGreenWeight())
                currentNodeData.setG(edge);
        }
    }
    private void updateX(List<PhylogenyNode> ancestors, GraphEdge edge) {
        PhylogenyNode leaf = ancestors.get(ancestors.size() - 1);
        SearchTreeNodeData leafData = getSearchTreeNodeData(leaf);
        ProperCrossing leafX = leafData.getX();
        if(leafX == null || edge.getGreenWeight() > leafX.getWeight())
            leafData.setX(new ProperCrossing(edge, null));

        GraphEdge currentMaxLfringeR = null;
        int currentMaxLfringeRweight = 0;
        for (int i = ancestors.size()-2; i >= 0; i--) {
            PhylogenyNode currentNode = ancestors.get(i);
            PhylogenyNode leftChild = currentNode.getChildNode1();
            if(leftChild != ancestors.get(i+1)){
                SearchTreeNodeData leftChildData = getSearchTreeNodeData(leftChild);
                GraphEdge leftChildR = leftChildData.getR();
                if(leftChildR != null && leftChildR.getRedWeight() > currentMaxLfringeRweight){
                    currentMaxLfringeR = leftChildR;
                    currentMaxLfringeRweight = leftChildR.getRedWeight();
                }
            }
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            ProperCrossing x = currentNodeData.getX();
            if(x == null || currentMaxLfringeRweight+edge.getGreenWeight() > x.getWeight())
                currentNodeData.setX(new ProperCrossing(edge, currentMaxLfringeR));
        }
    }
    private List<PhylogenyNode> getFringe(List<PhylogenyNode> ancestors, boolean left) {
        List<PhylogenyNode> fringe = new ArrayList<>();
        for (int i = 0; i < ancestors.size(); i++) {
            PhylogenyNode currentNode = ancestors.get(i);
            if(currentNode.isExternal()) break;
            PhylogenyNode child = left ? currentNode.getChildNode1() : currentNode.getChildNode2();
            if(child != ancestors.get(i+1)) fringe.add(child);
        }
        return fringe;
    }

    // Helper methods
    private MASTNodeData getMASTNodeDataFromNode(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getMastNodeData();
    }
    private class PhylogenyNodePair {
        private PhylogenyNode firstNode;
        private PhylogenyNode secondNode;

        public PhylogenyNodePair(PhylogenyNode firstNode, PhylogenyNode secondNode) {
            this.firstNode = firstNode;
            this.secondNode = secondNode;
        }

        public PhylogenyNode getFirstNode() {
            return firstNode;
        }

        public void setFirstNode(PhylogenyNode firstNode) {
            this.firstNode = firstNode;
        }

        public PhylogenyNode getSecondNode() {
            return secondNode;
        }

        public void setSecondNode(PhylogenyNode secondNode) {
            this.secondNode = secondNode;
        }
    }
}
