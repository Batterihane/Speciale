package nlogn;

import Utilities.ForesterNewickParser;
import Utilities.LCA;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.LinkedList;
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

        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree1);
        List<PhylogenyNode> leaves = tree1.getExternalNodes();
        PhylogenyNode[] leavesSubset = new PhylogenyNode[5];
        leavesSubset[0] = leaves.get(0);
        leavesSubset[1] = leaves.get(4);
        leavesSubset[2] = leaves.get(5);
        leavesSubset[3] = leaves.get(7);
        leavesSubset[4] = leaves.get(9);
        foresterNewickParser.displayPhylogeny(subtreeProcessor.induceSubtree(leavesSubset));

        for(PhylogenyNode node : leavesSubset){
            System.out.println(node.getName());
        }

//        MAST mast = new MAST();
//        System.out.println(mast.getMASTsize(tree1, tree2));
//        LCA lca = new LCA(tree1);
//        System.out.println(lca.getLCA(tree1.getNode(1), tree1.getNode(1)).getId());



    }
}
