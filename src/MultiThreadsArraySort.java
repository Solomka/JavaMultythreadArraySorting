import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadsArraySort {

	/*
	 * read data from file line by line
	 */
	public static String[] readFileData(String file, int size)
			throws IOException {

		String[] data = new String[size];

		FileInputStream fstream1 = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fstream1);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));

		System.out.println("File size:" + br.readLine() + "\nFile lines: "
				+ size);

		for (int i = 0; i < size; i++) {
			data[i] = br.readLine();
		}
		return data;
	}

	/*
	 * Threads num and data size config
	 */

	// 500 000 - max number of lines to read from file
	private static final int LINES_NUM_TO_READ = 1000;
	private static final int THREADS_NUM = 3;

	private static ArrayList<String[]> insSortArr;
	private static ArrayList<String[]> radixSortArr;

	/*
	 * Split main array into threads subarrays
	 */
	private static ArrayList<String[]> splitArray(String[] data) {
		ArrayList<String[]> arrays = new ArrayList<String[]>(THREADS_NUM);

		int remainder = LINES_NUM_TO_READ % THREADS_NUM;
		int arrSize = LINES_NUM_TO_READ / THREADS_NUM;
		int counter = 0;

		// splitting to N arrays
		for (int i = 0; i < THREADS_NUM; i++) {
			String[] temp;
			if (remainder > 0) {
				temp = new String[arrSize + 1];
			} else {
				temp = new String[arrSize];
			}
			for (int j = 0; j < arrSize; j++) {
				temp[j] = data[counter];
				counter++;
			}
			if (remainder > 0) {
				temp[temp.length - 1] = data[counter];
				counter++;
				remainder--;
			}
			arrays.add(temp);
		}
		return arrays;

	}

	/*
	 * process multythreading array sorting
	 */
	private static void processMultyThreadsSorting(String sortingType) {

		double st = 0;
		double en = 0;

		if (sortingType == "binary") {

			st = System.nanoTime();
			// create pool of threads
			ExecutorService pool = Executors.newFixedThreadPool(THREADS_NUM);
			List<Callable<Object>> tasks = new ArrayList<>();
			try {
				for (String[] subArray : insSortArr) {
					tasks.add(new Callable<Object>() {
						public Object call() throws Exception {
							BinaryInsertionSort.binaryInsertionSort(subArray);
							return subArray;
						}
					});
				}
				// run pool of threads
				List<Future<Object>> invokeAll = pool.invokeAll(tasks);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				pool.shutdown(); // the end
			}

			/*
			 * merge all separated sorted subarrays into one sorted array each
			 * 2-arrays merging process in new Thread
			 */
			String[] binaryInsertionSortResult = ArraysMerger
					.mergeMultithread(insSortArr);

			en = System.nanoTime();
			/*
			 * write sorted array into the result file with statistics
			 */
			writeResult(st, en, binaryInsertionSortResult,
					"BinaryInsertionSortResult.txt");
		} else if (sortingType == "radix") {
			st = System.nanoTime();
			// create pool of threads
			ExecutorService pool = Executors.newFixedThreadPool(THREADS_NUM);
			List<Callable<Object>> tasks = new ArrayList<>();
			try {
				for (String[] subArray : radixSortArr) {
					tasks.add(new Callable<Object>() {
						public Object call() throws Exception {
							BinaryInsertionSort.binaryInsertionSort(subArray);
							return subArray;
						}
					});
				}
				// run pool of threads
				List<Future<Object>> invokeAll = pool.invokeAll(tasks);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				pool.shutdown(); // the end
			}

			/*
			 * merge all separated sorted subarrays into one sorted array each
			 * 2-arrays merging process in new Thread
			 */
			String[] radixSortResult = ArraysMerger
					.mergeMultithread(radixSortArr);

			en = System.nanoTime();
			/*
			 * write sorted array into the result file with statistics
			 */
			writeResult(st, en, radixSortResult, "RadixSortResult.txt");

		} else {
			//
		}

	}

	private static void writeResult(double st, double en, String[] sortedArray,
			String resultFile) {

		// writing to file
		try (FileWriter writer = new FileWriter(resultFile, false)) {
			for (String element : sortedArray) {
				writer.write(element);
				writer.append('\n');
			}
			writer.write("\nStatistics");
			writer.write("\nSorted in: " + ((en - st) / 1000000000)
					+ " seconds");
			writer.append('\n');
			writer.write("By: " + THREADS_NUM + " threads");
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println(resultFile.substring(0, resultFile.indexOf('.'))
				+ " " + THREADS_NUM + " Thread(s)\nFinished in: "
				+ ((en - st) / 1000000000) + " seconds");
	}

	public static void main(String[] args) throws IOException {

		String[] data = readFileData("TestData.txt", LINES_NUM_TO_READ);

		/*
		 * data for Binary Insertion Sort
		 */

		insSortArr = splitArray(data);

		/*
		 * data for Radix Sort
		 */
		radixSortArr = new ArrayList<String[]>(insSortArr);

		/*
		 * runs BinaryInsertionSort in N threads and write result in file
		 */

		processMultyThreadsSorting("binary");
		/*
		 * runs BinaryInsertionSort in N threads and write result in file
		 */

		processMultyThreadsSorting("radix");

	}
}
