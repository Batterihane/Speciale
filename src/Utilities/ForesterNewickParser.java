package Utilities;

import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nikolaj on 14-11-2015.
 */
public class ForesterNewickParser {


    public static void main(String[] args) throws IOException {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        Phylogeny tree = foresterNewickParser.parseNewickFile("trees\\Tree4.new");
        foresterNewickParser.displayPhylogeny(tree);
        System.out.println(tree.getRoot().getNumberOfDescendants());
        System.out.println(tree.getNode(3).getName());
        System.out.println(tree.getNode(3).isExternal());
        System.out.println(tree.getNode(3).isInternal());
    }

    public void displayPhylogeny(Phylogeny tree)
    {
        Archaeopteryx.createApplication(tree);
    }

    public Phylogeny parseNewickFile(String filePath)
    {
        PhylogenyParser parserDependingOnFileType;
        File treeFile;

        try {
            treeFile = new File(filePath);
            parserDependingOnFileType = ParserUtils.createParserDependingOnFileType(treeFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Phylogeny[] phys;
        Phylogeny tree;
        try {
            phys = PhylogenyMethods.readPhylogenies(parserDependingOnFileType, treeFile);
            tree = phys[0];
        }
        catch ( final IOException e ) {
            e.printStackTrace();
            return null;
        }

        return tree;
    }


    //Random shit
        /*
        String nhx = "(mammal,(turtle,rayfinfish,(frog,salamander)))";
        Phylogeny phylogeny = new Phylogeny();

        final File treefile = new File("C:\\Users\\Nikolaj\\BioTreeAndSeq\\BioTrees\\trees\\quickTree\\testnewick.new");
        PhylogenyParser parserDependingOnFileType = ParserUtils.createParserDependingOnFileType(treefile, true);

        Phylogeny[] phys = null;
        try {
            phys = PhylogenyMethods.readPhylogenies(parserDependingOnFileType, treefile);
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
        //org.forester.phylogeny.Phylogeny.class.newInstance().
        //Phylogeny ph = org.forester.phylogeny.Phylogeny.class.newInstance().
        Phylogeny tree = phys[0];
        PhylogenyNode root = tree.getRoot();
        System.out.println(tree.isRerootable());
        //tree.reRoot(tree.getNode(0));
        //tree.reRoot(tree.getNode("turtle"));
        PhylogenyNodeIterator phylogenyNodeIterator = tree.iteratorPreorder();

            while(phylogenyNodeIterator.hasNext()) {
            System.out.println(phylogenyNodeIterator.next().toString());
        }


        List<PhylogenyNode> allDescendants = tree.getNode(0).getAllDescendants();
        allDescendants.forEach(node -> System.out.println(node.getName()));
        System.out.println(allDescendants.size());
        MainFrame application = Archaeopteryx.createApplication(tree);
      */
}
