package nlogn;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Thomas on 18-02-2016.
 */
public class MAST {

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
//        Phylogeny tree = foresterNewickParser.parseNewickFile("treess\\Tree2.new");

        Phylogeny tree1 = PhylogenyGenerator.generateTree(10);
        Phylogeny tree2 = PhylogenyGenerator.generateTree(10);
        MAST mast = new MAST();
//        List<PhylogenyNode> firstDecomposition = mast.computeFirstDecomposition(tree);
//        for (PhylogenyNode node : firstDecomposition){
//            System.out.println(node.getName());
//        }
        foresterNewickParser.displayPhylogeny(tree1);
        foresterNewickParser.displayPhylogeny(tree2);
        mast.getMAST(tree1, tree2);
    }

    public Phylogeny getMAST(Phylogeny tree1, Phylogeny tree2){
        addNodeDataReferences(tree1);
        addNodeDataReferences(tree2);

        List<PhylogenyNode> tree1Decomposition = computeFirstDecomposition(tree1);
        List<List<PhylogenyNode>> tree2Decomposition = computeSecondDecomposition(tree2);

        List<Phylogeny> siSubtrees = induceSubtrees(tree1Decomposition, tree1, tree2);

//        throw new NotImplementedException();
        return new Phylogeny();
    }

    public List<Phylogeny> induceSubtrees(List<PhylogenyNode> centroidPath, Phylogeny tree1, Phylogeny tree2){
        int i = tree1.getNumberOfExternalNodes();
        updateMiNumbers(centroidPath);

        PhylogenyNode[] sortedTree1Leaves = sortTree1LeavesAndSetTwins(tree1, tree2);

        List<PhylogenyNode>[] sortedMiTree2Leaves = new List[centroidPath.size()];
        for (PhylogenyNode leaf : sortedTree1Leaves){
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(leaf);
            int miNumber = mastNodeData.getMiNumber();
            if (miNumber == 0) continue;
            if(sortedMiTree2Leaves[miNumber] == null) sortedMiTree2Leaves[miNumber] = new ArrayList<>();
            sortedMiTree2Leaves[miNumber].add(mastNodeData.getTwin());
        }

        List<Phylogeny> result = new ArrayList<>();
        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree2);
        long time = System.nanoTime();
        for (List<PhylogenyNode> miList : sortedMiTree2Leaves){
            if(miList == null) continue;
            result.add(subtreeProcessor.induceSubtree(miList));
        }
        System.out.println((int)((System.nanoTime() - time)/(i*(Math.log(i)/Math.log(2)))));
        return result;
    }

    public List<PhylogenyNode> computeFirstDecomposition(Phylogeny tree){
        tree.recalculateNumberOfExternalDescendants(true); // TODO: maybe move to constructor or elsewhere

        List<PhylogenyNode> result = new ArrayList<>();

        PhylogenyNode currentNode = tree.getRoot();
        while (!currentNode.isExternal()){
            result.add(currentNode);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();
            currentNode = firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()
                    ? firstChild : secondChild;
        }
        result.add(currentNode);

        return result;
    }

    public List<List<PhylogenyNode>> computeSecondDecomposition(Phylogeny tree){
        tree.recalculateNumberOfExternalDescendants(true);

        List<List<PhylogenyNode>> result = new ArrayList<>();
        Stack<PhylogenyNode> remainingStartNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        if(root.isExternal()) return result;
        remainingStartNodes.add(root);

        while (!remainingStartNodes.isEmpty()){
            PhylogenyNode firstNode = remainingStartNodes.pop();
            List<PhylogenyNode> newPath = new ArrayList<>();
            PhylogenyNode currentNode = firstNode;

            while (!currentNode.isExternal()){
                newPath.add(currentNode);
                currentNode.setLink(firstNode);
                PhylogenyNode firstChild = currentNode.getChildNode1();
                PhylogenyNode secondChild = currentNode.getChildNode2();
                if(firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()){
                    currentNode = firstChild;
                    if(!secondChild.isExternal()) remainingStartNodes.push(secondChild);
                }
                else {
                    currentNode = secondChild;
                    if(!firstChild.isExternal()) remainingStartNodes.push(firstChild);
                }
            }
            newPath.add(currentNode);
            currentNode.setLink(firstNode);
            result.add(newPath);
        }

        return result;
    }

    public void updateMiNumbers(List<PhylogenyNode> centroidPath)
    {
        for (int i = 0; i < centroidPath.size() - 1; i++)
        {
            PhylogenyNode currentNode = centroidPath.get(i);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();

            PhylogenyNode sNode = (centroidPath.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;

            if (sNode.isExternal()) {
                MASTNodeData mastNodeData = ((NodeDataReference) sNode.getNodeData().getReference()).getMastNodeData();
                mastNodeData.setMiNumber(i+1);
            }
            else {
                for (PhylogenyNode sChild : sNode.getAllExternalDescendants()) {
                    MASTNodeData mastNodeData = ((NodeDataReference) sChild.getNodeData().getReference()).getMastNodeData();
                    mastNodeData.setMiNumber(i+1);
                }
            }
        }
    }

    public PhylogenyNode[] sortTree1LeavesAndSetTwins(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree2Leaves = new ArrayList<>();
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPreorder();
        while (tree2Iterator.hasNext()){
            PhylogenyNode currentNode = tree2Iterator.next();
            if(currentNode.isExternal()) tree2Leaves.add(currentNode);
        }

        int[] tree2LeavesOrdering = getLeavesOrdering(tree2Leaves);
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree2LeavesOrdering.length];
        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPreorder();
        while (tree1Iterator.hasNext()){
            PhylogenyNode currentNode = tree1Iterator.next();
            if(currentNode.isExternal()){
                int name = Integer.parseInt(currentNode.getName());
                int tree2Index = tree2LeavesOrdering[name];
                sortedTree1Leaves[tree2Index] = currentNode;

                // Set twin
                MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
                mastNodeData.setTwin(tree2Leaves.get(tree2Index));
            }


        }
        return sortedTree1Leaves;
    }

    private int[] getLeavesOrdering(List<PhylogenyNode> tree2Leaves) {

        int[] treeLeavesOrdering = new int[tree2Leaves.size()];

        for (int i = 0; i < tree2Leaves.size(); i++) {
            PhylogenyNode currentNode = tree2Leaves.get(i);
            int index = Integer.parseInt(currentNode.getName());
            treeLeavesOrdering[index] = i;
        }
        return treeLeavesOrdering;
    }

    private void addNodeDataReferences(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            NodeDataReference nodeDataReference = new NodeDataReference();
            nodeDataReference.setMastNodeData(new MASTNodeData());
            currentNode.getNodeData().addReference(nodeDataReference);
        }
    }

    private MASTNodeData getMASTNodeDataFromNode(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getMastNodeData();
    }
}
