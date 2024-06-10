import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Comparator<Integer> integerComparator = Integer::compare;

        BTree<Integer> bTree = new BTree<>(integerComparator, 2);
        Random random = new Random();
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
//        for(int i = 0; i < 100; i ++){
//            Integer x = random.nextInt(0,100);
//            bTree.insert(x);
//            System.out.println(bTree.search(x));
//        }
        bTree.insert(5);
        bTree.insert(2);
        bTree.insert(3);
        bTree.insert(1);
        bTree.insert(8);
        bTree.insert(9);

        bTree.remove(4);
        System.out.println(bTree.search(9));







    }
}