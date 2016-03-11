package Utilities;

import n_squared.MAST;
import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Thomas on 10-02-2016.
 */
public class PhylogenyGenerator {

    public static void main(String[] args) {
        Phylogeny tree1 = generateTree(1000);

        Phylogeny tree2 = generateTree(1000);

        MAST mast = new MAST();
        //int size = mast.getMAST(tree1, tree2);
        //System.out.println(size);
    }

    public static Phylogeny generateTree(int size){
        Random random = new Random();

        List<PhylogenyNode> nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(i + "");
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
            newNode.setName(newNode.getId() + "");
        }

        Phylogeny tree = new Phylogeny();
        tree.setRoot(nodes.get(0));

        return tree;
    }
}
