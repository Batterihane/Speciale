package n_squared;

import Utilities.ForesterNewickParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        Phylogeny tree1 = foresterNewickParser.parseNewickFile("trees\\Tree3.new");
        Phylogeny tree2 = foresterNewickParser.parseNewickFile("trees\\Tree4.new");
        //foresterNewickParser.displayPhylogeny(tree);
        //System.out.println(tree.getRoot().getNumberOfDescendants());


        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPostorder();
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPostorder();

        MAST mast = new MAST();
        System.out.println(mast.getMAST(tree1, tree2));
    }
}
