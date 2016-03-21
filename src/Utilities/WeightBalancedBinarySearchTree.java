package Utilities;

/**
 * Created by Thomas on 18-03-2016.
 */
public class WeightBalancedBinarySearchTree {
    public static void main(String[] args) {
        int[] a = new int[5];
        a[0] = 0;
        a[1] = 1;
        a[2] = 4;
        a[3] = 6;
        a[4] = 9;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        System.out.println(weightBalancedBinarySearchTree.findIndexOfCut(a, 9, 0, 4));
    }

    public void tester(int[] weights, int cut, int from, int to){
        if(to == from + 1 || to == from) return;

        int indexOfCut = findIndexOfCut(weights, cut, from, to);
        int leftFrom = from;
        int leftTo = indexOfCut - 1;
        int leftCut =  leftFrom + (((leftTo - leftFrom) + 1) / 2 - 1);
        tester(weights, leftCut, leftFrom, leftTo);

        int rightFrom = indexOfCut + 1;
        int rightTo = to;
        int rightCut =  rightFrom + (((rightTo - rightFrom) + 1) / 2 - 1);
        tester(weights, rightCut, rightFrom, rightTo);

    }

    public int findIndexOfCut(int[] weights, int cut, int i, int j){
        if(j == i + 1 || j == i) return j;

        int middleIndex = (j - i + 1) / 2;
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
        int kIndex = fromLeft ? from + k : to - k;
        int previousKIndex = fromLeft ? from : to;
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
            kIndex = fromLeft ? from + k : to - k;
        }

        if(fromLeft) return binarySearch(weights, previousKIndex, kIndex, cut);
        else return binarySearch(weights, kIndex, previousKIndex, cut);
    }

    private int binarySearch(int[] a, int fromIndex, int toIndex, int key) {
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
}
