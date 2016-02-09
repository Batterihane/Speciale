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

    public int getMAST(Phylogeny tree1, Phylogeny tree2){
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

    public int max(int a, int b, int c, int d, int e, int f){
        return Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, f)))));
    }

}
