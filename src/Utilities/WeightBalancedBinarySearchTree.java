package Utilities;

import java.util.Stack;

/**
 * Created by Thomas on 18-03-2016.
 */
public class WeightBalancedBinarySearchTree {
    private int numberOfCallsToFindIndexOfCut = 0;

    public static void main(String[] args) {
        int[] a = new int[5];
        a[0] = 0;
        a[1] = 1;
        a[2] = 4;
        a[3] = 6;
        a[4] = 9;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        System.out.println(weightBalancedBinarySearchTree.findIndexOfCut(a, 1, 0, 4));
    }

    public void tester(int[] weights, int cut, int from, int to){
        if(to == from + 1 || to == from) return;

        int indexOfCut = binarySearch(weights, cut, from, to);
        if(indexOfCut != from){
            int leftFrom = from;
            int leftTo = indexOfCut - 1;
//            int leftCut =  leftFrom + (((leftTo - leftFrom) + 1) / 2 - 1);
            int leftCut = leftFrom + 1;
            tester(weights, leftCut, leftFrom, leftTo);
        }

        if(indexOfCut != to){
            int rightFrom = indexOfCut + 1;
            int rightTo = to;
//            int rightCut =  rightFrom + (((rightTo - rightFrom) + 1) / 2 - 1);
            int rightCut = rightFrom + 1;
            tester(weights, rightCut, rightFrom, rightTo);
        }

    }

    public void testerIterative(int[] weights, int firstCut, int from, int to){

        Stack<IntegerTriplet> stack = new Stack<>();
        stack.push(new IntegerTriplet(firstCut, from, to));

        while(!stack.isEmpty()){
            IntegerTriplet triplet = stack.pop();
            int cut = triplet.getFirst();
            int i = triplet.getSecond();
            int j = triplet.getThird();

            if(j == i + 1 || j == i) continue;

            int indexOfCut = findIndexOfCut(weights, cut, i, j);
            if(indexOfCut != i+1){
                int leftFrom = i;
                int leftTo = indexOfCut - 1;
                int leftCut =  leftFrom + (((leftTo - leftFrom) + 1) / 2 - 1);
//                int leftCut = leftFrom;
                stack.push(new IntegerTriplet(leftCut, leftFrom, leftTo));
            }

            if(indexOfCut != j){
                int rightFrom = indexOfCut;
                int rightTo = j;
                int rightCut =  rightFrom + (((rightTo - rightFrom) + 1) / 2 - 1);
//                int rightCut = rightFrom;
                stack.push(new IntegerTriplet(rightCut, rightFrom, rightTo));
            }
        }
    }

    public int findIndexOfCut(int[] weights, int cut, int i, int j){
        numberOfCallsToFindIndexOfCut++;
        if(j == i + 1 || j == i) return j;

        int middleIndex = i + (j - i + 1) / 2;
        if(weights[middleIndex] == cut) return middleIndex;

        int from,to;
        boolean fromLeft;
        if(weights[middleIndex] < cut){
            from = middleIndex + 1;
            to = j;
            fromLeft = false;
        }
        else {
            from = i;
            to = middleIndex - 1;
            fromLeft = true;
        }

        int k = 0;
        int kIndex = fromLeft ? from + 1 : to;
        int previousKIndex = fromLeft ? from + 1 : to;
        while(true){
            if(kIndex > to){
                kIndex = to;
                break;
            }
            if(kIndex < from){
                kIndex = from;
                break;
            }

            if(weights[kIndex] == cut) return kIndex;

            if(fromLeft && weights[kIndex] > cut) break;
            if(!fromLeft && weights[kIndex] < cut) break;

            k = (k + 1)*2 - 1;
            previousKIndex = kIndex;
            kIndex = fromLeft ? from + k + 1 : to - k;
        }

        if(fromLeft) return binarySearch(weights, cut, previousKIndex, kIndex);
        else return binarySearch(weights, cut, kIndex, previousKIndex);
    }

    private int binarySearch(int[] a, int key, int fromIndex, int toIndex) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return low;  // key not found.
    }

    public int getNumberOfCallsToFindIndexOfCut() {
        return numberOfCallsToFindIndexOfCut;
    }

    private class IntegerTriplet {
        private int first;
        private int second;
        private int third;

        public IntegerTriplet(int first, int second, int third){
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public int getThird() {
            return third;
        }

        public void setThird(int third) {
            this.third = third;
        }
    }
}
