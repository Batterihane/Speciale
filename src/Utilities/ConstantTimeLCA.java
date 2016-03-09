package Utilities;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.BitSet;

/**
 * Created by Thomas on 04-03-2016.
 */
public class ConstantTimeLCA {

    private PhylogenyNode[] headsOfRuns;
    private CompleteBinaryTreeLCA completeBinaryTreeLCAFinder;

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree = PhylogenyGenerator.generateTree(5);
        foresterNewickParser.displayPhylogeny(tree);
        ConstantTimeLCA lca = new ConstantTimeLCA(tree);
        lca.getLCA(tree.getNode("Leaf_0"), tree.getNode("Leaf_4"));
        tree.getNodeCount();
    }

    public ConstantTimeLCA(Phylogeny tree){
        preprocessTree(tree);
    }

    private void preprocessTree(Phylogeny tree) {
        addLCANodeDataAndDFSNumberingsAndLSBIndex(tree);
        addMaxHeightSubtreeNodesAndHeadsOfRuns(tree);
        completeBinaryTreeLCAFinder = createCompleteBinaryTreeLCAAndAddMappings(tree);
        addBitNumbers(tree);
    }

    public PhylogenyNode getLCA(PhylogenyNode node1, PhylogenyNode node2){
        // Find h(I(z)):
        LCANodeData node1Data = (LCANodeData) node1.getNodeData().getReference();
        LCANodeData node2Data = (LCANodeData) node2.getNodeData().getReference();
        PhylogenyNode node1CompleteBinaryTreeNode = node1Data.getCompleteBinaryTreeNode();
        PhylogenyNode node2CompleteBinaryTreeNode = node2Data.getCompleteBinaryTreeNode();
        PhylogenyNode completeBinaryTreeLCA = completeBinaryTreeLCAFinder.getLCA(node1CompleteBinaryTreeNode, node2CompleteBinaryTreeNode); // b
        int cbtLCAHeight = ((CompleteBinaryTreeLCANodeData) completeBinaryTreeLCA.getNodeData().getReference()).getHeight(); // h(b)
        int node1BitNumber = node1Data.getBitNumber();
        int node2BitNumber = node2Data.getBitNumber();
        int k = node1BitNumber & node2BitNumber;
        k = k >> cbtLCAHeight-1;
        k = k << cbtLCAHeight-1;
        int lcaMaxHeightSubtreeNodeHeight = BitSet.valueOf(new long[] { k }).nextSetBit(0) + 1; // h(I(z))
        System.out.println(lcaMaxHeightSubtreeNodeHeight);

        // Find z:
        throw new NotImplementedException();
    }

    private void addLCANodeDataAndDFSNumberingsAndLSBIndex(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        for (int i = 1 ; iterator.hasNext() ; i++){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData nodeData = new LCANodeData();
            nodeData.setDfsNumber(i);
            int leastSignificant1BitIndex = BitSet.valueOf(new long[] { i }).nextSetBit(0); //TODO: avoid BitSet
            nodeData.setLeastSignificant1BitIndex(leastSignificant1BitIndex+1);
            currentNode.getNodeData().addReference(nodeData);
        }
    }

    private void addMaxHeightSubtreeNodesAndHeadsOfRuns(Phylogeny tree) {
        headsOfRuns = new PhylogenyNode[tree.getNodeCount()+1];
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData currentNodeData = (LCANodeData) currentNode.getNodeData().getReference();
            PhylogenyNode maxHeightSubtreeNode;

            if(currentNode.isExternal()) {
                maxHeightSubtreeNode = currentNode;
            }
            else{
                int lsbIndex = currentNodeData.getLeastSignificant1BitIndex();
                PhylogenyNode child1 = currentNode.getChildNode1();
                PhylogenyNode child2 = currentNode.getChildNode2();
                LCANodeData child1NodeData = (LCANodeData) child1.getNodeData().getReference();
                LCANodeData child2NodeData = (LCANodeData) child2.getNodeData().getReference();
//                int child1LSBIndex = child1NodeData.getLeastSignificant1BitIndex(); // According to Gusfield??
//                int child2LSBIndex = child2NodeData.getLeastSignificant1BitIndex();
                PhylogenyNode child1MaxHeightSubtreeNode = child1NodeData.getMaxHeightSubtreeNode();
                PhylogenyNode child2MaxHeightSubtreeNode = child2NodeData.getMaxHeightSubtreeNode();
                int child1LSBIndex = ((LCANodeData)child1MaxHeightSubtreeNode.getNodeData().getReference()).getLeastSignificant1BitIndex();
                int child2LSBIndex = ((LCANodeData)child2MaxHeightSubtreeNode.getNodeData().getReference()).getLeastSignificant1BitIndex();

                if(lsbIndex > child1LSBIndex && lsbIndex > child2LSBIndex){
                    maxHeightSubtreeNode = currentNode;
                }
                else if(child1LSBIndex > child2LSBIndex) maxHeightSubtreeNode = child1MaxHeightSubtreeNode;
                else maxHeightSubtreeNode = child2MaxHeightSubtreeNode;

                // set heads of runs
                if(child1MaxHeightSubtreeNode != maxHeightSubtreeNode){
                    int child1MaxHeightSubtreeNodeNumber = ((LCANodeData)child1MaxHeightSubtreeNode.getNodeData().getReference()).getDfsNumber();
                    headsOfRuns[child1MaxHeightSubtreeNodeNumber] = child1;
                }
                if(child2MaxHeightSubtreeNode != maxHeightSubtreeNode){
                    int child2MaxHeightSubtreeNodeNumber = ((LCANodeData)child2MaxHeightSubtreeNode.getNodeData().getReference()).getDfsNumber();
                    headsOfRuns[child2MaxHeightSubtreeNodeNumber] = child2;
                }
                if(currentNode.isRoot()){
                    int maxHeightSubtreeNodeNumber = ((LCANodeData)maxHeightSubtreeNode.getNodeData().getReference()).getDfsNumber();
                    headsOfRuns[maxHeightSubtreeNodeNumber] = currentNode;
                }
            }

            currentNodeData.setMaxHeightSubtreeNode(maxHeightSubtreeNode);
        }
    }

    private CompleteBinaryTreeLCA createCompleteBinaryTreeLCAAndAddMappings(Phylogeny tree){
        int nodeCount = tree.getNodeCount();
        CompleteBinaryTreeLCA completeBinaryTreeLCA = new CompleteBinaryTreeLCA((int) (Math.ceil(Math.log(nodeCount) / Math.log(2))) - 1);

        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData currentNodeData = (LCANodeData) currentNode.getNodeData().getReference();
            PhylogenyNode maxHeightSubtreeNode = currentNodeData.getMaxHeightSubtreeNode();
            int maxHeightSubtreeNodeNumber = ((LCANodeData)maxHeightSubtreeNode.getNodeData().getReference()).getDfsNumber();
            PhylogenyNode completeBinaryTreeNode = completeBinaryTreeLCA.getNodeFromPathNumber(maxHeightSubtreeNodeNumber);
            currentNodeData.setCompleteBinaryTreeNode(completeBinaryTreeNode);
        }
        return completeBinaryTreeLCA;
    }

    private void addBitNumbers(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            int parentBitNumber;
            if(currentNode.isRoot()) parentBitNumber = 0;
            else {
                PhylogenyNode parent = currentNode.getParent();
                parentBitNumber = ((LCANodeData)parent.getNodeData().getReference()).getBitNumber();
            }
            LCANodeData currentNodeData = (LCANodeData) currentNode.getNodeData().getReference();
            PhylogenyNode maxHeightSubtreeNode = currentNodeData.getMaxHeightSubtreeNode();
            int heightOfMaxHeightSubtreeNode = ((LCANodeData) maxHeightSubtreeNode.getNodeData().getReference()).getLeastSignificant1BitIndex();
            int bitNumber = parentBitNumber | (int)Math.pow(2, heightOfMaxHeightSubtreeNode-1); // set bit heightOfMaxHeightSubtreeNode to 1
            currentNodeData.setBitNumber(bitNumber);
        }
    }

    private class LCANodeData extends Reference {
        private int dfsNumber;
        private int leastSignificant1BitIndex; //h(v), counting from 1
        private PhylogenyNode maxHeightSubtreeNode; //I(v)
        private int bitNumber; //A(v)
        private PhylogenyNode completeBinaryTreeNode;

        public LCANodeData() {
            super("");
        }

        public int getBitNumber() {
            return bitNumber;
        }

        public void setBitNumber(int bitNumber) {
            this.bitNumber = bitNumber;
        }

        public PhylogenyNode getMaxHeightSubtreeNode() {
            return maxHeightSubtreeNode;
        }

        public void setMaxHeightSubtreeNode(PhylogenyNode maxHeightSubtreeNode) {
            this.maxHeightSubtreeNode = maxHeightSubtreeNode;
        }

        public int getDfsNumber() {
            return dfsNumber;
        }

        public void setDfsNumber(int dfsNumber) {
            this.dfsNumber = dfsNumber;
        }

        public int getLeastSignificant1BitIndex() {
            return leastSignificant1BitIndex;
        }

        public void setLeastSignificant1BitIndex(int leastSignificant1BitIndex) {
            this.leastSignificant1BitIndex = leastSignificant1BitIndex;
        }

        public PhylogenyNode getCompleteBinaryTreeNode() {
            return completeBinaryTreeNode;
        }

        public void setCompleteBinaryTreeNode(PhylogenyNode completeBinaryTreeNode) {
            this.completeBinaryTreeNode = completeBinaryTreeNode;
        }
    }
}
