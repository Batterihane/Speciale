package Tests;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.Pair;
import Utilities.PhylogenyGenerator;
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
//        testRandomTreesGCSubtracted(80000, false);
//        testPerfectTrees(80000, false);
//        testRandomTrees(80000, false); // with 1000GB and 2000GB allocated memory
//        testGCOnRandomTrees(80000, false);
//        testIdenticalBaseCaseTrees(80000, false);
//        testNonSimilarBaseCaseTrees(80000, false);
        testPerfectTreesGCSubtracted(80000, false);
//        testNonSimilarBaseCaseTreesMLIS(80000);
    }

    private static void runRandomTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void runRandomIdenticalTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) {
            Pair<Phylogeny, Phylogeny> trees = PhylogenyGenerator.generateIdenticalRandomTrees(i, false);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void runBaseCaseTrees() {
//        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void testGCOnRandomTrees(int maxsize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100 ; i <= maxsize ; i+= 100) {
            long[] gcTimes = new long[5];
            gcTimes[0] = timeGCGetMASTRandomTrees(i, recursive, gcMonitor);
            gcTimes[1] = timeGCGetMASTRandomTrees(i, recursive, gcMonitor);
            gcTimes[2] = timeGCGetMASTRandomTrees(i, recursive, gcMonitor);
            gcTimes[3] = timeGCGetMASTRandomTrees(i, recursive, gcMonitor);
            gcTimes[4] = timeGCGetMASTRandomTrees(i, recursive, gcMonitor);
            System.out.println(i + "\t" + median(gcTimes));
        }
    }

    private static void testGCOnBaseCaseTrees(int maxsize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100 ; i <= maxsize ; i+= 100) {
            long[] gcTimes = new long[5];
            gcTimes[0] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[1] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[2] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[3] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[4] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            System.out.println(i + "\t" + median(gcTimes));
        }
    }

    private static void testIterativeAndRecursive(int maxsize){
        System.out.println("Iterative:");
        try {
            testRandomTrees(maxsize, false);
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("Recursive:");
            testRandomTrees(maxsize, true);
        }
    }

    private static void testIdenticalBaseCaseTrees(int size, boolean recursive){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testNonSimilarBaseCaseTrees(int size, boolean recursive){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            PhylogenyGenerator.renameTreeLeavesRightToLeft(tree1);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testNonSimilarBaseCaseTreesMLIS(int size){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            PhylogenyGenerator.renameTreeLeavesRightToLeft(tree1);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[1] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[2] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[3] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[4] = timeGetMASTUsingMLIS(tree1, tree2);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesOld(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMASTRandomTrees(i, recursive);
            runtimes[1] = timeGetMASTRandomTrees(i, recursive);
            runtimes[2] = timeGetMASTRandomTrees(i, recursive);
            runtimes[3] = timeGetMASTRandomTrees(i, recursive);
            runtimes[4] = timeGetMASTRandomTrees(i, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesGCSubtractedOld(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesGCSubtracted(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testPerfectTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testPerfectTreesGCSubtracted(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testGCOnPerfectTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtimes[0] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[1] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[2] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[3] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[4] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
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
        for (int i = 100; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
            System.out.println(i);
        }
    }

    private static long timeGetMASTRandomTrees(int size, boolean recursive) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, recursive);
        return System.nanoTime() - time;
    }

    private static long timeGetMAST(Phylogeny tree1, Phylogeny tree2, boolean recursive) {
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, recursive);
        return System.nanoTime() - time;
    }

    private static long timeGetMASTUsingMLIS(Phylogeny tree1, Phylogeny tree2) {
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMASTUsingMLIS(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static long timeGCGetMASTRandomTrees(int size, boolean recursive, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static long timeGCGetMAST(Phylogeny tree1, Phylogeny tree2, boolean recursive, GCMonitor gcMonitor) {
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static long timeGCGetMASTBaseCase(int size, boolean recursive, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(size, false);
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
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
