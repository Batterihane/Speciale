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
        for (int i = 10; i < 10000; i+= 10) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i);

            PhylogenyGenerator.renameTreeLeavesLeftToRight(tree2);
            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            Phylogeny nLogNMast = nLogNMastFinder.getMAST(tree1, tree2);
            int nLogNMastSize = nLogNMast.getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                MainFrame tree1Window = Archaeopteryx.createApplication(tree1);
                MainFrame tree2Window = Archaeopteryx.createApplication(tree2);
                MainFrame nLogNResultWindow = Archaeopteryx.createApplication(nLogNMast);
                MainFrame nSquaredResultWindow = Archaeopteryx.createApplication(nSquaredMast);

                System.out.println(i + ": Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }


            System.out.println(i + ": Success!");

        }
    }
}
