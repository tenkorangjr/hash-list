import java.util.Iterator;

@SuppressWarnings("unchecked")
public class HashedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        /**
         * next is the next Node in the bucket,
         * before is the previous Node to have been inserted
         * after is the next Node to have been inserted
         */
        Node<T> next, before, after;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node<T> first, last;
    private int size;
    private Node<T>[] buckets;
    private double maxLoadFactor;

    public HashedList() {
        buckets = (Node<T>[]) new Node[16];
        maxLoadFactor = .75;
        size = 0;
        first = null;
        last = null;
    }

    private int capacity() {
        return buckets.length;
    }

    private int hash(T item) {
        return Math.abs(item.hashCode() % capacity());
    }

    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> cur = first;

            public boolean hasNext() {
                return cur != null;
            }

            public T next() {
                T toReturn = cur.data;
                cur = cur.next;
                return toReturn;
            }
        };
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("[");
        for (Node<T> curNode = first; curNode != null; curNode = curNode.after) {
            out.append(curNode.data);
            if (curNode != last) {
                out.append(", ");
            } else {
                out.append("]");
            }
        }
        return out.toString();
    }

    public T getFirst() {
        return first.data;
    }

    public T getLast() {
        return last.data;
    }

    public void addFirst(T item) {
        if (size == 0) {
            first = new Node<>(item);
            last = first;
            int index = hash(item);

            buckets[index] = first;
        }

        else {
            Node<T> newNode = new Node<>(item);
            first.before = newNode;
            newNode.after = first;
            first = newNode;

            if (size == 1) {
                last = first.after;
                last.before = first;
                last.after = null;
            }

            int index = hash(item);

            if (buckets[index] == null) {
                buckets[index] = newNode;
            } else {
                newNode.next = buckets[index];
                buckets[index] = newNode;
            }
        }

        size++;

        if (size() > maxLoadFactor * capacity()) {
            resize(capacity() / 2);
        }

    }

    public void addLast(T item) {

        if (size == 0) {
            last = new Node<>(item);
            first = last;
            int index = hash(item);

            buckets[index] = first;
        }

        else {
            Node<T> newNode = new Node<>(item);
            last.after = newNode;
            newNode.before = last;
            last = newNode;

            int index = hash(item);

            if (buckets[index] == null) {
                buckets[index] = newNode;
            } else {
                newNode.next = buckets[index];
                buckets[index] = newNode;
            }
        }

        size++;
        if (size() > maxLoadFactor * capacity()) {
            resize(capacity() / 2);
        }
    }

    public T removeFirst() {

        if (size == 0) {
            return null;
        }

        Node<T> nodeToRemove = first;
        first = nodeToRemove.after;
        if (first != null) {
            first.before = null;
        }

        if (size == 1) {
            last = first;
        }

        int index = hash(nodeToRemove.data);
        if (buckets[index] == nodeToRemove) {
            buckets[index] = nodeToRemove.next;
        } else {
            Node<T> cur = buckets[index];
            while (cur.next != nodeToRemove) {
                cur = cur.next;
            }
            cur.next = nodeToRemove.next;
        }
        size--;
        if (size() < maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }

        return nodeToRemove.data;
    }

    public T removeLast() {

        if (size == 0) {
            return null;
        }

        Node<T> nodeToRemove = last;
        last = nodeToRemove.before;
        last.after = null;

        if (size == 1) {
            first = last;
        }

        int index = hash(nodeToRemove.data);

        if (buckets[index] == nodeToRemove) {
            buckets[index] = buckets[index].next;
        } else {
            Node<T> cur = buckets[index];
            while (cur.next != nodeToRemove) {
                cur = cur.next;
            }
            cur.next = nodeToRemove.next;
        }
        size--;
        if (size() < maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }

        return nodeToRemove.data;
    }

    public boolean contains(T item) {

        int index = hash(item);

        for (Node<T> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
            if (curNode.data == item) {
                return true;
            }
        }

        return false;

    }

    public boolean remove(T item) {

        int index = hash(item);

        if (buckets[index].data == item) {

            if (buckets[index] == first) {
                removeFirst();
            } else if (buckets[index] == last) {
                removeLast();
            }

            else if (buckets[index] != first && buckets[index] != last) {

                buckets[index].before.after = buckets[index].after;
                buckets[index] = buckets[index].next;
                size--;

                if (size() < maxLoadFactor * capacity()) {
                    resize(capacity() * 2);
                }
            }

            return true;

        } else {

            for (Node<T> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.next != null) {

                    if (curNode.next.data == item) {

                        if (curNode.next.next == null) {
                            curNode.next = null;
                        }

                        else {
                            curNode.next = curNode.next.next;
                        }

                        size--;

                        if (size() < maxLoadFactor * capacity()) {
                            resize(capacity() * 2);
                        }

                        return true;

                    }
                }
            }
        }

        return false;

    }

    private void resize(int newCapacity) {
        Node<T>[] newBuckets = (Node<T>[]) new Node[newCapacity];
        for (Node<T> curNode = first; curNode != null; curNode = curNode.after) {
            int newBucket = Math.abs(curNode.data.hashCode() % newCapacity);
            curNode.next = newBuckets[newBucket];
            newBuckets[newBucket] = curNode;
        }
        buckets = newBuckets;
    }

    public static void main(String[] args) {
        HashedList<String> list = new HashedList<>();
        list.addFirst("apple");
        list.addFirst("banana");
        list.addLast("cherry");
        list.addLast("date");
        System.out.println(list.toString()); // output: [banana, apple, cherry, date]
        System.out.println(list.size()); // output: 4
        System.out.println(list.contains("banana")); // output: true
        System.out.println(list.contains("orange")); // output: false
        System.out.println(list.getFirst()); // output: banana
        System.out.println(list.getLast()); // output: date
        list.remove("banana");
        list.remove("cherry");
        System.out.println(list.toString()); // output: [apple, cherry]
        System.out.println(list.size()); // output: 2
    }

}
