import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class BTree <T>{
    class Node {
        T[] valueList;
        Node[] childrenList;
        int valueNo;
        int childrenNo;
        boolean isLeaf;
        int t;
        Node parentNode;
        @SuppressWarnings("unchecked")
        public Node(int t) {
            this.isLeaf = true;
            this.valueList = (T[]) new Object[2 * t - 1];
            this.childrenList = (Node[]) Array.newInstance(Node.class, 2 * t);
            this.childrenNo = 0;
            this.valueNo = 0;
        }




        void divide(){
            T middleElement = valueList[2 * t - 1];

        }

        void insert(){

        }

        int[] binarySearch(T key) {
            int leftIndex = 0;
            int rightIndex = valueNo - 1;

            while (leftIndex <= rightIndex) {
                int middleIndex = leftIndex + ((rightIndex - leftIndex) / 2);
                int cmp = comparator.compare(valueList[middleIndex], key);
                if (cmp < 0) {
                    leftIndex = middleIndex + 1;
                } else if (cmp > 0) {
                    rightIndex = middleIndex - 1;
                } else {
                    return new int[]{middleIndex, 1};
                }
            }
            return new int[]{leftIndex, 0};
        }

        T search(T key){
            int[] result = binarySearch(key);
            if (result[1] == 1) {
                return key;
            }
            if (isLeaf){
                throw new NoSuchElementException();
            }
            for (int i = result[0] - 1; i < valueNo; i++){
                if (comparator.compare(key,valueList[i])>0){
                    return childrenList[i].search(key);
                }
            return childrenList[i+1].search(key);
            }
            return null;
        }


        void add(){

        }


        boolean contains(T key) {
            return binarySearch(key)[1] == 1;
        }

    }
    int t;
    private Comparator<T> comparator;
    Node root;

    public BTree(Comparator<T> comparator, int t) {
        this.comparator = comparator;
        this.t = t;
        this.root = new Node(t);
    }

    public T search(T key){
        return root.search(key);
    }

    public void add(){

    }

}
