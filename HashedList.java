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
        int index = hash(item);

        if (size == 0) {
            first = new Node<>(item);
            buckets[index] = first;
            last = first;
        }

        else {
            Node<T> newNode = new Node<>(item);
            newNode.after = first;
            first.before = newNode;
            first = newNode;

            if (buckets[index] == null) {
                buckets[index] = newNode;
            } else {
                newNode.next = buckets[index];
                buckets[index] = newNode;
            }
        }

        size++;

        if (size() > maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }

    }

    public void addLast(T item) {

        int index = hash(item);

        if (size == 0) {
            last = new Node<>(item);
            buckets[index] = first;
            first = last;
        }

        else {
            Node<T> newNode = new Node<>(item);
            newNode.before = last;
            last.after = newNode;
            last = newNode;

            if (buckets[index] == null) {
                buckets[index] = newNode;
            } else {
                newNode.next = buckets[index];
                buckets[index] = newNode;
            }
        }

        size++;
        if (size() > maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }
    }

    public T removeFirst() {

        if (size == 0) {
            return null;
        }

        Node<T> toRemove = first;
        int index = hash(toRemove.data);

        first = toRemove.after;

        if (first != null) {
            first.before = null;
        }

        if (size == 1) {
            last = first;
        }

        if (buckets[index].equals(toRemove)) {
            buckets[index] = toRemove.next;
        } else {
            Node<T> cur = buckets[index];
            while (!cur.next.equals(toRemove)) {
                cur = cur.next;
            }
            cur.next = toRemove.next;
        }
        size--;

        if (size() < (maxLoadFactor * capacity()) / 4) {
            resize(capacity() / 2);
        }

        return toRemove.data;
    }

    public T removeLast() {

        if (size == 0) {
            return null;
        }

        Node<T> toRemove = last;
        last = toRemove.before;

        int index = hash(toRemove.data);

        if (last != null) {
            last.after = null;
        }

        if (size == 1) {
            first = last;
        }

        if (buckets[index] == toRemove) {
            buckets[index] = buckets[index].next;
        } else {
            Node<T> cur = buckets[index];
            while (!cur.next.equals(toRemove)) {
                cur = cur.next;
            }
            cur.next = toRemove.next;
        }
        size--;

        if (size() < (maxLoadFactor * capacity()) / 4) {
            resize(capacity() / 2);
        }

        return toRemove.data;
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
            } else {

                buckets[index].before.after = buckets[index].after;
                buckets[index] = buckets[index].next;
                size--;

                if (size() < (maxLoadFactor * capacity()) / 4) {
                    resize(capacity() / 2);
                }
            }

            return true;

        } else {

            for (Node<T> curNode = buckets[index]; curNode != null; curNode = curNode.next) {

                if (curNode.next != null) {
                    if (curNode.next.data == item) {
                        if (curNode.next.next == null) {
                            curNode.next = null;
                        } else {
                            curNode.next = curNode.next.next;
                        }
                        size--;

                        if (size() < (maxLoadFactor * capacity()) / 4) {
                            resize(capacity() / 2);
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

}
