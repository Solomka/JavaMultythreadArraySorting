import java.util.Arrays;

/**
 * Binary Insertion sort for String types
 * 
 * @author Solomka
 *
 */
/*
 * In worst case scenario – Normal insertion sort takes O( i ) time in its
 * ith iteration whereas using binary search can reduce it to O(log( i )).
 * 
 * Note – Overall time complexity of the algorithm in the worst case is
 * still O(n2) because of the number of swaps required to put every element
 * at the correct location.
 */
public final class BinaryInsertionSort {

	private BinaryInsertionSort() {
		throw new RuntimeException();
	}

	public static void binaryInsertionSort(String array[]) {
		for (int i = 1; i < array.length; i++) {
			String x = array[i];

			// Find location to insert using binary search
			int j = Math.abs(Arrays.binarySearch(array, 0, i, x) + 1);

			// Shifting array to one location right
			System.arraycopy(array, j, array, j + 1, i - j);

			// Placing element at its correct location
			array[j] = x;
		}
	}

}
