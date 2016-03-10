package nlogn;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Thomas on 18-02-2016.
 */
public class MAST {

    public List<Phylogeny> induceSubtrees(List<PhylogenyNode> centroidPath){
        throw new NotImplementedException();
    }

    public List<PhylogenyNode> computeFirstDecomposition(Phylogeny tree){
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

    private class MiNodeData extends Reference {
        public int getMiNumber() {
            return miNumber;
        }

        public void setMiNumber(int miNumber) {
            this.miNumber = miNumber;
        }

        private int miNumber;

        public MiNodeData() {
            super("");
        }

    }

}
