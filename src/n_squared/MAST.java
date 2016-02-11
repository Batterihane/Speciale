package n_squared;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.List;

/**
 * Created by Thomas on 09-02-2016.
 */
public class MAST {

    public Phylogeny getMAST(Phylogeny tree1, Phylogeny tree2){
        int[] traversalNumbers = new int[tree1.getNodeCount() + tree2.getNodeCount()];

        MASTPair[][] subtreeMASTs = new MASTPair[tree1.getNodeCount()][tree2.getNodeCount()];

        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPostorder();

        for (int i = 0 ; tree1Iterator.hasNext() ; i++){
            PhylogenyNode currentTree1Node = tree1Iterator.next();
            traversalNumbers[currentTree1Node.getId()] = i;

            PhylogenyNodeIterator tree2Iterator = tree2.iteratorPostorder();
            for (int j = 0 ; tree2Iterator.hasNext() ; j++){
                PhylogenyNode currentTree2Node = tree2Iterator.next();
                traversalNumbers[currentTree2Node.getId()] = j;

                if(currentTree1Node.isExternal()){
                    if(currentTree2Node.isExternal()){
                        subtreeMASTs[i][j] = leafLeafMAST(currentTree1Node, currentTree2Node);
                    }
                    else{
                        subtreeMASTs[i][j] = leafInternalNodeMAST(traversalNumbers, subtreeMASTs, i, currentTree2Node);
                    }
                }
                else{
                    if(currentTree2Node.isExternal()){
                        subtreeMASTs[i][j] = internalNodeLeafMAST(traversalNumbers, subtreeMASTs, currentTree1Node, j);
                    }
                    else{
                        subtreeMASTs[i][j] = internalNodeInternalNodeMAST(traversalNumbers, subtreeMASTs, currentTree1Node, currentTree2Node, i, j);
                    }
                }
            }
        }
        Phylogeny tree = new Phylogeny();
        tree.setRoot(subtreeMASTs[tree1.getNodeCount()-1][tree2.getNodeCount()-1].getMast());

        updateParentReferences(tree);
        return tree;
    }

    private void updateParentReferences(Phylogeny tree) {
        PhylogenyNodeIterator phylogenyNodeIterator = tree.iteratorPreorder();
        while (phylogenyNodeIterator.hasNext()) {
            PhylogenyNode next = phylogenyNodeIterator.next();
            List<PhylogenyNode> allDescendants = next.getAllDescendants();
            if (allDescendants != null) allDescendants.forEach(child -> child.setParent(next));
        }
    }

    private MASTPair internalNodeInternalNodeMAST(int[] traversalNumbers, MASTPair[][] subtreeMASTs, PhylogenyNode internalNode1, PhylogenyNode internalNode2, int i, int j){
        int internalNode1Child1TraversalNumber = traversalNumbers[internalNode1.getChildNode1().getId()];
        int internalNode1Child2TraversalNumber = traversalNumbers[internalNode1.getChildNode2().getId()];

        int internalNode2Child1TraversalNumber = traversalNumbers[internalNode2.getChildNode1().getId()];
        int internalNode2Child2TraversalNumber = traversalNumbers[internalNode2.getChildNode2().getId()];

        int size1 = subtreeMASTs[internalNode1Child1TraversalNumber][internalNode2Child1TraversalNumber].getSize() + subtreeMASTs[internalNode1Child2TraversalNumber][internalNode2Child2TraversalNumber].getSize();
        int size2 = subtreeMASTs[internalNode1Child1TraversalNumber][internalNode2Child2TraversalNumber].getSize() + subtreeMASTs[internalNode1Child2TraversalNumber][internalNode2Child1TraversalNumber].getSize();
        int size3 = subtreeMASTs[i][internalNode2Child1TraversalNumber].getSize();
        int size4 = subtreeMASTs[i][internalNode2Child2TraversalNumber].getSize();
        int size5 = subtreeMASTs[internalNode1Child1TraversalNumber][j].getSize();
        int size6 = subtreeMASTs[internalNode1Child2TraversalNumber][j].getSize();

        int maxSize = max(size1, size2, size3, size4, size5, size6);

        if(maxSize == size6) return subtreeMASTs[internalNode1Child2TraversalNumber][j];
        if(maxSize == size5) return subtreeMASTs[internalNode1Child1TraversalNumber][j];
        if(maxSize == size4) return subtreeMASTs[i][internalNode2Child2TraversalNumber];
        if(maxSize == size3) return subtreeMASTs[i][internalNode2Child1TraversalNumber];
        if(maxSize == size2){
            PhylogenyNode mast = new PhylogenyNode();
            mast.setChild1(subtreeMASTs[internalNode1Child1TraversalNumber][internalNode2Child2TraversalNumber].getMast());
            mast.setChild2(subtreeMASTs[internalNode1Child2TraversalNumber][internalNode2Child1TraversalNumber].getMast());
            return new MASTPair(mast, maxSize);
        }
        else{ //if(maxSize == size1)
            PhylogenyNode mast = new PhylogenyNode();
            mast.setChild1(subtreeMASTs[internalNode1Child1TraversalNumber][internalNode2Child1TraversalNumber].getMast());
            mast.setChild2(subtreeMASTs[internalNode1Child2TraversalNumber][internalNode2Child2TraversalNumber].getMast());
            return new MASTPair(mast, maxSize);
        }
    }

