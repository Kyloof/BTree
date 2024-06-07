import java.util.ArrayList;
import java.util.LinkedList;

public class LimitedArrayList<T> extends ArrayList<T> {
    private final int size;
    private int currentCapacity = 0;

    public LimitedArrayList(int size) {
        this.size = size;
    }


    @Override
    public void add(int index, T t){
        if (t == null)
            throw new NullPointerException();
        if (currentCapacity < size) {
            currentCapacity++;
            super.add(index, t);
        }
    }

    @Override
    public void addLast(T t) {
        if (t == null)
            throw new NullPointerException();
        if (currentCapacity < size) {
            currentCapacity++;
            super.addLast(t);
        }
    }
    @Override
    public void addFirst(T t){
        if (t == null)
            throw new NullPointerException();
        if (currentCapacity<size) {
            currentCapacity++;
            super.addFirst(t);
        }
    }

    @Override
    public boolean remove(Object object){
        boolean t = super.remove(object);
        currentCapacity--;
        return t;
    }

    @Override
    public T removeFirst(){
        T t = super.removeFirst();
        currentCapacity--;
        return t;
    }

    @Override
    public T removeLast(){
        T t = super.removeLast();
        currentCapacity--;
        return t;
    }

}
