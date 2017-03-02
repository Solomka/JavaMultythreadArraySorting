import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadsArraySort {
	

	protected static final int SIZE = 1000;												// MAX = 500000
	protected static final int THREADS = 10;												// N
	private static double st, en;

	public static void main(String[] args) throws IOException {
		/*
		final String[] input = { "mother", "father", "sister", "brother", "another", "eclipse" };
		System.out.println("Before Sorting - " + Arrays.toString(input));
		//BinaryInsertionSort.binaryInsertionSort(input);
		RadixSort.radixSort(input);
		System.out.println("After Sorting - " + Arrays.toString(input));
		*/
		
		String [] data = DataReader.readFile("500.000.txt", SIZE);
		
		/*
		 *  data for Binary Insertion Sort
		 */
		ArrayList <String[]> arrays= new ArrayList<String[]>(THREADS);
		int residue = SIZE%THREADS;
		int arrSize = (SIZE-residue)/THREADS;
		int counter = 0;										// general counter, in the end must = SIZE.
		
		// splitting to N arrays
		for(int i=0; i<THREADS; i++){
			String [] temp;
			if(residue>0){ 
				temp = new String [arrSize+1];
			}else{
				temp = new String [arrSize];
			}
			for(int j=0; j<arrSize; j++){
				temp[j]=data[counter];
				counter++;
			}
			if(residue>0){
				temp[temp.length-1]=data[counter];
				counter++;
				residue--;
			}
			arrays.add(temp);
		}
		
		// data for Radix Sort
		ArrayList<String[]> arrays1 = new ArrayList<String[]>(arrays);					// fine only for IMMUTABLE objects
		
		/*
		 *  runs BInaryInsertionSort in N threads and waits until all done
		 */
		st = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();
        try {
            for (String[] element:arrays) {
                tasks.add(new Callable<Object>() {								// adds tasks
                    public Object call() throws Exception {
                    	BinaryInsertionSort.binaryInsertionSort(element);
						return element;
                    }
                });
            }
            List<Future<Object>> invokeAll = pool.invokeAll(tasks);				// runs pool of threads and WAIT
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();													// the end
        }
        
        // merging
        String insertionSortResult [] = ArraysMerger.mergeMultithread(arrays);
        
        en = System.nanoTime();
        
        // writing to file
        try(FileWriter writer = new FileWriter("BinaryInsertionSortResult.txt", false)){
        	for(String element: insertionSortResult){
        		writer.write(element);
        		writer.append('\n');
        	}
        	writer.write("Sorted in: "+((en-st)/1000000000)+" seconds"); writer.append('\n');
        	writer.write("By: "+THREADS+" threads");
        	writer.flush();
        }catch(IOException ex){
        	System.out.println(ex.getMessage());
        }
        System.out.println("Binary Insertion Sort ("+THREADS+" Thread(s)) finished in: "+((en-st)/1000000000)+" seconds");

        st=0;
        en=0;
        
    	/*
    	 * runs RadixSort in N threads and waits until all done
    	 */
 		st = System.nanoTime();
 		ExecutorService pool1 = Executors.newFixedThreadPool(THREADS);
         List<Callable<Object>> tasks1 = new ArrayList<>();
         try {
             for (String[] element:arrays1) {
                 tasks1.add(new Callable<Object>() {								// adds tasks
                     public Object call() throws Exception {
                     	RadixSort.radixSort(element);
 						return element;
                     }
                 });
             }
             List<Future<Object>> invokeAll1 = pool1.invokeAll(tasks1);				// runs pool of threads and WAIT
         } catch (InterruptedException e) {
             e.printStackTrace();
         } finally {
             pool1.shutdown();													// the end
         }
         
         // merging
         String mergeSortResult [] = ArraysMerger.mergeMultithread(arrays1);
         
         en = System.nanoTime();
         
         // writing to file
         try(FileWriter writer = new FileWriter("RadixSortResult.txt", false)){
         	for(String element: mergeSortResult){
         		writer.write(element);
         		writer.append('\n');
         	}
         	writer.write("Sorted in: "+((en-st)/1000000000)+" seconds"); writer.append('\n');
         	writer.write("By: "+THREADS+" threads");
         	writer.flush();
         }catch(IOException ex){
         	System.out.println(ex.getMessage());
         }
        
        System.out.println("Radix Sort ("+THREADS+" Thread(s)) finished in: "+((en-st)/1000000000)+" seconds");

	}

}
