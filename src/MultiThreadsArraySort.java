import java.util.Arrays;

public class MultiThreadsArraySort {

	public static void main(String[] args) {
		final String[] input = { "mother", "father", "sister", "brother", "another", "eclipse" };
		System.out.println("Before Sorting - " + Arrays.toString(input));
		//BinaryInsertionSort.binaryInsertionSort(input);
		RadixSort.radixSort(input);
		System.out.println("After Sorting - " + Arrays.toString(input));

	}

}
