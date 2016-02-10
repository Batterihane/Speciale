package n_squared;

import Utilities.ForesterNewickParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        Phylogeny tree1 = foresterNewickParser.parseNewickFile("trees\\304_A1_Propeptide.new");
        Phylogeny tree2 = foresterNewickParser.parseNewickFile("trees\\304_A1_Propeptide.new");
        //foresterNewickParser.displayPhylogeny(tree);
        //System.out.println(tree.getRoot().getNumberOfDescendants());


        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPostorder();
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPostorder();

        MAST mast = new MAST();
        int size = mast.getMAST(tree1, tree2);
        System.out.println(size);

    }
}
