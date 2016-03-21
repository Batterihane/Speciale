package Tests;

import Utilities.WeightBalancedBinarySearchTree;

/**
 * Created by Thomas on 21-03-2016.
 */
public class WeightBalancedBinarySearchTreeTest {
    public static void main(String[] args) {
        int size = 100;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();

        int[] weights = new int[size];
        for (int i = 0; i < size; i++) {
            weights[i] = i;
        }

        for (int i = 10; i < size; i+=10) {
            int leftOfMiddleIndex = (i + 1) / 2 - 1;
            long time = System.nanoTime();
            weightBalancedBinarySearchTree.tester(weights, leftOfMiddleIndex, 0, i);
            System.out.println(System.nanoTime() - time);
        }
    }
}
