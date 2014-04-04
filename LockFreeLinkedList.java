package LockFreeDataStructure;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * LockFreeLinkedList is implementation of Link List based set
 *  Reference:
 *  
 *  M.M. Michael, “High Performance Dynamic Lock-Free Hash
 *	Tables and List-Based Sets,” Proc. 14th Ann. ACM Symp. Parallel
 *	Algorithms and Architectures, pp. 73-82, Aug. 2002.
 *
 * @author Jigar Kaneriya
 *
 * @param <T> Type
 */

public class LockFreeLinkedList<T> {
	Node<T> tail, head;

	public LockFreeLinkedList(T low, T high) {
		tail = new Node<T>(high, null, false);
		head = new Node<T>(low, tail, false);
	}

	// AtomicMarkableReference<Node<T>> _cur=new
	// AtomicMarkableReference<Node<T>>(new Node<T>(null, null, false),false);
	// AtomicMarkableReference<Node<T>> _prev=new
	// AtomicMarkableReference<Node<T>>(head, false);
	// AtomicMarkableReference<Node<T>> _next=new
	// AtomicMarkableReference<Node<T>>(new Node<T>(null, null, false),false);
	//

	/*
	 * boolean find(T data){ T curData; boolean nextMark[] = new boolean[1];
	 * Node<T> _cur,_prev,_next; _prev=head; _cur=_prev.next.getReference();
	 * while(true){ if(_cur==null){ return false; }
	 * _next=_cur.next.get(nextMark); curData=_cur.data; // boolean[] stamp =
	 * new boolean [1]; // Node<T> tmpNode = _prev.get(stamp); // if(stamp[0]
	 * !=false || tmpNode.next != _cur.getReference() ){ //
	 * _prev.set(head,head.mark); //
	 * _cur.set(_prev.getReference(),_prev.getReference().mark); // continue; //
	 * } if( nextMark[0] == false ){ if( ((Comparable<T>)
	 * curData).compareTo(data) > 0 ){ return ((Comparable<T>)
	 * curData).compareTo(data) == 0; } _prev=_cur;
	 * 
	 * }else{
	 * if(_prev.compareAndSet(_cur.getReference(),_next.getReference(),false
	 * ,false)){
	 * 
	 * //delete node }else{ _prev.set(head,head.mark);
	 * _cur.set(_prev.getReference(),_prev.getReference().mark); continue; } }
	 * _cur.set(_next.getReference(),_next.isMarked()); } }
	 */
	
	
	private Pair<T> find(Node<T> head, T key) {
		Node<T> pred = null, curr = null, succ = null;
		boolean[] marked = { false };
		retry: while (true) {
			pred = head;
			curr = pred.next.getReference();
			while (true) {
				succ = curr.next.get(marked);
				while (marked[0]) {
					if (!pred.next.compareAndSet(curr, succ, false, false))
						continue retry;
					curr = succ;
					succ = curr.next.get(marked);
				}
				if (((Comparable<T>) curr.key).compareTo(key) > 0) {
					return new Pair<T>(pred, curr);
				}
				pred = curr;
				curr = succ;
			}
		}
	}

	/**
	 * Inserts new Data item in to link list if not already exists
	 * @param data any comparable object
	 * @return True if successful insertion False if already exists
	 */
	public boolean insert(T key) {
		Node<T> newNode = new Node<T>(key, null, false);
		while (true) {
			Pair<T> pair = find(head, key);
			Node<T> pred = pair.pred, curr = pair.curr;
			if (curr.key == key) {
				return false;
			} else {
				newNode.next = new AtomicMarkableReference<Node<T>>(curr, false);
				if (pred.next.compareAndSet(curr, newNode, false, false)) {
					return true;
				}
			}
		}
	}

	/**
	 * Removes node if exists
	 * @param data any comparable object
	 * @return True if successful removal False if data does not exists in linked list
	 */
	public boolean remove(T key) {
		while (true) {
			Pair<T> pair = find(head, key);
			Node<T> pred = pair.pred, curr = pair.curr;
			if (curr.key != key) {
				return false;
			} else {
				Node<T> succ = curr.next.getReference();
				if (!curr.next.attemptMark(succ, true))
					continue;
				pred.next.compareAndSet(curr, succ, false, false);
				return true;
			}

		}
	}

	/**
	 * Checks weather data is present in linked list or not
	 * @param data any comparable object
	 * @return True if present False otherwise
	 */
	public boolean contains(T key) {
		boolean[] marked = { false };
		Node<T> curr = head;
		while (((Comparable<T>) curr.key).compareTo(key) < 0) {
			curr = curr.next.getReference();
			curr.next.get(marked);
		}
		return (curr.key == key && !marked[0]);
	}
	
	/**
	 * Remove all nodes of linked list.
	 */
	public void clean(){
		head.next.set(tail, false);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		Node<T> node = head.next.getReference();
		while (node != tail) {
			builder.append(node.toString());
			node = node.next.getReference();
		}
		return builder.toString().substring(0, builder.length() - 2) + "]";
	}

	private class Pair<T> {
		public Node<T> pred, curr;
		Pair(Node<T> myPred, Node<T> myCurr) {
			pred = myPred;
			curr = myCurr;
		}
	}
}
