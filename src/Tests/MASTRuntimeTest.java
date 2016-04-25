package Tests;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import Utilities.PhylogenyParser;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
        testRandomTrees();
    }

    private static void runRandomTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 40000 ; i+= 100) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2);
        }
    }

    private static void testBaseCaseTrees(){
        initialRuns();

        System.out.println("Test:");
        for (int i = 10; i < 5000; i+= 10) { // GC overhead limit at size 42300
            long averageTime = (timeGetMASTIdenticalTrees(i) + timeGetMASTIdenticalTrees(i) + timeGetMASTIdenticalTrees(i) + timeGetMASTIdenticalTrees(i) + timeGetMASTIdenticalTrees(i))/5;
            System.out.println(i + "\t" + ((int)(averageTime/ nLogNCube(i))));
        }
    }

    private static void testRandomTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i < 100000; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(i);
            runtimes[1] = timeGetMAST(i);
            runtimes[2] = timeGetMAST(i);
            runtimes[3] = timeGetMAST(i);
            runtimes[4] = timeGetMAST(i);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
        }
    }

    private static void testInduceSubtrees() {
        for (int i = 10; i < 50000; i+= 50) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            tree1.recalculateNumberOfExternalDescendants(true);
            tree2.recalculateNumberOfExternalDescendants(true);
            addNodeDataReferences(tree1);
            addNodeDataReferences(tree2);

            MAST mastFinder = new MAST();
            mastFinder.setTwins(tree1, tree2);
            List<PhylogenyNode> tree1Decomposition = mastFinder.computeFirstDecomposition(tree1);

            long time = System.nanoTime();
            mastFinder.induceSubtrees(tree1Decomposition, tree1, tree2);
            System.out.println(i + "\t" + (System.nanoTime() - time)/i);
        }
    }

    private static void initialRuns() {
        System.out.println("Initial:");
        for (int i = 20; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2);
            System.out.println(i);
        }
    }

    private static long timeGetMAST(int size) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static long timeGetMASTIdenticalTrees(int size) {
        Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(size, false);
        Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static double nLogN(int n){
        return n * (Math.log(n) / Math.log(2));
    }

    private static double nLogNCube(int n){
        double logn = Math.log(n) / Math.log(2);
        return n * logn * logn * logn;
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

    private static long median(long[] numbers){
        Arrays.sort(numbers);
        return numbers[2];
    }
}
