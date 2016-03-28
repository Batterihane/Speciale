package Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 23-03-2016.
 */
public class LongestIncreasingSubsequence {

    public static void main(String[] args) {
        int[] a = new int[20];
        a[0] = 9;
        a[1] = 2;
        a[2] = 18;
        a[3] = 15;
        a[4] = 6;
        a[5] = 4;
        a[6] = 16;
        a[7] = 12;
        a[8] = 7;
        a[9] = 11;
        a[10] = 19;
        a[11] = 10;
        a[12] = 13;
        a[13] = 5;
        a[14] = 14;
        a[15] = 1;
        a[16] = 17;
        a[17] = 0;
        a[18] = 3;
        a[19] = 8;
        for (int i : findLISModified(a)){
            System.out.print(i + " ");
        }
    }

    public static int[] findLIS(int[] numbers) {
        int[] parents = new int[numbers.length];
        List<Integer> increasingSequence= new ArrayList<>();
        List<Integer> increasingSequenceIndices = new ArrayList<>();
        increasingSequence.add(numbers[0]);
        increasingSequenceIndices.add(0);

        for (int i = 1; i < numbers.length; i++) {
            int currentNumber = numbers[i];
            int lastElementIndex = increasingSequenceIndices.size()-1;
            if(currentNumber > increasingSequence.get(lastElementIndex)){
                increasingSequence.add(currentNumber);
                increasingSequenceIndices.add(i);
                parents[i] = increasingSequenceIndices.get(lastElementIndex);
            }
            else {
                int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                increasingSequence.set(currentNumberIndex, currentNumber);
                increasingSequenceIndices.set(currentNumberIndex, i);
                if(currentNumberIndex > 0)
                    parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                else parents[i] = -1;
            }
        }

        int resultSize = increasingSequence.size();
        int[] result = new int[resultSize];
        Integer index = increasingSequenceIndices.get(resultSize - 1);
        result[resultSize-1] = numbers[index];
        for (int i = resultSize-2; i >= 0; i--) {
            index = parents[index];
            result[i] = numbers[index];
        }

        return result;
    }

    private static int binarySearch(List<Integer> a, int key) {
        int low = 0;
        int high = a.size() - 2;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a.get(mid);

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return low;  // key not found.
    }


    public static int[] findLISModified(int[] numbers) {
        int[] parents = new int[numbers.length];
        int[] beforeParents = new int[numbers.length]; // necessary for when last element is moved
        List<Integer> increasingSequence= new ArrayList<>();
        List<Integer> increasingSequenceIndices = new ArrayList<>();
        increasingSequence.add(numbers[0]);
        increasingSequenceIndices.add(0);
        increasingSequence.add(numbers[1]);
        increasingSequenceIndices.add(1);
        parents[1] = 0;

        for (int i = 2; i < numbers.length; i++) {
            int currentNumber = numbers[i];
            int lastElementIndex = increasingSequenceIndices.size()-1;
            Integer parent = increasingSequence.get(lastElementIndex);
            int grandParentIndex = parents[increasingSequenceIndices.get(lastElementIndex)];
            int grandParent = numbers[grandParentIndex];
            if(parent > grandParent){
                if(currentNumber > grandParent){
                    increasingSequence.add(currentNumber);
                    increasingSequenceIndices.add(i);
                    parents[i] = increasingSequenceIndices.get(lastElementIndex);
                    beforeParents[i] = increasingSequenceIndices.get(lastElementIndex-1);
                }
                else {
                    int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                    increasingSequence.set(currentNumberIndex, currentNumber);
                    increasingSequenceIndices.set(currentNumberIndex, i);
                    if(currentNumberIndex > 0)
                        parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                    else parents[i] = -1;
                }
            }
            else if(currentNumber > increasingSequence.get(lastElementIndex-1) || currentNumber > parent) {
                if(parent < increasingSequence.get(lastElementIndex-1)){
                    increasingSequence.set(lastElementIndex-1, parent);
                    Integer parentIndex = increasingSequenceIndices.get(lastElementIndex);
                    increasingSequenceIndices.set(lastElementIndex-1, parentIndex);
                    parents[parentIndex] = beforeParents[parentIndex];
                }

                increasingSequence.set(lastElementIndex, currentNumber);
                increasingSequenceIndices.set(lastElementIndex, i);
                parents[i] = increasingSequenceIndices.get(lastElementIndex-1);
                if(lastElementIndex > 1)
                    beforeParents[i] = increasingSequenceIndices.get(lastElementIndex-2);
            }
            else {
                int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                increasingSequence.set(currentNumberIndex, currentNumber);
                increasingSequenceIndices.set(currentNumberIndex, i);
                if(currentNumberIndex > 0)
                    parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                else parents[i] = -1;
            }
        }

        int resultSize = increasingSequence.size();
        int[] result = new int[resultSize];
        Integer index = increasingSequenceIndices.get(resultSize - 1);
        result[resultSize-1] = numbers[index];
        for (int i = resultSize-2; i >= 0; i--) {
            index = parents[index];
            result[i] = numbers[index];
        }

        return result;
    }
}
