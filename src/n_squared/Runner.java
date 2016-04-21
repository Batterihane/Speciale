package n_squared;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class Runner {
    public static void main(String[] args) {


        backTrackTest();
        System.exit(0);


        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

//        Phylogeny tree1 = foresterNewickParser.parseNewickFile("treess\\Tree1.new");
//        Phylogeny tree2 = foresterNewickParser.parseNewickFile("treess\\Tree4.new");
        //Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(10);
        //Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(10);
        //foresterNewickParser.displayPhylogeny(tree1);
        //foresterNewickParser.displayPhylogeny(tree2);

        for (int i = 100; i < 200000; i+=100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            long start = System.currentTimeMillis();
            PhylogenyNodeIterator phylogenyNodeIterator = tree.iteratorPostorder();
            while(phylogenyNodeIterator.hasNext()){
                phylogenyNodeIterator.next();
            }
            long finish = System.currentTimeMillis();
            System.out.println(i+ ", " + (finish-start));
        }


        //System.out.println(tree1.getRoot().getNumberOfExternalNodes());
        //System.out.println(tree1.getRoot().getChildNode1().getNumberOfExternalNodes());
        //System.out.println(tree1.getRoot().getChildNode2().getNumberOfExternalNodes());
        //tree1.getRoot().getChildNode2().getChildNode1().setLink(tree1.getRoot());
        //boolean equals = tree1.getRoot().getChildNode2().getChildNode1().getLink().equals(tree1.getRoot());
        //System.out.println(equals);

        //MAST mast = new MAST();
//        System.out.println(mast.getMASTsize(tree1, tree2));
        //foresterNewickParser.displayPhylogeny(mast.getMAST(tree1, tree2));



    }
    private static void backTrackTest() {
        for (int i = 10; i < 10000; i+= 10) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            Phylogeny backTrackMast = nSquaredMastFinder.getMastBackTrack(tree1, tree2);
            int backTrackSize = backTrackMast.getNumberOfExternalNodes();

            if(backTrackSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(backTrackMast);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println(i + ": Failure - BackTrack(" + backTrackSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

            MainFrame application = Archaeopteryx.createApplication(backTrackMast);
            application.dispose();

            System.out.println(i + ": Success!");

        }
    }
}
