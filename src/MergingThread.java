/**
 * Thread for process merging
 * 
 * @author Solomka
 *
 */
public class MergingThread extends Thread {
	private String[] arr1;
	private String[] arr2;

	public MergingThread(String[] arr1, String[] arr2) {
		this.arr1 = arr1;
		this.arr2 = arr2;
	}

	/**
	 * merge 2 sorted arrays and into one sorted array
	 */
	@Override
	public void run() {
		String[] result = Merger.mergePairOfArrays(arr1, arr2);
		Merger.addToSortedList(result);
	}
}
