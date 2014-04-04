package LockFreeDataStructure;
import java.util.concurrent.atomic.AtomicMarkableReference;


class Node<T> {

	T key;
	AtomicMarkableReference<Node<T>> next;
	
	public Node(T _data,Node<T> _next,boolean _mark) {
		key=_data;
		next = new AtomicMarkableReference<Node<T>>(_next, false);
	}
	
	public Node(T _data){
		key = _data;
		next = new AtomicMarkableReference<Node<T>>(null, false);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(key+", ");
		return builder.toString();
	}
	
//	void setValues(AtomicMarkableReference<Node<T>> node){
//		data=node.data;
//		next=node.next;
//		mark=node.mark;
//	}
	

}
