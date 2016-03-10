package n_squared;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class Runner {
    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

//        Phylogeny tree1 = foresterNewickParser.parseNewickFile("treess\\Tree1.new");
//        Phylogeny tree2 = foresterNewickParser.parseNewickFile("treess\\Tree4.new");
        //Phylogeny tree1 = PhylogenyGenerator.generateTree(10);
        //Phylogeny tree2 = PhylogenyGenerator.generateTree(10);
        //foresterNewickParser.displayPhylogeny(tree1);
        //foresterNewickParser.displayPhylogeny(tree2);

        for (int i = 100; i < 200000; i+=100) {
            Phylogeny tree = PhylogenyGenerator.generateTree(i);
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
}
