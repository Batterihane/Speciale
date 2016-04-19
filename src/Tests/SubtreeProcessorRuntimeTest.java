package Tests;

import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.List;
import java.util.Random;

/**
 * Created by Thomas on 11-03-2016.
 */
public class SubtreeProcessorRuntimeTest {

    public static void main(String[] args) {
        for (int i = 100; i < 10000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            List<PhylogenyNode> leaves = tree.getRoot().getAllExternalDescendants();
            Random random = new Random();
            while (leaves.size() > 100){
                leaves.remove(random.nextInt(leaves.size()));
            }

            SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree);

            long time = System.currentTimeMillis();
            subtreeProcessor.induceSubtree(leaves);
            System.out.println(System.currentTimeMillis() - time);
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
