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

		System.out.println("File size: " + br.readLine() + "\nFile lines: "
				+ size); 

		for (int i = 0; i < size; i++) {
			data[i] = br.readLine();
		}
		return data;
	}

	// 500 000 - max number of lines to read from file
	private static final int LINES_NUM_TO_READ = 1000;
	private static final int THREADS_NUM = 10;
	private static double st, en;

	public static void main(String[] args) throws IOException {
		/*
		 * final String[] input = { "mother", "father", "sister", "brother",
		 * "another", "eclipse" }; System.out.println("Before Sorting - " +
		 * Arrays.toString(input));
		 * //BinaryInsertionSort.binaryInsertionSort(input);
		 * RadixSort.radixSort(input); System.out.println("After Sorting - " +
		 * Arrays.toString(input));
		 */

		String[] data = readFileData("500.000.txt", LINES_NUM_TO_READ);

		/*
		 * data for Binary Insertion Sort
		 */
		ArrayList<String[]> arrays = new ArrayList<String[]>(THREADS_NUM);
		int residue = LINES_NUM_TO_READ % THREADS_NUM;
		int arrSize = (LINES_NUM_TO_READ - residue) / THREADS_NUM;
		int counter = 0; // general counter, in the end must = SIZE.

		// splitting to N arrays
		for (int i = 0; i < THREADS_NUM; i++) {
			String[] temp;
			if (residue > 0) {
				temp = new String[arrSize + 1];
			} else {
				temp = new String[arrSize];
			}
			for (int j = 0; j < arrSize; j++) {
				temp[j] = data[counter];
				counter++;
			}
			if (residue > 0) {
				temp[temp.length - 1] = data[counter];
				counter++;
				residue--;
			}
			arrays.add(temp);
		}

		// data for Radix Sort
		ArrayList<String[]> arrays1 = new ArrayList<String[]>(arrays); // fine
																		// only
																		// for
																		// IMMUTABLE
																		// objects

		/*
		 * runs BInaryInsertionSort in N threads and waits until all done
		 */
		st = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(THREADS_NUM);
		List<Callable<Object>> tasks = new ArrayList<>();
		try {
			for (String[] element : arrays) {
				tasks.add(new Callable<Object>() { // adds tasks
					public Object call() throws Exception {
						BinaryInsertionSort.binaryInsertionSort(element);
						return element;
					}
				});
			}
			List<Future<Object>> invokeAll = pool.invokeAll(tasks); // runs pool
																	// of
																	// threads
																	// and WAIT
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			pool.shutdown(); // the end
		}

		// merging
		String insertionSortResult[] = ArraysMerger.mergeMultithread(arrays);

		en = System.nanoTime();

		// writing to file
		try (FileWriter writer = new FileWriter(
				"BinaryInsertionSortResult.txt", false)) {
			for (String element : insertionSortResult) {
				writer.write(element);
				writer.append('\n');
			}
			writer.write("Sorted in: " + ((en - st) / 1000000000) + " seconds");
			writer.append('\n');
			writer.write("By: " + THREADS_NUM + " threads");
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println("Binary Insertion Sort (" + THREADS_NUM
				+ " Thread(s)) finished in: " + ((en - st) / 1000000000)
				+ " seconds");

		st = 0;
		en = 0;

		/*
		 * runs RadixSort in N threads and waits until all done
		 */
		st = System.nanoTime();
		ExecutorService pool1 = Executors.newFixedThreadPool(THREADS_NUM);
		List<Callable<Object>> tasks1 = new ArrayList<>();
		try {
			for (String[] element : arrays1) {
				tasks1.add(new Callable<Object>() { // adds tasks
					public Object call() throws Exception {
						RadixSort.radixSort(element);
						return element;
					}
				});
			}
			List<Future<Object>> invokeAll1 = pool1.invokeAll(tasks1); // runs
																		// pool
																		// of
																		// threads
																		// and
																		// WAIT
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			pool1.shutdown(); // the end
		}

		// merging
		String mergeSortResult[] = ArraysMerger.mergeMultithread(arrays1);

		en = System.nanoTime();

		// writing to file
		try (FileWriter writer = new FileWriter("RadixSortResult.txt", false)) {
			for (String element : mergeSortResult) {
				writer.write(element);
				writer.append('\n');
			}
			writer.write("Sorted in: " + ((en - st) / 1000000000) + " seconds");
			writer.append('\n');
			writer.write("By: " + THREADS_NUM + " threads");
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		System.out.println("Radix Sort (" + THREADS_NUM
				+ " Thread(s)) finished in: " + ((en - st) / 1000000000)
				+ " seconds");

	}

}
