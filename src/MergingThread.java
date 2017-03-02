/**
 * Thread for process merging
 * 
 * @author Solomka
 *
 */
public class MergingThread extends Thread {
	private String[] data1;
	private String[] data2;

	public MergingThread(String[] data1, String[] data2) {
		this.data1 = data1;
		this.data2 = data2;
	}

	@Override
	public void run() {
		String[] result = ArraysMerger.merge(data1, data2);
		ArraysMerger.writeResult(result);
	}
}
