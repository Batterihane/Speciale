package nlogn;

import Utilities.ForesterNewickParser;
import Utilities.LCA;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.List;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

//        Phylogeny tree1 = foresterNewickParser.parseNewickFile("treess\\Tree1.new");
//        Phylogeny tree2 = foresterNewickParser.parseNewickFile("treess\\Tree4.new");
        Phylogeny tree1 = PhylogenyGenerator.generateTree(10);
        Phylogeny tree2 = PhylogenyGenerator.generateTree(10);
        foresterNewickParser.displayPhylogeny(tree1);
//        foresterNewickParser.displayPhylogeny(tree2);

        MAST mast = new MAST();
//        System.out.println(mast.getMASTsize(tree1, tree2));
        LCA lca = new LCA(tree1);
        System.out.println(lca.getLCA(tree1.getNode(1), tree1.getNode(1)).getId());



    }
}
