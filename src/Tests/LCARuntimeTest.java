package Tests;

import Utilities.ConstantTimeLCA;
import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

/**
 * Created by Thomas on 09-03-2016.
 */
public class LCARuntimeTest {
    public static void main(String[] args) {
        runtimeTest();
    }

    private static void preprocessRuntimeTest() {
        for (int i = 100; i < 50000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            long time = System.currentTimeMillis();
            ConstantTimeLCA lcaFinder = new ConstantTimeLCA(tree);
            time = System.currentTimeMillis() - time;
            System.out.println(time);
        }
    }

    private static void runtimeTest() {
        for (int i = 100; i < 20000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            PhylogenyNode node1 = tree.getNode("Leaf_0");
            PhylogenyNode node2 = tree.getNode("Leaf_10");
            ConstantTimeLCA lcaFinder = new ConstantTimeLCA(tree);
            long time = System.nanoTime();
            lcaFinder.getLCA(node1, node2);
            time = System.nanoTime() - time;
            System.out.println(time);
        }
    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            currentNode.getNodeData().addReference(new NodeDataReference());
        }
    }
}
