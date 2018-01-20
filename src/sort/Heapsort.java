package sort;

public class Heapsort<E extends Object> extends SortingAlgorithm<E> {

    public Heapsort(Comparator<E> c) {
        super(c);
    }

    @Override
    public E[] sort() {
        constructHeap();
        for (int i = size() - 1; i >= 0; i--) {
            switchValues(0, i);
            seep(0, i);
        }
        return getValues();
    }

    private void constructHeap() {
        for (int i = size() / 2 - 1; i >= 0; i--) {
            seep(i, size());
        }
    }

    private void seep(int pos, int n) {
        int j;
        E h = get(pos);
        while (pos < n / 2) {
            j = pos * 2 + 1;
            if (j + 1 < n && compare(j + 1, j)) {
                j++;
            }
            if (!compare(get(j), h)) {
                break;
            }
            set(pos, get(j));
            pos = j;
        }
        set(pos, h);
    }

    @Override
    public String toString() {
        return "Heapsort";
    }
}
