/**
 * Copyright (C) 2016 Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package dmsr.utils.eds;

import java.io.Serializable;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.03.2016
 */
public class Dictionary<K extends Object, E extends Object> implements Serializable
{

    private static final long serialVersionUID = -7458466228319267397L;
    protected final java.util.LinkedList<K> keys;
    protected final java.util.LinkedList<E> elements;

    public Dictionary()
    {
        keys = new java.util.LinkedList<K>();
        elements = new java.util.LinkedList<E>();
    }

    public int size()
    {
        return keys.size();
    }

    public boolean isEmpty()
    {
        return keys.isEmpty();
    }

    public boolean containsKey(K key)
    {
        for (K k : keys)
        {
            if (k.equals(key))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsElement(E element)
    {
        for (E e : elements)
        {
            if (e.equals(element))
            {
                return true;
            }
        }
        return false;
    }

    public int indexOfKey(K key)
    {
        for (int i = 0; i < keys.size(); i++)
        {
            if (keys.get(i).equals(key))
            {
                return i;
            }
        }
        return -1;
    }

    public int firstIndexOf(E element)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            if (elements.get(i).equals(element))
            {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(E element)
    {
        int last = -1;
        for (int i = 0; i < elements.size(); i++)
        {
            if (elements.get(i).equals(element))
            {
                last = i;
            }
        }
        return last;
    }

    public E get(K key)
    {
        for (int i = 0; i < keys.size(); i++)
        {
            if (keys.get(i).equals(key))
            {
                return elements.get(i);
            }
        }
        return null;
    }

    public boolean set(K key, E e)
    {
        for (int i = 0; i < keys.size(); i++)
        {
            if (keys.get(i).equals(key))
            {
                elements.set(i, e);
                return true;
            }
        }
        return false;
    }

    public boolean add(K key, E element)
    {
        if (!containsKey(key))
        {
            keys.add(key);
            elements.add(element);
            return true;
        }
        return false;
    }

    public int addAll(Dictionary<K, E> values)
    {
        int n = 0;
        for (int i = 0; i < values.size(); i++)
        {
            if (add(values.keys.get(i), values.elements.get(i)))
            {
                n++;
            }
        }
        return n;
    }

    public E remove(K key)
    {
        for (int i = 0; i < keys.size(); i++)
        {
            if (keys.get(i).equals(key))
            {
                E removed = elements.remove(i);
                keys.remove(i);
                return removed;
            }
        }
        return null;
    }

    public java.util.LinkedList<K> getKeys()
    {
        return new java.util.LinkedList<>(keys);
    }

    public java.util.LinkedList<E> getElements()
    {
        return new java.util.LinkedList<>(elements);
    }

    public K getKey(int i)
    {
        return keys.get(i);
    }

    public E getElement(int i)
    {
        return elements.get(i);
    }

    public void clear()
    {
        keys.clear();
        elements.clear();
    }

    /**
     *
     * @return
     */
    @Override
    public Dictionary<K, E> clone()
    {
        try
        {
            return (Dictionary<K, E>) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            System.err.println(e);
            return new Dictionary<>();
        }
    }
}
