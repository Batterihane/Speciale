package Utilities;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by Thomas on 04-03-2016.
 */
public class CompleteBinaryTreeLCA {
    private Phylogeny tree;
    private int treeHeight;
    private PhylogenyNode[] pathNumberOrderedNodes;

    public CompleteBinaryTreeLCA(int numberOfLeaves){
        treeHeight = (int)(Math.log(numberOfLeaves)/Math.log(2));
        int numberOfNodes = 2*numberOfLeaves-1;
        pathNumberOrderedNodes = new PhylogenyNode[numberOfNodes+1];
        Phylogeny tree = createTree(numberOfLeaves);
        preprocessTree(tree);
        this.tree = tree;
    }

    public Phylogeny getTree() {
        return tree;
    }

    public static void main(String[] args) {
//        System.out.println((int)(Math.log(32)/Math.log(2)));
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        CompleteBinaryTreeLCA completeBinaryTreeLCA = new CompleteBinaryTreeLCA(32);
        Phylogeny tree = completeBinaryTreeLCA.getTree();
//        foresterNewickParser.displayPhylogeny(tree);
        completeBinaryTreeLCA.getLCA(completeBinaryTreeLCA.getNodeFromPathNumber(10), completeBinaryTreeLCA.getNodeFromPathNumber(15));
//        int x = 3387;
//        x = x >> 3;
//        x |= 0b1;
//        x = x << 3;
//        System.out.println(x);
    }

    public PhylogenyNode getNodeFromPathNumber(int pathNumber){
        return pathNumberOrderedNodes[pathNumber];
    }

    public PhylogenyNode getLCA(PhylogenyNode node1, PhylogenyNode node2){
        if(isAncestorOf(node1, node2)) return node1;
        if(isAncestorOf(node2, node1)) return node2;

        LCANodeData node1Data = (LCANodeData) node1.getNodeData().getReference();
        LCANodeData node2Data = (LCANodeData) node2.getNodeData().getReference();
        int node1PathNumber = node1Data.getPathNumber();
        int node2PathNumber = node2Data.getPathNumber();

        int xor = node1PathNumber ^ node2PathNumber;
        int mostSignificant1Bit = BitSet.valueOf(new long[] { xor }).previousSetBit(treeHeight);

        int resultPathNumber = node1PathNumber >> mostSignificant1Bit;
        resultPathNumber |= 0b1;
        resultPathNumber = resultPathNumber << mostSignificant1Bit;

        System.out.println(resultPathNumber);
        return pathNumberOrderedNodes[resultPathNumber];
    }

    private boolean isAncestorOf(PhylogenyNode node1, PhylogenyNode node2){
        LCANodeData node1Data = (LCANodeData) node1.getNodeData().getReference();
        LCANodeData node2Data = (LCANodeData) node2.getNodeData().getReference();
        int node1DfsNumber = node1Data.getDfsNumber();
        int node2DfsNumber = node2Data.getDfsNumber();
        int node1SubtreeNodeCount = node1Data.getSubtreeNodeCount();

        return node1DfsNumber < node2DfsNumber && node2DfsNumber < node1DfsNumber + node1SubtreeNodeCount;
    }

    private Phylogeny createTree(int numberOfLeaves) {
        Phylogeny tree = new Phylogeny();
        Queue<PhylogenyNode> nodes = new LinkedList<>();
        for (int i = 0; i < numberOfLeaves; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.getNodeData().addReference(new LCANodeData());
            nodes.add(newNode);
        }

        while (nodes.size() > 1){
            PhylogenyNode node1 = nodes.poll();
            PhylogenyNode node2 = nodes.poll();
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.getNodeData().addReference(new LCANodeData());
            newNode.setChild1(node1);
            newNode.setChild2(node2);
            nodes.add(newNode);
        }
        PhylogenyNode root = nodes.poll();
        tree.setRoot(root);
        return tree;
    }

    private void preprocessTree(Phylogeny tree) {
        addDFSNumberings(tree);
        addPathNumberings(tree);
        addSubtreeNodeCount(tree);
    }

    private void addDFSNumberings(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        for (int i = 0 ; iterator.hasNext() ; i++){
            PhylogenyNode currentNode = iterator.next();
            ((LCANodeData)currentNode.getNodeData().getReference()).setDfsNumber(i);
        }
    }

    private void addPathNumberings(Phylogeny tree) {
        Stack<NodeNumberPair> stack = new Stack();
        stack.push(new NodeNumberPair(tree.getRoot(), 1));

        int i = 1;
        while (!stack.isEmpty()){
            NodeNumberPair topPair = stack.pop();
            PhylogenyNode node = topPair.getNode();
            int phase = topPair.getNumber();
            if(phase >= node.getNumberOfDescendants()){
                ((LCANodeData)node.getNodeData().getReference()).setPathNumber(i);
                pathNumberOrderedNodes[i] = node;
                i++;
                if(!node.isExternal()){
                    stack.push(new NodeNumberPair(node.getChildNode2(), 1));
                }
                continue;
            }
            topPair.setNumber(phase+1);
            stack.push(topPair);
            stack.push(new NodeNumberPair(node.getChildNode1(), 1));
        }
    }

    private void addSubtreeNodeCount(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            int subtreeNodeCount;
            if(currentNode.isExternal()) subtreeNodeCount = 1;
            else {
                int leftChildsubtreeNodeCount = ((LCANodeData) currentNode.getChildNode1().getNodeData().getReference()).getSubtreeNodeCount();
                int rightChildsubtreeNodeCount = ((LCANodeData) currentNode.getChildNode2().getNodeData().getReference()).getSubtreeNodeCount();
                subtreeNodeCount = leftChildsubtreeNodeCount + rightChildsubtreeNodeCount + 1;
            }
            ((LCANodeData)currentNode.getNodeData().getReference()).setSubtreeNodeCount(subtreeNodeCount);
        }
    }

    private class LCANodeData extends Reference {
        private int dfsNumber;
        private int pathNumber;
        private int subtreeNodeCount;

        public LCANodeData() {
            super("");
        }

        public int getSubtreeNodeCount() {
            return subtreeNodeCount;
        }

        public void setSubtreeNodeCount(int subtreeNodeCount) {
            this.subtreeNodeCount = subtreeNodeCount;
        }

        public int getDfsNumber() {
            return dfsNumber;
        }

        public void setDfsNumber(int dfsNumber) {
            this.dfsNumber = dfsNumber;
        }

        public int getPathNumber() {
            return pathNumber;
        }

        public void setPathNumber(int pathNumber) {
            this.pathNumber = pathNumber;
        }
    }

    private class NodeNumberPair {
        private PhylogenyNode node;
        private int number;

        public NodeNumberPair(PhylogenyNode node, int number){
            this.node = node;
            this.number = number;
        }

        public PhylogenyNode getNode() {
            return node;
        }

        public void setNode(PhylogenyNode node) {
            this.node = node;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

}
