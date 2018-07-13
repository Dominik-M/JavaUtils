package dmsr.utils.sort;

/**
 *
 * @author Dominik
 * @param <E> Data type for this container class
 */
public abstract class SortingAlgorithm<E extends Object>
{

    private E[] values;
    private int comparisons, permutations, readAccesses, writeAccesses;
    private Comparator comp;
    private final java.util.LinkedList<SortingListener> listener = new java.util.LinkedList();

    public SortingAlgorithm(Comparator<E> c)
    {
        resetCounters();
        comp = c;
    }

    /**
     * Sortiermethode für einen Array aus ganzen Zahlen. Muss für jeden
     * SortierAlgorithmus implementiert werden.
     *
     * @return der sortierte Array
     */
    public abstract E[] sort();

    /**
     * Füllt einen Array der Länge n mit Zufallszahlen von 0 bis faktor*n.
     *
     * @param dopplung Ob Zahlen mehrfach vorkommen dürfen
     * @param n Die Anzahl der Elemente
     * @param faktor multipiliziert mit n bestimmt das Maximum
     * @return ein Array mit Zufallszahlen
     */
    public static int[] fillIntArray(int n, double faktor, boolean dopplung)
    {
        int[] a = new int[n++];
        for (int i = 0; i < a.length; i++)
        {
            a[i] = (int) (Math.random() * faktor * n);
            if (!dopplung)
            {
                for (int j = 0; j < i; j++)
                {
                    if (a[j] == a[i])
                    {
                        i--;
                        break;
                    }
                }
            }
        }
        return a;
    }

    /**
     * Gibt die Anzahl der vorgenommenen Vergleiche dieser Instanz zurück. Nur
     * sinnvoll wenn sortiere() schon benutzt wurde. Die Zahl wird nach Aufruf
     * von sortiere() in der Regel NICHT zurückgesetzt.
     *
     * @return Anzahl der vorgenommenen Vergleiche zweier Einträge in einem
     * Array
     */
    public int getComparisons()
    {
        return comparisons;
    }

    /**
     * Gibt die Anzahl der vorgenommenen Vertauschoperationen dieser Instanz
     * zurück. Nur sinnvoll wenn sortiere() schon benutzt wurde. Die Zahl wird
     * nach Aufruf von sortiere() in der Regel NICHT zurückgesetzt.
     *
     * @return Anzahl der vorgenommenen Vertauschoperationen
     */
    public int getSwitches()
    {
        return permutations;
    }

    /**
     * Gibt die Anzahl der vorgenommenen Lesezugriffe dieser Instanz zurück. Nur
     * sinnvoll wenn sortiere() schon benutzt wurde. Die Zahl wird nach Aufruf
     * von sortiere() in der Regel NICHT zurückgesetzt.
     *
     * @return Anzahl Lesezugriffe
     */
    public int getReadOperations()
    {
        return readAccesses;
    }

    /**
     * Gibt die Anzahl der vorgenommenen Schreibzugriffe dieser Instanz zurück.
     * Nur sinnvoll wenn sortiere() schon benutzt wurde. Die Zahl wird nach
     * Aufruf von sortiere() in der Regel NICHT zurückgesetzt.
     *
     * @return Anzahl Lesezugriffe
     */
    public int getWriteOperations()
    {
        return writeAccesses;
    }

    public void addSortierListener(SortingListener neu)
    {
        listener.add(neu);
    }

    public void removeSortierListener(SortingListener sl)
    {
        listener.remove(sl);
    }

    /**
     * Returns the Element at the spezified position. Also increases the counter
     * for readaccess operations.
     *
     * @param i Index of the Element
     * @return value at the spezified position in the array
     */
    public E get(int i)
    {
        readAccesses++;
        return values[i];
    }

    /**
     * Sets a given value at the spezified position. Also increases the counter
     * for writeaccess operations.
     *
     * @param i Position in the array
     * @param val new value to set at the spezified position
     */
    public void set(int i, E val)
    {
        writeAccesses++;
        values[i] = val;
    }

    /**
     * Returns the number of elements. Equal to array.length.
     *
     * @return the length of the array
     */
    public int size()
    {
        return values.length;
    }

    /**
     * Sets a clone of the given array to use for this instance.
     *
     * @param a the array to set
     */
    public void setValues(E[] a)
    {
        values = a.clone();
    }

    public E[] getValues()
    {
        return values.clone();
    }

    /**
     * Resets all operation counters to zero. Use this before starting a new
     * measurement.
     */
    public final void resetCounters()
    {
        permutations = 0;
        comparisons = 0;
        readAccesses = 0;
        writeAccesses = 0;
    }

    /**
     * Compares the elements in the array at the spezified positions. returns
     * the result of the compare() of the inherited Comparator instance.
     * Increases comparison counter as well as readaccess counter due to calling
     * the get() function.
     *
     * @param i1 position of the first element.
     * @param i2 position of the second element.
     * @return true if the element at the first position is GREATER THAN the
     * element at the second position, false otherwise.
     */
    public boolean compare(int i1, int i2)
    {
        for (SortingListener sl : listener)
        {
            sl.compared(i1, i2);
        }
        comparisons++;
        return comp.compare(get(i1), get(i2));
    }

    public boolean compare(E e1, E e2)
    {
        for (SortingListener sl : listener)
        {
            //sl.verglichen(e1, e2);
        }
        comparisons++;
        return comp.compare(e1, e2);
    }

    public void setComparator(Comparator c)
    {
        comp = c;
    }

    public void switchValues(int i1, int i2)
    {
        E temp = get(i1);
        set(i1, get(i2));
        set(i2, temp);
        permutations++;
        for (SortingListener sl : listener)
        {
            sl.switched(i1, i2);
        }
    }

    public boolean check()
    {
        for (int i = 1; i < values.length; i++)
        {
            if (compare(i - 1, i))
            {
                return false;
            }
        }
        return true;
    }

    public void printValues()
    {
        for (E e : values)
        {
            System.out.print(e + " ");
        }
    }
}
