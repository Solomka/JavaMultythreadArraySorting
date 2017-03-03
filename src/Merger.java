import java.util.ArrayList;
import java.util.List;

public class Merger {

	/*
	 * list of by pair merged sorted arrays
	 * 
	 * volatile - just one thread can add sorted merged array to the list per
	 * moment of time and thread can read from list after list writing is
	 * finished
	 */
	public static volatile ArrayList<String[]> sortedArrays = new ArrayList<String[]>();

	/*
	 * add to list of sorted arrays
	 * 
	 * synchronized - just one thread can add sorted merged array to the list
	 * per
	 */
	public static synchronized void addToSortedList(String[] result) {
		sortedArrays.add(result);
	}

	/**
	 * merge all sorted arrays into one sorted array
	 * 
	 * @param arrays
	 * @return
	 */
	public static String[] sortedArraysMerge(ArrayList<String[]> arrays) {
		/*
		 * if arrays.size() ==1 -> the resulting array is on the first position
		 */
		if (arrays.size() == 1) {
			return arrays.get(0);
			/*
			 * process merging of min of 2 arrays
			 */
		} else {
			while (arrays.size() > 1) {
				arrays = processMerging(arrays);
			}
			return arrays.get(0);
		}
	}

	/**
	 * process merging of each pair of arrays in separate thread
	 * 
	 * @param processArrays
	 * @return
	 */
	private static ArrayList<String[]> processMerging(
			ArrayList<String[]> processArrays) {
		sortedArrays = new ArrayList<String[]>();
		// if even number of threads
		if (processArrays.size() % 2 == 0) {
			// create threads for merging
			List<Thread> threads = new ArrayList<Thread>(
					processArrays.size() / 2);
			for (int i = 0; i < (processArrays.size() / 2); i++) {
				Thread thread = new MergingThread(processArrays.get(i * 2),
						processArrays.get(i * 2 + 1));
				threads.add(thread);
			}
			// start each thread
			for (Thread thread : threads) {
				thread.start();
			}
			// wain until each thread die
			for (int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return sortedArrays;
			// if odd number of threads
		} else {
			int size = processArrays.size() - 1;
			List<Thread> threads = new ArrayList<Thread>(size);
			for (int i = 0; i < (size / 2); i++) {
				Thread thread = new MergingThread(processArrays.get(i * 2),
						processArrays.get(i * 2 + 1));
				threads.add(thread);
			}
			// start each thread
			for (Thread thread : threads) {
				thread.start();
			}
			// wait until each thread die
			for (int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// to sort the last array with completely sorted last finish array
			sortedArrays.add(processArrays.get(processArrays.size() - 1));
			return sortedArrays;
		}
	}

	/*
	 * merge two sorted arrays into one array
	 */
	public static String[] mergePairOfArrays(String[] arr1, String[] arr2) {

		String[] mergedArray = new String[arr1.length + arr2.length];
		int i = 0, j = 0, k = 0;
		while (i < arr1.length && j < arr2.length) {
			if (arr1[i].compareToIgnoreCase(arr2[j]) < 0) {
				mergedArray[k] = arr1[i];
				i++;
			} else {
				mergedArray[k] = arr2[j];
				j++;
			}
			k++;
		}
		// merge additional elements left in arr1.
		while (i < arr1.length) {
			mergedArray[k] = arr1[i];
			i++;
			k++;
		}
		// merge additional elements left in arr2.
		while (j < arr2.length) {
			mergedArray[k] = arr2[j];
			j++;
			k++;
		}
		return mergedArray;
	}

}
