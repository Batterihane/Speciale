package Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 23-03-2016.
 */
public class LongestIncreasingSubsequence {

    public static void main(String[] args) {
        int[] a = new int[10];
        a[0] = 2;
        a[1] = 99;
        a[2] = 3;
        a[3] = 4;
        a[4] = 8;
        a[5] = 9;
        a[6] = 1;
        a[7] = 5;
        a[8] = 6;
        a[9] = 7;
        for (int i : findLIS(a)){
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
}
