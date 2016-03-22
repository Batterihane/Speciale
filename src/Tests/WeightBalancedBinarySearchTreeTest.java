package Tests;

import Utilities.WeightBalancedBinarySearchTree;

/**
 * Created by Thomas on 21-03-2016.
 */
public class WeightBalancedBinarySearchTreeTest {
    public static void main(String[] args) {
        int size = 10000000;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();

        int[] weights = new int[size];
        for (int i = 0; i < size; i++) {
            weights[i] = i;
        }

////        int leftOfMiddleIndex = (size + 1) / 2 - 1;
//        int leftOfMiddleIndex = 0;
//        weightBalancedBinarySearchTree.testerIterative(weights, leftOfMiddleIndex, 0, size-1);
//        System.out.println(weightBalancedBinarySearchTree.getNumberOfCallsToFindIndexOfCut());

        for (int i = 10000; i < size; i+=10000) {
            int leftOfMiddleIndex = (i + 1) / 2 - 1;
//            int leftOfMiddleIndex = 1;
            long time = System.nanoTime();
            weightBalancedBinarySearchTree.testerIterative(weights, leftOfMiddleIndex, 0, i-1);
            System.out.println((System.nanoTime() - time)/i);
        }
    }
}
