package dmsr.utils.sort;

/**
 * SelectionSort ist die einfachste und zuverlässigste Sortiermethode, denn es
 * benötigt immer (N²-N)/2 Vergleiche und N-1 Austauschoperationen.
 *
 * @author Dundun
 * @param <E> Datatype for the Elements
 */
public class SelectionSort<E extends Object> extends SortingAlgorithm<E> {

    public SelectionSort(Comparator<E> c) {
        super(c);
    }

    @Override
    public E[] sort() {
        for (int i = 0; i < size() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < size(); j++) {
                if (compare(min, j)) {
                    min = j;
                }
            }
            if (min != i) {
                switchValues(min, i);
            }
        }
        return getValues();
    }

    @Override
    public String toString() {
        return "SelectionSort";
    }
}
