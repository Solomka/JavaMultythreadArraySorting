import java.util.ArrayList;
import java.util.List;

public class ArraysMerger {

	public static volatile ArrayList<String[]> res = new ArrayList<String[]>();

	public static String[] mergeMultithread(ArrayList<String[]> arrays) {
		if (arrays.size() == 1) {
			return arrays.get(0);
		} else {
			while (arrays.size() > 1) {
				arrays = new ArrayList<String[]>(mergeBranches(arrays));
			}
			return arrays.get(0);
		}
	}

	private static ArrayList<String[]> mergeBranches(ArrayList<String[]> toMerge) {
		res = new ArrayList<String[]>();
		if (toMerge.size() % 2 == 0) { // even number of threads
			List<Thread> threads = new ArrayList<Thread>(toMerge.size() / 2);
			for (int i = 0; i < (toMerge.size() / 2); i++) {
				Thread thread = new MergingThread(toMerge.get(i * 2),
						toMerge.get(i * 2 + 1));
				threads.add(thread);
			}
			for (Thread thread : threads) {
				thread.start();
			}
			for (int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return res;
		} else {
			int size = toMerge.size() - 1;
			List<Thread> threads = new ArrayList<Thread>(size);
			for (int i = 0; i < (size / 2); i++) {
				Thread thread = new MergingThread(toMerge.get(i * 2),
						toMerge.get(i * 2 + 1));
				threads.add(thread);
			}
			for (Thread thread : threads) {
				thread.start();
			}
			for (int i = 0; i < threads.size(); i++) {
				try {
					threads.get(i).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			res.add(toMerge.get(toMerge.size() - 1));
			return res;
		}
	}

	public static synchronized void writeResult(String[] result) {
		res.add(result);
	}

	/*
	 * merge two sorted arrays into one array
	 */
	public static String[] merge(String[] arr1, String[] arr2) {

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
