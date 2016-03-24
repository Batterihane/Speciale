package Utilities;

import n_squared.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
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
        renameTreeLeavesLeftToRight(tree2);
//        Archaeopteryx.createApplication(tree2);
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

    public static void renameTreeLeavesLeftToRight(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        int i = 0;
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            if(currentNode.isExternal()){
                currentNode.setName(i + "");
                i++;
            }
        }
    }

    public static Phylogeny generateBaseCaseTree(int size){
        Random random = new Random();

        List<PhylogenyNode> nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(i + "");
            nodes.add(newNode);
        }

        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();
        tree.setRoot(root);
        PhylogenyNode currentNode = root;
        while (nodes.size() > 2){
            int i = random.nextInt(nodes.size());
            PhylogenyNode leaf = nodes.get(i);
            nodes.remove(i);
            currentNode.setChild1(leaf);
            PhylogenyNode newInternalNode = new PhylogenyNode();
            currentNode.setChild2(newInternalNode);
            currentNode = newInternalNode;
        }
        currentNode.setChild1(nodes.get(0));
        currentNode.setChild2(nodes.get(1));
        return tree;
    }
}
