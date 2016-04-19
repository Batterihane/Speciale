package Tests;

import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
        testRandomTrees();
    }

    private static void testRandomTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i < 100000; i+= 100) { // GC overhead limit at size 42300
            int averageTime = (timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i) + timeGetMAST(i))/5;
            System.out.print(i + "\t" + averageTime);
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

    private static int timeGetMAST(int size) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return (int) ((System.nanoTime() - time) / (size * (Math.log(size) / Math.log(2))));
    }

}
