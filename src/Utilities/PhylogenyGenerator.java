package Utilities;

import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Thomas on 10-02-2016.
 */
public class PhylogenyGenerator {

    public static void main(String[] args) {
        Phylogeny tree1 = generateTree(10);
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        foresterNewickParser.displayPhylogeny(tree1);
        Phylogeny tree2 = generateTree(10);
        foresterNewickParser.displayPhylogeny(tree2);
    }

    public static Phylogeny generateTree(int size){
        Random random = new Random();

        List<PhylogenyNode> nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName("Leaf_" + i);
            nodes.add(newNode);
        }

        while(nodes.size() > 1){
            int i = random.nextInt(nodes.size());
            PhylogenyNode node1 = nodes.get(i);
            nodes.remove(i);
            int j = random.nextInt(nodes.size());
            PhylogenyNode node2 = nodes.get(j);
            nodes.remove(j);
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setChild1(node1);
            newNode.setChild2(node2);
            nodes.add(newNode);
        }

        Phylogeny tree = new Phylogeny();
        tree.setRoot(nodes.get(0));

        return tree;
    }
}
