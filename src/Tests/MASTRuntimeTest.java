package Tests;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
        testBaseCaseTrees();
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
            gcTimes[0] = timeGCGetMAST(i, recursive, gcMonitor);
            gcTimes[1] = timeGCGetMAST(i, recursive, gcMonitor);
            gcTimes[2] = timeGCGetMAST(i, recursive, gcMonitor);
            gcTimes[3] = timeGCGetMAST(i, recursive, gcMonitor);
            gcTimes[4] = timeGCGetMAST(i, recursive, gcMonitor);
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

    private static void testBaseCaseTrees(){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i < 80000; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMASTIdenticalTrees(i, false);
            runtimes[1] = timeGetMASTIdenticalTrees(i, false);
            runtimes[2] = timeGetMASTIdenticalTrees(i, false);
            runtimes[3] = timeGetMASTIdenticalTrees(i, false);
            runtimes[4] = timeGetMASTIdenticalTrees(i, false);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
        }
    }

    private static void testRandomTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(i, recursive);
            runtimes[1] = timeGetMAST(i, recursive);
            runtimes[2] = timeGetMAST(i, recursive);
            runtimes[3] = timeGetMAST(i, recursive);
            runtimes[4] = timeGetMAST(i, recursive);
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
            mast.getMAST(tree1, tree2, false);
            System.out.println(i);
        }
    }

    private static long timeGetMAST(int size, boolean recursive) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, recursive);
        return System.nanoTime() - time;
    }

    private static long timeGetMASTIdenticalTrees(int size, boolean recursive) {
        Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(size, recursive);
        Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(size, recursive);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, false);
        return System.nanoTime() - time;
    }

    private static long timeGCGetMAST(int size, boolean recursive, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
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

    private static class GCMonitor {
        private long previousTotalGarbageCollections = 0;
        private long previousTotalGarbageCollectingTime = 0;

        private GCMonitor(){
            getNumberOfGarbageCollectionsSinceLastMeasurement();
            getTimeUsedOnGarbageCollectingSinceLastMeasurement();
        }

        private long getNumberOfGarbageCollectionsSinceLastMeasurement() {
            long totalGarbageCollections = 0;
            long garbageCollectionTime = 0;

            for(GarbageCollectorMXBean gc :
                    ManagementFactory.getGarbageCollectorMXBeans()) {

                long count = gc.getCollectionCount();

                if(count >= 0) {
                    totalGarbageCollections += count;
                }

                long time = gc.getCollectionTime();

                if(time >= 0) {
                    garbageCollectionTime += time;
                }
            }

            long result = totalGarbageCollections - previousTotalGarbageCollections;
            previousTotalGarbageCollections = totalGarbageCollections;
            return result;
//            System.out.println(totalGarbageCollections);
//        System.out.println("Total Garbage Collection Time (ms): "
//                + garbageCollectionTime);
        }

        private long getTimeUsedOnGarbageCollectingSinceLastMeasurement() { // in ms
            long totalGarbageCollectingTime = 0;

            for(GarbageCollectorMXBean gc :
                    ManagementFactory.getGarbageCollectorMXBeans()) {

                long time = gc.getCollectionTime();

                if(time >= 0) {
                    totalGarbageCollectingTime += time;
                }
            }

            long result = totalGarbageCollectingTime - previousTotalGarbageCollectingTime;
            previousTotalGarbageCollectingTime = totalGarbageCollectingTime;
            return result;
        }
    }
}
