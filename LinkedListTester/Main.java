package LockFreeDataStructure.LinkedListTester;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import LockFreeDataStructure.LockFreeLinkedList;
public class Main {
	public final static int N = 10; 

	final static LockFreeLinkedList<Integer> lfList = new LockFreeLinkedList<Integer>(Integer.MIN_VALUE,Integer.MAX_VALUE);
	final static ConcurrentLinkedQueue<Integer> cSkipList = new ConcurrentLinkedQueue<Integer>();
	public static void main(String[] args) {
	while(true){
		Thread t[] = new Thread[N];
		final Random rand = new Random();
		for(int i=0; i<N; i++ ){
			t[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i=0;i<10;i++){
						int data = rand.nextInt(1000);
						lfList.insert(data);
						cSkipList.add(data);
					}
				}
			});
		}
		for(int i=0; i<N; i++ )
			t[i].start();
		for(int i=0; i<N; i++ ){
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Integer a[] = new Integer[2];
		Integer tmp [] =cSkipList.toArray(a);
		Arrays.sort(tmp);
		System.out.println(Arrays.toString(tmp));
		System.out.println(lfList);
		if(!Arrays.toString(tmp).equals(lfList.toString()))
			break;
		cSkipList.clear();
		lfList.clean();
	}
	}
}
