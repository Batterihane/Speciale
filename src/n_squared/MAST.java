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
        int[] tree1TraversalNumbers = new int[tree1.getNodeCount()];
        int[] tree2TraversalNumbers = new int[tree2.getNodeCount()];

        int[][] subtreeMASTs = new int[tree1.getNodeCount()][tree2.getNodeCount()];

        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPostorder();
        PhylogenyNodeIterator tree2Iterator = tree1.iteratorPostorder();

        for (int i = 0 ; tree1Iterator.hasNext() ; i++){
            PhylogenyNode currentTree1Node = tree1Iterator.next();
            tree1TraversalNumbers[currentTree1Node.getId()] = i;
            for (int j = 0 ; tree2Iterator.hasNext() ; j++){
                PhylogenyNode currentTree2Node = tree2Iterator.next();
                tree2TraversalNumbers[currentTree2Node.getId()] = j;
                if(currentTree1Node.getNumberOfDescendants() == 0){
                    if(currentTree2Node.getNumberOfDescendants() == 0){
                        subtreeMASTs[i][j] = currentTree1Node.getName().equals(currentTree2Node.getName())? 1 : 0;
                    }
                    else{
                        int child1TraversalNumber = tree2TraversalNumbers[currentTree2Node.getChildNode1().getId()];
                        int child2TraversalNumber = tree2TraversalNumbers[currentTree2Node.getChildNode2().getId()];
                        subtreeMASTs[i][j] = Math.max(subtreeMASTs[i][child1TraversalNumber], subtreeMASTs[i][child2TraversalNumber]);
                    }
                }
                else{

                }
            }
        }
        return 0;
    }


}
