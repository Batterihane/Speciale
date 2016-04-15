package Tests;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        for (int i = 100; i < 100000; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateTree(i);
            Phylogeny tree2 = PhylogenyGenerator.generateTree(i);
            MAST mast = new MAST();
            long time = System.nanoTime();
            mast.getMAST(tree1, tree2);
            System.out.println((int)((System.nanoTime() - time)/(i*(Math.log(i)/Math.log(2)))));
        }


    }

}
