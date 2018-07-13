package dmsr.utils.sort;

/**
 * BubbleSort benötigt im Durchschnitt und im ungünstigsten fall ungefähr N²/2
 * Vergleiche und Austauschoperationen. Diese Implementation besitzt zudem eine
 * flag, um festzustellen ob etwas geändert wurde. Das verkürzt die Laufzeit bei
 * "fast" sortierten Datensätzen auf annährend N. BubbleSort empfiehlt sich
 * daher für "unkomplizierte" (d.h. kleine, wenig vielfältige) Datensätze und
 * ist dabei sehr zuverlässig, aber auch relativ langsam, da der
 * durchschnittliche Aufwand gleich dem Aufwand im ungünstigsten Fall ist.
 *
 * @author Dominik Messerschmidt
 * @param <E> Datatype of the elements
 */
public class BubbleSort<E extends Object> extends SortingAlgorithm<E> {

    public BubbleSort(Comparator<E> c) {
        super(c);
    }

    @Override
    public E[] sort() {
        boolean flag = true; // die "flag" (nicht zwingend notwendig)
        for (int i = 0; i < size() - 1 && flag; i++) { //n-1 durchläufe
            flag = false;
            for (int j = 1; j < size() - i; j++) { // ((n-1)*(n-2))/2 durchläufe
                if (compare(j - 1, j)) {
                    switchValues(j - 1, j);
                    flag = true;
                }
            }
        }
        return getValues();
    }

    @Override
    public String toString() {
        return "Bubblesort";
    }
}
