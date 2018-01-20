/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

/**
 *
 * @author Dominik
 * @param <E> Type of elements to compare
 */
public interface Comparator<E extends Object> {

    public abstract boolean compare(E e1, E e2);
}
