import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class BTree<T> {
    class Node {
        ArrayList<T> valueList;
        ArrayList<Node> childrenList;
        int valueNo;
        boolean isLeaf;
        int t;

        public Node(int t) {
            this.isLeaf = true;
            this.valueList = new ArrayList<>(2 * t - 1);
            this.childrenList = new ArrayList<>(2 * t);
            this.valueNo = 0;
            this.t = t;
        }

        void addChild(Node child) {
            childrenList.add(child);
        }

        T getFirstElement() {
            return valueList.get(0);
        }

        void merge(int index) {
            Node child = childrenList.get(index);
            Node sibling = childrenList.get(index + 1);

            // Pull the key from the current node and insert it into the child
            child.valueList.add(t - 1, valueList.get(index));

            // Move the keys and children from sibling to child
            for (int i = 0; i < sibling.valueNo; i++) {
                child.valueList.add(sibling.valueList.get(i));
            }
            if (!child.isLeaf) {
                for (int i = 0; i <= sibling.valueNo; i++) {
                    child.childrenList.add(sibling.childrenList.get(i));
                }
            }

            // Move the keys and children in the current node
            for (int i = index + 1; i < valueNo; i++) {
                valueList.set(i - 1, valueList.get(i));
            }
            for (int i = index + 2; i <= valueNo; i++) {
                childrenList.set(i - 1, childrenList.get(i));
            }

            // Update the child count and key count
            child.valueNo += sibling.valueNo + 1;
            valueNo--;

            // Remove the sibling
            childrenList.remove(index + 1);
        }

        void rotateFromLeft(int index) {
            Node child = childrenList.get(index);
            Node sibling = childrenList.get(index - 1);

            // Move the key from the parent to child
            child.valueList.add(0, valueList.get(index - 1));

            // Move the largest key from the sibling to the parent
            valueList.add(index - 1, sibling.valueList.remove(sibling.valueNo - 1));

            // Move the last child from the sibling to the child
            if (!child.isLeaf) {
                child.childrenList.add(0, sibling.childrenList.remove(sibling.valueNo));
            }

            // Update the value numbers
            child.valueNo += 1;
            sibling.valueNo -= 1;
        }

        void rotateFromRight(int index) {
            Node child = childrenList.get(index);
            Node sibling = childrenList.get(index + 1);

            // Move the key from the parent to child
            child.valueList.add(0, valueList.get(index + 1));

            // Move the largest key from the sibling to the parent
            valueList.add(index + 1, sibling.valueList.remove(sibling.valueNo - 1));

            // Move the last child from the sibling to the child
            if (!child.isLeaf) {
                child.childrenList.add(0, sibling.childrenList.remove(sibling.valueNo));
            }

            // Update the value numbers
            child.valueNo += 1;
            sibling.valueNo -= 1;
        }

        private T remove(T value) {
            int index = 0;
            while(index < valueNo && comparator.compare(value, valueList.get(index)) > 0) {
                index++;
            }

            // value is in the node
            if(index < valueNo && comparator.compare(value, valueList.get(index)) == 0) {


                // if its a leaf remove from the node
                if(isLeaf) {
                    T val = valueList.remove(index);
                    valueNo--;
                    return val;
                }

                //if the node is internal node we must get value from its child
                else {
                    valueList.set(index, childrenList.get(index).removePredecessor(null));
                    // if the child became minimal as a result of a swap - fix nodes
                    if(childrenList.get(index).valueNo < t - 1) {
                        this.fixNodesChild(index);
                    }
                }
            }

            // not found
            else if(isLeaf) {
                return null;
            }

            // value is in one of the node's children
            else {
                value = childrenList.get(index).remove(value);
            }

            // fix nodes of children
            if(childrenList.get(index).valueNo < t - 1) {
                // if not, we call fixNodesChild on the parent of the child and pass child's index
                this.fixNodesChild(index);
            }

            return value;
        }


        private T removePredecessor(Node parent) {
            T value;

            if(isLeaf) {
                value = valueList.remove(valueNo-1);
                valueNo--;
            }

            else {
                value = childrenList.getLast().removePredecessor(this);
            }

            if(parent != null && childrenList.size() < t - 1) {
                parent.fixNodesChild(parent.childrenList.size() - 1);
            }

            return value;
        }

        private void fixNodesChild(int index) {
            Node child = childrenList.get(index);
            Node leftSibling = index > 0 ? childrenList.get(index - 1) : null;
            Node rightSibling = index < valueNo ? childrenList.get(index + 1) : null;
            //1. Jeśli lewy brat nie jest minimalny, to wykonywana jest
            //rotacja z lewego brata. Koniec.
            if (leftSibling != null && leftSibling.valueNo >= t) {
                rotateFromLeft(index);
                //2. Jeśli prawy brat nie jest minimalny, to wykonywana jest
                //rotacja z prawego brata. Koniec.
            } else if (rightSibling != null && rightSibling.valueNo >= t) {
                rotateFromRight(index);
                //3. Wpp, węzeł jest łączony z lewym lub prawym bratem, co
                //może sprawić, że rodzic będzie miał mniej niż minimalną
                //liczbę kluczy
            } else {
                merge(index);
            }
        }

        void splitChild(int index, Node child) {
            int middleIndex = t - 1;
            Node newNode = new Node(child.t);
            newNode.isLeaf = child.isLeaf;
            newNode.valueNo = t - 1;

            for (int i = 0; i < t - 1; i++) {
                newNode.valueList.add(child.valueList.get(i + t));
            }

            if (t > 1) {
                child.valueList.subList(t, 2 * t - 1).clear();
            }

            if (!child.isLeaf) {
                for (int i = 0; i < t; i++) {
                    newNode.childrenList.add(child.childrenList.get(i + t));
                }
                for (int i = 0; i < t; i++) {
                    child.childrenList.remove(t);
                }
            }

            child.valueNo = t - 1;

            childrenList.add(index + 1, newNode);

            valueList.add(index, child.valueList.get(middleIndex));

            child.valueList.remove(middleIndex);

            valueNo++;
        }


        void insertNonFull(T value) {
            int i = valueNo - 1;
            if (isLeaf) {
                while (i >= 0 && comparator.compare(value, valueList.get(i)) < 0) {
                    i--;
                }
                valueList.add(i + 1, value);
                valueNo++;
            } else {
                while (i >= 0 && comparator.compare(value, valueList.get(i)) < 0) {
                    i--;
                }
                i++;
                if (childrenList.get(i).valueNo == 2 * t - 1) {
                    splitChild(i, childrenList.get(i));
                    if (comparator.compare(value, valueList.get(i)) > 0) {
                        i++;
                    }
                }
                childrenList.get(i).insertNonFull(value);
            }
        }

        int[] binarySearch(T key) {
            int leftIndex = 0;
            int rightIndex = valueNo - 1;

            while (leftIndex <= rightIndex) {
                int middleIndex = leftIndex + ((rightIndex - leftIndex) / 2);
                int cmp = comparator.compare(valueList.get(middleIndex), key);
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

        T search(T key) {
            int[] result = binarySearch(key);
            int searchIndex = result[0];
            int isFound = result[1];

            if (isFound == 1) {
                return valueList.get(searchIndex);
            }

            if (isLeaf) {
                throw new NoSuchElementException();
            }

            return childrenList.get(searchIndex).search(key);
        }

        Node searchNode(T key) {
            int[] result = binarySearch(key);
            int searchIndex = result[0];
            int isFound = result[1];

            if (isFound == 1) {
                return this;
            }
            if (isLeaf) {
                return this;
            }
            return childrenList.get(searchIndex).searchNode(key);
        }
    }

    int t;
    private final Comparator<T> comparator;
    Node root;

    public BTree(Comparator<T> comparator, int t) {
        this.comparator = comparator;
        this.t = t;
    }

    public T search(T key) {
        if (root == null) {
            throw new NoSuchElementException();
        }
        return root.search(key);
    }

    private Node searchNode(T key) {
        if (root == null) {
            return null;
        }
        return root.searchNode(key);
    }

    public T remove(T value) {

        T result = root.remove(value);

        // if root is empty and there is a new root merged
        if (root.valueList.isEmpty() && !root.isLeaf) {
            root = root.childrenList.get(0);
        }
        return result;
    }

    public void insert(T value) {
        if (root == null) {
            root = new Node(t);
            root.valueList.add(value);
            root.valueNo = 1;
        } else {
            if (root.valueNo == 2 * t - 1) {
                Node newNode = new Node(t);
                newNode.isLeaf = false;
                newNode.childrenList.add(root);
                newNode.splitChild(0, root);
                root = newNode;
                root.insertNonFull(value);
            } else {
                root.insertNonFull(value);
            }
        }
    }
}
