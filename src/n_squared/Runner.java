package n_squared;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

//        Phylogeny tree1 = foresterNewickParser.parseNewickFile("trees\\Tree1.new");
//        Phylogeny tree2 = foresterNewickParser.parseNewickFile("trees\\Tree4.new");
        Phylogeny tree1 = PhylogenyGenerator.generateTree(3);
        Phylogeny tree2 = PhylogenyGenerator.generateTree(3);
//        foresterNewickParser.displayPhylogeny(tree1);
//        foresterNewickParser.displayPhylogeny(tree2);

        MAST mast = new MAST();
//        System.out.println(mast.getMASTsize(tree1, tree2));
        foresterNewickParser.displayPhylogeny(mast.getMAST(tree1, tree2));
    }
}