    private MASTPair internalNodeLeafMAST(int[] traversalNumbers, MASTPair[][] subtreeMASTs, PhylogenyNode internalNode, int leafTraversalNumber) {
        int internalNodeChild1TraversalNumber = traversalNumbers[internalNode.getChildNode1().getId()];
        int internalNodeChild2TraversalNumber = traversalNumbers[internalNode.getChildNode2().getId()];
        MASTPair internalNodeChild1AndLeafMASTPair = subtreeMASTs[internalNodeChild1TraversalNumber][leafTraversalNumber];
        MASTPair internalNodeChild2AndLeafMASTPair = subtreeMASTs[internalNodeChild2TraversalNumber][leafTraversalNumber];
        if(internalNodeChild1AndLeafMASTPair.getSize() >= internalNodeChild2AndLeafMASTPair.getSize()){
            return internalNodeChild1AndLeafMASTPair;
        }
        else{
            return internalNodeChild2AndLeafMASTPair;
        }
    }

    private MASTPair leafInternalNodeMAST(int[] traversalNumbers, MASTPair[][] subtreeMASTs, int leafTraversalNumber, PhylogenyNode internalNode) {
        int internalNodeChild1TraversalNumber = traversalNumbers[internalNode.getChildNode1().getId()];
        int internalNodeChild2TraversalNumber = traversalNumbers[internalNode.getChildNode2().getId()];

        MASTPair leafAndInternalNodeChild1MASTPair = subtreeMASTs[leafTraversalNumber][internalNodeChild1TraversalNumber];
        MASTPair leafAndInternalNodeChild2MASTPair = subtreeMASTs[leafTraversalNumber][internalNodeChild2TraversalNumber];
        if(leafAndInternalNodeChild1MASTPair.getSize() >= leafAndInternalNodeChild2MASTPair.getSize()){
            return leafAndInternalNodeChild1MASTPair;
        }
        else{
            return leafAndInternalNodeChild2MASTPair;
        }
    }

    private MASTPair leafLeafMAST(PhylogenyNode leaf1, PhylogenyNode leaf2) {
        if(leaf1.getName().equals(leaf2.getName())){
            PhylogenyNode mast = new PhylogenyNode();
            mast.setName(leaf1.getName());
            return new MASTPair(mast, 1);
        }
        else{
            return new MASTPair();
        }
    }

    public int max(int a, int b, int c, int d, int e, int f){
        return Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, f)))));
    }

    public void printMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getMASTsize(Phylogeny tree1, Phylogeny tree2){
        int[] traversalNumbers = new int[tree1.getNodeCount() + tree2.getNodeCount()];

        int[][] subtreeMASTs = new int[tree1.getNodeCount()][tree2.getNodeCount()];

        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPostorder();

        for (int i = 0 ; tree1Iterator.hasNext() ; i++){
            PhylogenyNode currentTree1Node = tree1Iterator.next();
            traversalNumbers[currentTree1Node.getId()] = i;
            PhylogenyNodeIterator tree2Iterator = tree2.iteratorPostorder();
            for (int j = 0 ; tree2Iterator.hasNext() ; j++){
                PhylogenyNode currentTree2Node = tree2Iterator.next();
                traversalNumbers[currentTree2Node.getId()] = j;
                if(currentTree1Node.isExternal()){
                    if(currentTree2Node.isExternal()){
                        subtreeMASTs[i][j] = currentTree1Node.getName().equals(currentTree2Node.getName())? 1 : 0;
                    }
                    else{
                        int tree2Child1TraversalNumber = traversalNumbers[currentTree2Node.getChildNode1().getId()];
                        int tree2Child2TraversalNumber = traversalNumbers[currentTree2Node.getChildNode2().getId()];
                        subtreeMASTs[i][j] = Math.max(subtreeMASTs[i][tree2Child1TraversalNumber], subtreeMASTs[i][tree2Child2TraversalNumber]);
                    }
                }
                else{
                    int tree1Child1TraversalNumber = traversalNumbers[currentTree1Node.getChildNode1().getId()];
                    int tree1Child2TraversalNumber = traversalNumbers[currentTree1Node.getChildNode2().getId()];

                    if(currentTree2Node.isExternal()){
                        subtreeMASTs[i][j] = Math.max(subtreeMASTs[tree1Child1TraversalNumber][j], subtreeMASTs[tree1Child2TraversalNumber][j]);
                    }
                    else{
                        int tree2Child1TraversalNumber = traversalNumbers[currentTree2Node.getChildNode1().getId()];
                        int tree2Child2TraversalNumber = traversalNumbers[currentTree2Node.getChildNode2().getId()];

                        subtreeMASTs[i][j] = max(
                                subtreeMASTs[tree1Child1TraversalNumber][tree2Child1TraversalNumber] + subtreeMASTs[tree1Child2TraversalNumber][tree2Child2TraversalNumber],
                                subtreeMASTs[tree1Child1TraversalNumber][tree2Child2TraversalNumber] + subtreeMASTs[tree1Child2TraversalNumber][tree2Child1TraversalNumber],
                                subtreeMASTs[i][tree2Child1TraversalNumber],
                                subtreeMASTs[i][tree2Child2TraversalNumber],
                                subtreeMASTs[tree1Child1TraversalNumber][j],
                                subtreeMASTs[tree1Child2TraversalNumber][j]
                        );
                    }
                }
            }
        }

        return subtreeMASTs[tree1.getNodeCount()-1][tree2.getNodeCount()-1];
    }

}
