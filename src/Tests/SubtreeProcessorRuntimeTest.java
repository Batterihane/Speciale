package Tests;

import Utilities.DataObjects.MASTNodeData;
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
        for (int i = 100; i < 100000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);


            List<PhylogenyNode> leaves = tree.getRoot().getAllExternalDescendants();
            Random random = new Random();
            while (leaves.size() > 100){
                leaves.remove(random.nextInt(leaves.size()));
            }


            long time = System.currentTimeMillis();
            SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree);
            //subtreeProcessor.induceSubtree(leaves);
            System.out.println(System.currentTimeMillis() - time);
        }
    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            NodeDataReference nodeDataReference = new NodeDataReference();
            MASTNodeData mastNodeData = new MASTNodeData();
            nodeDataReference.setMastNodeData(mastNodeData);
            currentNode.getNodeData().addReference(nodeDataReference);

        }
    }

}
