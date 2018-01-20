package sort;

public class InsertionSort<E extends Object> extends SortingAlgorithm<E> {

    public InsertionSort(Comparator<E> c) {
        super(c);
    }

    @Override
    public E[] sort() {
        int j;
        for (int i = 1; i < size(); i++) {
            j = i;
            while (j > 0 && compare(j - 1, i)) {
                switchValues(j, --j);
            }
            set(j, get(i));
        }
        return getValues();
    }

    @Override
    public String toString() {
        return "Insertionsort";
    }
}
