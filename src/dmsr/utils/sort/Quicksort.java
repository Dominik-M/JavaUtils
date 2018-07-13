/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dmsr.utils.sort;

/**
 *
 * @author Dominik
 * @param <E> Datatype of the elements
 */
public class Quicksort<E extends Object> extends SortingAlgorithm<E> {

    public Quicksort(Comparator<E> c) {
        super(c);
    }

    @Override
    public E[] sort() {
        quicksort(0, size() - 1);
        return getValues();
    }

    private void quicksort(int left, int right) {
        if (left < right) {
            int pivot = divide(left, right);
            quicksort(left, pivot - 1);
            quicksort(pivot + 1, right);
        }
    }

    private int divide(int left, int right) {
        int i = left;
        int j = right - 1;
        E pivot = get(right);
        do {
            while (!compare(get(i), pivot) && i < right) {
                i++;
            }
            while (!compare(pivot, get(j)) && j > left) {
                j--;
            }
            if (i < j) {
                switchValues(i, j);
            }
        } while (i < j);
        if (compare(get(i), pivot)) {
            switchValues(i, right);
        }
        return i;
    }

    @Override
    public String toString() {
        return "Quicksort";
    }
}
