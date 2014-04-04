package LockFreeDataStructure.LinkedListTester;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import LockFreeDataStructure.LockFreeLinkedList;

public class PerformanceTester {
	public final static int N = 10;

	final static LockFreeLinkedList<Data> lfList = new LockFreeLinkedList<Data>(
			new Data(Integer.MIN_VALUE), new Data(Integer.MAX_VALUE));
	final static ConcurrentLinkedQueue<Data> cSkipList = new ConcurrentLinkedQueue<Data>();

	public static void main(String[] args) {
		Thread t[] = new Thread[N];
		final Random rand = new Random();
		for (int i = 0; i < N; i++) {
			t[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						Data data = new Data (rand.nextInt(1000));
						lfList.insert(data);
						cSkipList.add(data);
					}
				}
			});
		}
		for (int i = 0; i < N; i++)
			t[i].start();
		for (int i = 0; i < N; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Data a[] = new Data[2];
		Data tmp[] = cSkipList.toArray(a);
		Arrays.sort(tmp);
		System.out.println(Arrays.toString(tmp));
		System.out.println(lfList);	
		cSkipList.clear();
		lfList.clean();

	}
}
