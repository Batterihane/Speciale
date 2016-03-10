package nlogn;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Thomas on 18-02-2016.
 */
public class MAST {

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
//        Phylogeny tree = foresterNewickParser.parseNewickFile("treess\\Tree2.new");

        Phylogeny tree = PhylogenyGenerator.generateTree(10);
        MAST mast = new MAST();
        List<PhylogenyNode> firstDecomposition = mast.computeFirstDecomposition(tree);
        for (PhylogenyNode node : firstDecomposition){
            System.out.println(node.getName());
        }
        foresterNewickParser.displayPhylogeny(tree);
    }

    public Phylogeny getMAST(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree1Decomposition = computeFirstDecomposition(tree1);
        List<List<PhylogenyNode>> tree2Decomposition = computeSecondDecomposition(tree2);

        throw new NotImplementedException();
    }

    public List<Phylogeny> induceSubtrees(List<PhylogenyNode> centroidPath){
        throw new NotImplementedException();
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

    public void updateSiNumbers(List<PhylogenyNode> decomposition)
    {
        for (int i = 0; i < decomposition.size() - 1; i++)
        {
            PhylogenyNode currentNode = decomposition.get(i);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();

            PhylogenyNode sNode = decomposition.get(i+1).getName().equals(""+ firstChild.getName()) ? secondChild : firstChild;

            if (sNode.isExternal()) {
                MiNodeData nodeData = new MiNodeData();
                nodeData.setMiNumber(i+1);
                sNode.getNodeData().addReference(nodeData);
            }
            else {
                for (PhylogenyNode sChild : sNode.getAllExternalDescendants()) {
                    MiNodeData nodeData = new MiNodeData();
                    nodeData.setMiNumber(i+1);
                    sChild.getNodeData().addReference(nodeData);
                }
            }
        }
    }


}
