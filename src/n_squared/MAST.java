package n_squared;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

/**
 * Created by Thomas on 09-02-2016.
 */
public class MAST {
    public MAST(){

    }

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
                        if(currentTree1Node.getName().equals(currentTree2Node.getName())){
                            PhylogenyNode mast = new PhylogenyNode();
                            mast.setName(currentTree1Node.getName());
                            subtreeMASTs[i][j] = new MASTPair(mast, 1);
                        }
                        else{
                            subtreeMASTs[i][j] = new MASTPair();
                        }
                    }
                    else{
                        int tree2Child1TraversalNumber = traversalNumbers[currentTree2Node.getChildNode1().getId()];
                        int tree2Child2TraversalNumber = traversalNumbers[currentTree2Node.getChildNode2().getId()];

                        MASTPair tree2Child1MASTPair = subtreeMASTs[i][tree2Child1TraversalNumber];
                        MASTPair tree2Child2MASTPair = subtreeMASTs[i][tree2Child2TraversalNumber];
                        if(tree2Child1MASTPair.getSize() >= tree2Child2MASTPair.getSize()){
                            subtreeMASTs[i][j] = tree2Child1MASTPair;
                        }
                        else{
                            subtreeMASTs[i][j] = tree2Child2MASTPair;
                        }

                    }
                }
                else{
                    int tree1Child1TraversalNumber = traversalNumbers[currentTree1Node.getChildNode1().getId()];
                    int tree1Child2TraversalNumber = traversalNumbers[currentTree1Node.getChildNode2().getId()];

                    if(currentTree2Node.isExternal()){
                        MASTPair tree1Child1MASTPair = subtreeMASTs[tree1Child1TraversalNumber][j];
                        MASTPair tree1Child2MASTPair = subtreeMASTs[tree1Child2TraversalNumber][j];
                        if(tree1Child1MASTPair.getSize() >= tree1Child2MASTPair.getSize()){
                            subtreeMASTs[i][j] = tree1Child1MASTPair;
                        }
                        else{
                            subtreeMASTs[i][j] = tree1Child2MASTPair;
                        }
                    }
                    else{
                        int tree2Child1TraversalNumber = traversalNumbers[currentTree2Node.getChildNode1().getId()];
                        int tree2Child2TraversalNumber = traversalNumbers[currentTree2Node.getChildNode2().getId()];

                        int size1 = subtreeMASTs[tree1Child1TraversalNumber][tree2Child1TraversalNumber].getSize() + subtreeMASTs[tree1Child2TraversalNumber][tree2Child2TraversalNumber].getSize();
                        int size2 = subtreeMASTs[tree1Child1TraversalNumber][tree2Child2TraversalNumber].getSize() + subtreeMASTs[tree1Child2TraversalNumber][tree2Child1TraversalNumber].getSize();
                        int size3 = subtreeMASTs[i][tree2Child1TraversalNumber].getSize();
                        int size4 = subtreeMASTs[i][tree2Child2TraversalNumber].getSize();
                        int size5 = subtreeMASTs[tree1Child1TraversalNumber][j].getSize();
                        int size6 = subtreeMASTs[tree1Child2TraversalNumber][j].getSize();

                        int maxSize = max(size1, size2, size3, size4, size5, size6);

                        if(maxSize == size6) subtreeMASTs[i][j] = subtreeMASTs[tree1Child2TraversalNumber][j];
                        else if(maxSize == size5) subtreeMASTs[i][j] = subtreeMASTs[tree1Child1TraversalNumber][j];
                        else if(maxSize == size4) subtreeMASTs[i][j] = subtreeMASTs[i][tree2Child2TraversalNumber];
                        else if(maxSize == size3) subtreeMASTs[i][j] = subtreeMASTs[i][tree2Child1TraversalNumber];
                        else if(maxSize == size2){
                            PhylogenyNode mast = new PhylogenyNode();
                            mast.setChild1(subtreeMASTs[tree1Child1TraversalNumber][tree2Child2TraversalNumber].getMast());
                            mast.setChild2(subtreeMASTs[tree1Child2TraversalNumber][tree2Child1TraversalNumber].getMast());
                            subtreeMASTs[i][j] = new MASTPair(mast, maxSize);
                        }
                        else if(maxSize == size1){
                            PhylogenyNode mast = new PhylogenyNode();
                            mast.setChild1(subtreeMASTs[tree1Child1TraversalNumber][tree2Child1TraversalNumber].getMast());
                            mast.setChild2(subtreeMASTs[tree1Child2TraversalNumber][tree2Child2TraversalNumber].getMast());
                            subtreeMASTs[i][j] = new MASTPair(mast, maxSize);
                        }
                    }
                }
            }
        }
        Phylogeny tree = new Phylogeny();
        tree.setRoot(subtreeMASTs[tree1.getNodeCount()-1][tree2.getNodeCount()-1].getMast());
        return tree;
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

    public void printMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int max(int a, int b, int c, int d, int e, int f){
        return Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, f)))));
    }

}
