package Tests;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

/**
 * Created by Thomas on 10-03-2016.
 */
public class PhylogenyTest {
    public static void main(String[] args) {
        addChildRuntimeTest();
    }

    private static void addChildRuntimeTest() {
        PhylogenyNode root = new PhylogenyNode();
        PhylogenyNode currentNode = root;
        for (int i = 0; i < 10000; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            long time = System.nanoTime();
            currentNode.setChild1(newNode);
            time = System.nanoTime() - time;
            System.out.println(time);

            PhylogenyNode newNode2 = new PhylogenyNode();
            currentNode.setChild2(newNode2);

            currentNode = newNode;
        }
//        Phylogeny tree = new Phylogeny();
//        tree.setRoot(root);
//        tree.recalculateNumberOfExternalDescendants(true);
//
//        System.out.println("Root has " + root.getNumberOfExternalNodes() + " external nodes");
//
//        Phylogeny tree2 = PhylogenyGenerator.generateTree(10);
//        System.out.println("Root2 has " + tree2.getRoot().getNumberOfExternalNodes() + " external nodes");
//
//        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
//        foresterNewickParser.displayPhylogeny(tree2);
    }
}
