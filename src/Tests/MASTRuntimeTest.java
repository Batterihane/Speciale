package Tests;

import Utilities.PhylogenyGenerator;
import Utilities.PhylogenyParser;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
        testRandomTrees();
    }

    private static void runRandomTrees() {
//        initialRuns();

        System.out.println("Test:");
        while (true) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(8, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(8, false);
            MAST mast = new MAST();
            try{
                mast.getMAST(tree1, tree2);
            } catch (Exception e){
                new PhylogenyParser().toNewick(tree1, "testTree1", false);
                new PhylogenyParser().toNewick(tree2, "testTree2", false);
                return;
            }
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
            long averageTime = (timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i))/5;
            System.out.println(i + "\t" + ((int)(averageTime/nLogN(i))));
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

}
