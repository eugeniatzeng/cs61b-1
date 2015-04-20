/* Radix.java */

package radix;

/**
 * Sorts is a class that contains an implementation of radix sort.
 * @author
 */
public class Sorts {


    /**
     *  Sorts an array of int keys according to the values of <b>one</b>
     *  of the base-16 digits of each key. Returns a <b>NEW</b> array and
     *  does not modify the input array.
     *  
     *  @param key is an array of ints.  Assume no key is negative.
     *  @param whichDigit is a number in 0...7 specifying which base-16 digit
     *    is the sort key. 0 indicates the least significant digit which
     *    7 indicates the most significant digit
     *  @return an array of type int, having the same length as "keys"
     *    and containing the same keys sorted according to the chosen digit.
     **/
    public static int[] countingSort(int[] keys, int whichDigit) {
        int mask = 15 << (4 * whichDigit);
        //initialized to 0?
        int[] countArray = new int[16];
        int[] result = new int[keys.length];
        int bits;
        int index;
        for (int x : keys) {
            bits = (x & mask) >> (4 * whichDigit);
            countArray[bits]++;
        }
        for (int i = 1; i < 16; i++){
            countArray[i] = countArray[i] + countArray[i-1];
        }
        for (int x : keys) {
            bits = (x & mask) >> (4 * whichDigit);
            index = countArray[bits] - 1;
            countArray[bits]++;
            result[index] = x;
        }
        return result;
        //YOUR CODE HERE
    }

    /**
     *  radixSort() sorts an array of int keys (using all 32 bits
     *  of each key to determine the ordering). Returns a <b>NEW</b> array
     *  and does not modify the input array
     *  @param key is an array of ints.  Assume no key is negative.
     *  @return an array of type int, having the same length as "keys"
     *    and containing the same keys in sorted order.
     **/
    public static int[] radixSort(int[] keys) {
        for(int i=0;i<8;i++) {
            keys = countingSort(keys, i);
        }
        return keys;
        //YOUR CODE HERE
    }

}
