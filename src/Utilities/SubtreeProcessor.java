package Utilities;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.Stack;

/**
 * Created by Thomas on 29-02-2016.
 */
public class SubtreeProcessor {
    private int[] depths;
    private int treeSize;
    private LCA lca;
    private int maxDepth;

    public SubtreeProcessor(Phylogeny tree){
        computeDepths(tree);
        lca = new LCA(tree);
    }

    private void computeDepths(Phylogeny tree) {
        treeSize = tree.getNodeCount();
        depths = new int[treeSize];
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            PhylogenyNode parent = currentNode.getParent();
            if(parent == null) continue;

            int depth = depths[parent.getId() % treeSize] + 1;
            depths[currentNode.getId()%treeSize] = depth;
            if(depth > maxDepth) maxDepth = depth;
        }
    }

    public Phylogeny induceSubtree(PhylogenyNode[] nodes){
        PhylogenyNode[] subtreeNodes = computeSubtreeNodes(nodes);
        Stack<Integer>[] nodeBuckets = computeNodeBuckets(subtreeNodes);
        IntegerPair[] leftRightIndexes = computeInitialLeftRightIndexes(subtreeNodes.length);
        updateLeftRightIndexes(nodeBuckets, leftRightIndexes);

        Phylogeny result = computeSubtree(subtreeNodes, leftRightIndexes);

        return result;
    }

    private Phylogeny computeSubtree(PhylogenyNode[] subtreeNodes, IntegerPair[] leftRightIndexes) {
        Phylogeny result = new Phylogeny();
        PhylogenyNode[] newSubtreeNodes = new PhylogenyNode[subtreeNodes.length];
        for (int i = 0; i < subtreeNodes.length; i++) {
            PhylogenyNode currentNode = subtreeNodes[i];
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(currentNode.getName());
            newSubtreeNodes[i] = newNode;
        }
        for (int i = 0; i < subtreeNodes.length; i++) {
            int leftIndex = leftRightIndexes[i].getLeft();
            int rightIndex = leftRightIndexes[i].getRight();
            int leftDepth = -1, rightDepth = -1;
            if(leftIndex != -1){
                PhylogenyNode leftNode = subtreeNodes[leftIndex];
                leftDepth = depths[leftNode.getId()%treeSize];
            }
            if(rightIndex != -1){
                PhylogenyNode rightNode = subtreeNodes[rightIndex];
                rightDepth = depths[rightNode.getId()%treeSize];
            }

            PhylogenyNode currentNode = newSubtreeNodes[i];
            if(leftDepth > rightDepth){
                PhylogenyNode parent = newSubtreeNodes[leftIndex];
                parent.setChild2(currentNode);
            }
            else if(rightDepth != -1){
                PhylogenyNode parent = newSubtreeNodes[rightIndex];
                parent.setChild1(currentNode);
            }
            else {
                result.setRoot(currentNode);
            }
        }
        return result;
    }

    private void updateLeftRightIndexes(Stack<Integer>[] nodeBuckets, IntegerPair[] leftRightIndexes) {
        for (int i = nodeBuckets.length-1; i >= 0; i--) {
            Stack<Integer> currentBucket = nodeBuckets[i];
            if(currentBucket == null) continue;

            while (!currentBucket.isEmpty()){
                int currentIndex = currentBucket.pop();
                IntegerPair currentIntegerPair = leftRightIndexes[currentIndex];
                int precedingIndex = currentIntegerPair.getLeft();
                int followingIndex = currentIntegerPair.getRight();
                if(precedingIndex != -1) leftRightIndexes[precedingIndex].setRight(currentIntegerPair.getRight());
                if(followingIndex != -1) leftRightIndexes[followingIndex].setLeft(currentIntegerPair.getLeft());
            }
        }
    }

    private IntegerPair[] computeInitialLeftRightIndexes(int length) {
        IntegerPair[] linkedSubtreeNodes = new IntegerPair[length];
        linkedSubtreeNodes[0] = new IntegerPair(-1, 1);
        for (int i = 1; i < length-1; i++) {
            linkedSubtreeNodes[i] = new IntegerPair(i-1, i+1);
        }
        linkedSubtreeNodes[length-1] = new IntegerPair(length-2, -1);
        return linkedSubtreeNodes;
    }

    private PhylogenyNode[] computeSubtreeNodes(PhylogenyNode[] nodes) {
        int subtreeSize = nodes.length * 2 - 1;
        PhylogenyNode[] subtreeNodes = new PhylogenyNode[subtreeSize];

        for (int i = 0; i < nodes.length-1; i++) {
            subtreeNodes[i*2] = nodes[i];
            subtreeNodes[i*2+1] = lca.getLCA(nodes[i], nodes[i+1]);
        }
        subtreeNodes[(nodes.length-1)*2] = nodes[nodes.length-1];
        return subtreeNodes;
    }

    private Stack<Integer>[] computeNodeBuckets(PhylogenyNode[] subtreeNodes){
        Stack<Integer>[] result = new Stack[maxDepth+1];
        for (int i = 0; i < subtreeNodes.length; i++) {
            PhylogenyNode node = subtreeNodes[i];
            int depth = depths[node.getId() % treeSize];
            Stack<Integer> bucket = result[depth];
            if(bucket == null){
                bucket = new Stack<>();
                result[depth] = bucket;
            }
            bucket.push(i);
        }
        return result;
    }

    private class IntegerPair {
        private int left;
        private int right;

        public IntegerPair(int left, int right){
            this.left = left;
            this.right = right;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }
    }

}