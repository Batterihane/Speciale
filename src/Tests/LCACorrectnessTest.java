package Tests;

import Utilities.ConstantTimeLCA;
import Utilities.DataObjects.NodeDataReference;
import Utilities.LCA;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

/**
 * Created by Thomas on 10-03-2016.
 */
public class LCACorrectnessTest {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Phylogeny tree = PhylogenyGenerator.generateTree(1000);
            addNodeDataReferences(tree);

            PhylogenyNode node1 = tree.getNode("Leaf_0");
            PhylogenyNode node2 = tree.getNode("Leaf_10");
            ConstantTimeLCA lcaFinder1 = new ConstantTimeLCA(tree);
            LCA lcaFinder2 = new LCA(tree);
            PhylogenyNode result1 = lcaFinder1.getLCA(node1, node2);
            PhylogenyNode result2 = lcaFinder2.getLCA(node1, node2);
            if(result1 != result2) System.out.println("Results not matching on run " + i);

        }
        System.out.println("Success!");


    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            currentNode.getNodeData().addReference(new NodeDataReference());
        }
    }
}
