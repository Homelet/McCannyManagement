package mccanny.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * an ordered Unique Array is an linklist implemented array which aimed at provide a high speed at adding data to an sorted array with no duplicate item
 */
public class OrderedUniqueArray<E extends Comparable<E>> implements Iterable<E>{
	
	private Node start;
	private int  size;
	private int  modCount = 0;
	
	public OrderedUniqueArray(){
		size = 0;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder("[");
		for(Node node = start; node != null; node = node.next){
			builder.append(node.item).append(",\n");
		}
		return builder.append("]").toString();
	}
	
	public void add(E item){
		if(start == null){
			start = new Node(item, null);
		}else{
			for(Node node = start, previous = null; ; previous = node, node = node.next){
				int result = node.item.compareTo(item);
				// find the first item which is greater to the item then insert the node
				// if find one which is equals return
				if(result >= 0){
					if(result == 0){
						// if the same item then return
						if(node.item.equals(item))
							return;
					}
					// if no previous means this is the first one
					if(previous == null){
						this.start = new Node(item, node);
					}else{
						previous.next = new Node(item, node);
					}
					break;
				}else{
					// if no next means this is the last one
					if(node.next == null){
						node.next = new Node(item, null);
						break;
					}
				}
			}
		}
		size++;
		modCount++;
	}
	
	public void remove(E item){
		if(start == null)
			return;
		for(Node node = start, previous = null; node != null; previous = node, node = node.next){
			if(node.item.equals(item)){
				if(previous == null){
					start = node.next;
				}else{
					previous.next = node.next;
				}
				size--;
				modCount++;
				// since we designed the data structure with only unique item in there we can safely break the loop
				break;
			}
		}
	}
	
	public void removeIf(Predicate<? super E> filter){
		if(start == null)
			return;
		for(Node node = start, previous = null; node != null; previous = node, node = node.next){
			if(filter.test(node.item)){
				if(previous == null){
					start = node.next;
				}else{
					previous.next = node.next;
				}
				size--;
				modCount++;
			}
		}
	}
	
	public void clear(){
		// Clearing all of the links between nodes is "unnecessary", but:
		// - helps a generational GC if the discarded nodes inhabit
		//   more than one generation
		// - is sure to free memory even if there is a reachable Iterator
		for(Node x = start; x != null; ){
			Node next = x.next;
			x.item = null;
			x.next = null;
			x = next;
		}
		start = null;
		size = 0;
		modCount++;
	}
	
	public boolean has(E item){
		for(Node node = start; node != null; node = node.next){
			if(node.item.equals(item)){
				return true;
			}
		}
		return false;
	}
	
	public int size(){
		return size;
	}
	
	public boolean isEmpty(){
		return size == 0;
	}
	
	@Override
	public Iterator<E> iterator(){
		return new Itr();
	}
	
	private class Itr implements Iterator<E>{
		
		Node current;
		int  expectedModCount;
		
		Itr(){
			this.current = start;
			this.expectedModCount = modCount;
		}
		
		@Override
		public boolean hasNext(){
			checkForConcurrentModification();
			return current != null;
		}
		
		@Override
		public E next(){
			checkForConcurrentModification();
			E t = current.item;
			current = current.next;
			return t;
		}
		
		private void checkForConcurrentModification(){
			if(modCount != expectedModCount)
				throw new ConcurrentModificationException("The List has been modified Concurrently!");
		}
	}
	
	private class Node{
		
		E    item;
		Node next;
		
		Node(E item, Node next){
			this.item = item;
			this.next = next;
		}
	}
}