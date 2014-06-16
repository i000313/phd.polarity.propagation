
package dic.wordspolarity.graphbook;

/*************************************************************************
 *  Compilation:  javac Bag.java
 *  Execution:    java Bag < input.txt
 *
 *  A generic bag or multiset, implemented using a linked list.
 *
 *************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The <tt>Bag</tt> class represents a bag (or multiset) of 
 *  generic items. It supports insertion and iterating over the 
 *  items in arbitrary order.
 *  <p>
 *  The <em>add</em>, <em>isEmpty</em>, and <em>size</em>  operation 
 *  take constant time. Iteration takes time proportional to the number of items.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Bag<Item> implements Iterable<Item> {
    private int N;         // number of elements in bag
    private Node first;    // beginning of bag
    private Node last;     
    
    // helper linked list class
    private class Node {
        private Item item;
        private Node next;
    }

   /**
     * Create an empty stack.
     */
    public Bag() {
        first = null;
        last = null;
        N = 0;
    }

   /**
     * Is the BAG empty?
     */
    public boolean isEmpty() {
        return (first == null && last == null);
    }

   /**
     * Return the number of items in the bag.
     */
    public int size() {
        return N;
    }

   /**
     * Add the item to the bag.
     */
    public void add(Item item) {        
        Node oldlast = last;
        last = new Node();
        last.item = item;
        
        if(oldlast != null)
            oldlast.next = last;
            
        if(first == null) 
            first = last;
        
        N++;
    }


   /**
     * Return an iterator that iterates over the items in the bag.
     */
    public Iterator<Item> iterator()  {
        return new ListIterator();  
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }

}
