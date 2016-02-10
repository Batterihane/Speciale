package Utilities;

import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikolaj on 10-02-2016.
 */
public class PhylogenyParser {
    private Phylogeny phylogeny;

    public void toNewick(Phylogeny phylogeny, String fileName, boolean writeDistancesToParent) {
        final PhylogenyWriter writer = new PhylogenyWriter();
        final File outfile = new File("trees\\random\\"+fileName+".new");
        try {
            writer.toNewHampshire(phylogeny, writeDistancesToParent, true, outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        PhylogenyParser phylogenyParser = new PhylogenyParser();

        Phylogeny phylogeny1 = PhylogenyGenerator.generateTree(2000);
        phylogenyParser.toNewick(phylogeny1, "bigOne", true);

        //Phylogeny phylogeny2 = foresterNewickParser.parseNewickFile("trees\\random\\something.new");
        //foresterNewickParser.displayPhylogeny(phylogeny2);
    }
}
