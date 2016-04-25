package Tests;

import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 24-03-2016.
 */
public class MASTCorrectnessTest {
    public static void main(String[] args) {
        nLognVsNSquared();
    }

    private static void nLognVsNSquared() {
        for (int i = 10; i < 10000; i+= 10) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            MAST.TreeAndSizePair nLogNTreeAndSize = nLogNMastFinder.getMAST(tree1, tree2, false);
            Phylogeny nLogNMast = nLogNTreeAndSize.getTree();
            int nLogNMastSize = nLogNTreeAndSize.getSize();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(nLogNMast);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println(i + ": Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

//            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
//            application.dispose();

            System.out.println(i + ": Success!");

        }
    }

    private static void nLognVsNSquaredConstantSize(int size) {
        while (true) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            MAST.TreeAndSizePair nLogNTreeAndSize = nLogNMastFinder.getMAST(tree1, tree2, false);
            Phylogeny nLogNMast = nLogNTreeAndSize.getTree();
            int nLogNMastSize = nLogNTreeAndSize.getSize();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(nLogNMast);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println( "Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
            application.dispose();

            System.out.println("Success!");

        }
    }

}
