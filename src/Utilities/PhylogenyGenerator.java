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

    public static Phylogeny generateTreeExampleA() {
        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();

        PhylogenyNode[] leaves = new PhylogenyNode[9];

        for (int i = 1; i < leaves.length; i++) {
            PhylogenyNode leaf = new PhylogenyNode();
            leaf.setName(i+"");
            leaves[i] = leaf;
        }

        PhylogenyNode NodeB = new PhylogenyNode();
        PhylogenyNode NodeC = new PhylogenyNode();
        PhylogenyNode NodeD = new PhylogenyNode();
        PhylogenyNode NodeE = new PhylogenyNode();
        PhylogenyNode NodeF = new PhylogenyNode();
        PhylogenyNode NodeG = new PhylogenyNode();

        NodeC.setChild1(leaves[1]);
        NodeC.setChild2(leaves[2]);
        NodeD.setChild1(leaves[3]);
        NodeD.setChild2(leaves[4]);
        NodeF.setChild1(leaves[5]);
        NodeF.setChild2(leaves[6]);
        NodeG.setChild1(leaves[7]);
        NodeG.setChild2(leaves[8]);

        NodeB.setChild1(NodeC);
        NodeB.setChild2(NodeD);
        NodeE.setChild1(NodeF);
        NodeE.setChild2(NodeG);

        root.setChild1(NodeB);
        root.setChild2(NodeE);
        tree.setRoot(root);

        return tree;
    }

    public static Phylogeny generateTreeExampleH() {
        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();

        PhylogenyNode[] leaves = new PhylogenyNode[9];

        for (int i = 1; i < leaves.length; i++) {
            PhylogenyNode leaf = new PhylogenyNode();
            leaf.setName(i+"");
            leaves[i] = leaf;
        }

        PhylogenyNode NodeI = new PhylogenyNode();
        PhylogenyNode NodeJ = new PhylogenyNode();
        PhylogenyNode NodeL = new PhylogenyNode();
        PhylogenyNode NodeM = new PhylogenyNode();
        PhylogenyNode NodeN = new PhylogenyNode();
        PhylogenyNode NodeF = new PhylogenyNode();

        NodeJ.setChild1(leaves[1]);
        NodeJ.setChild2(leaves[8]);
        NodeL.setChild1(leaves[5]);
        NodeL.setChild2(leaves[6]);
        NodeN.setChild1(leaves[2]);
        NodeN.setChild2(leaves[7]);
        NodeF.setChild1(leaves[3]);
        NodeF.setChild2(leaves[4]);

        NodeI.setChild1(NodeJ);
        NodeI.setChild2(NodeL);
        NodeM.setChild1(NodeN);
        NodeM.setChild2(NodeF);

        root.setChild1(NodeI);
        root.setChild2(NodeM);
        tree.setRoot(root);

        return tree;
    }
}
