package nlogn;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import Utilities.LongestIncreasingSubsequence;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
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

        Phylogeny tree1 = PhylogenyGenerator.generateTree(5);
        Phylogeny tree2 = PhylogenyGenerator.generateTree(5);
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

        // old base case
//        if(tree1Decomposition.size() == numberOfLeaves && tree2Decomposition.size() == 1){
//            return baseCaseModified(tree2, tree1);
//        }

        List<Phylogeny> siSubtrees = induceSubtrees(tree1Decomposition, tree1, tree2);

        computeMiSiMASTs(tree1Decomposition, siSubtrees);

        createGraphs(tree1Decomposition, tree2Decomposition, siSubtrees);

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
        PhylogenyNode[] tree1LeavesTopDown = getLeavesTopDownAndSetNumbers(tree1);
        PhylogenyNode[] tree2LeavesTopDown = getLeavesTopDownAndSetNumbers(tree2);

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
        PhylogenyNode[] tree1LeavesTopDown = getLeavesTopDownAndSetNumbers(tree1);
        PhylogenyNode[] tree2LeavesTopDown = getLeavesTopDownAndSetNumbers(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesTopDown.length; i++) {
            PhylogenyNode currentNode = tree1LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesTopDown);
        int[] lis = LongestIncreasingSubsequence.findLISModified(numbers);

        int i = 0;
        Phylogeny tree = new Phylogeny();
        PhylogenyNode currentBottomMostNode = new PhylogenyNode();
        tree.setRoot(currentBottomMostNode);

        for (PhylogenyNode currentLeaf : tree2LeavesTopDown){
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
    private PhylogenyNode[] getLeavesTopDownAndSetNumbers(Phylogeny tree) {
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
    private Graph[] createGraphs(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees) {
        Graph[] graphs = findAndAddGraphEdges(tree1Decomposition, tree2Decomposition, siSubtrees);
        setPathNumbers(tree1Decomposition, tree2Decomposition);

        // masts[i,j] = MAST(x,y) where x is the i'th node of pi and y is the j'th node of X
        Phylogeny[][] masts = new Phylogeny[tree1Decomposition.size()-1][tree2Decomposition.size()];
        for (int i = graphs.length-1; i >= 0; i--) {
            Graph graph = graphs[i];
            setGraphEdgesWeights(graph, masts);
            // computeMAST(graph, masts);
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

            if(leftNode.isExternal() || mapNode.isExternal()){
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
            PhylogenyNode rightNodeSecondChild = rightNode.getChildNode2();

            Phylogeny n_j = new Phylogeny();

            // child is not on a path or it is the first node in its path, i.e. root of N_j
            if(rightNodeFirstChild.getLink() == null || rightNodeFirstChild.getLink() == rightNodeFirstChild){
                n_j.setRoot(rightNodeFirstChild);
            }
            else n_j.setRoot(rightNodeSecondChild);

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
        return masts[leftNodePathNumber][n_jRootPathNumber].getRoot().getNumberOfExternalNodes();
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
