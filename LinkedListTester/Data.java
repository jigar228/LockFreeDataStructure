package LockFreeDataStructure.LinkedListTester;

public class Data implements Comparable<Data>{
	int data;
	Data(int i){
		data=i;
	}
	@Override
	public int compareTo(Data o) {
		return data - o.data;
	}
	@Override
	public String toString() {
		return data+"";
	}
}
