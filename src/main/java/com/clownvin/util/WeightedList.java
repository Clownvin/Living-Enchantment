package com.clownvin.util;

import java.util.*;

public final class WeightedList<E extends Weighted> implements List<E> {


    private final ArrayList<E> list = new ArrayList<>();
    private float totalWeights = 0.0f;

    public E get() {
        if (list.size() == 0)
            return null;
        float selected = (float) Math.random() * totalWeights;
        float soFar = 0;
        float thisWeight = 0.0f;
        for (int i = 0; i < list.size(); i++) {
            thisWeight = list.get(i).getWeight();
            if (selected == soFar || selected < soFar + thisWeight)
                return list.get(i);
            soFar += thisWeight;
            System.out.println(soFar);
        }
        System.err.println("WeightedList: Failed to actually select weighted entry. Total entries: " + list.size() + ", Total Weight: " + totalWeights);
        return list.get(0);
    }

    public E get(float val) {
        if (list.size() == 0 || val >= 1.0f)
            return list.get(0);
        float selected = val * totalWeights;
        float soFar = 0;
        float thisWeight = 0.0f;
        for (int i = 0; i < list.size(); i++) {
            thisWeight = list.get(i).getWeight();
            if (selected == soFar || selected < soFar + thisWeight)
                return list.get(i);
            soFar += thisWeight;
        }
        System.err.println("WeightedList: Failed to actually select weighted entry for " + selected + ". Total entries: " + list.size() + ", Total Weight: " + totalWeights);
        return list.get(0);
    }

    public float getVal(E e) {
        float soFar = 0;
        float thisWeight = 0.0f;
        for (int i = 0; i < list.size(); i++) {
            thisWeight = list.get(i).getWeight();
            if (list.get(i) == e) {
                return (soFar + (thisWeight / 2.0F)) / totalWeights;
            }
            soFar += thisWeight;
        }
        return 0;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        int i = 0;
        Object[] array = new Object[list.size()];
        for (Weighted b : list) {
            array[i++] = b;
        }
        return array;
    }

    @Override
    public synchronized <T> T[] toArray(T[] a) {
        for (int i = 0; i < list.size() && i < a.length; i++) {
            a[i] = (T) list.get(i);
        }
        return a;
    }

    @Override
    public synchronized boolean add(E e) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWeight() > e.getWeight()) {
                continue;
            }
            list.add(i, e);
            //System.out.print(totalWeights + " + " + e.getWeight() + " = ");
            totalWeights += e.getWeight();
            //System.out.println(totalWeights);
            return true;
        }
        list.add(e);
        //System.out.print(totalWeights + " + " + e.getWeight() + " = ");
        totalWeights += e.getWeight();
        //System.out.println(totalWeights);
        return true;
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (!(o instanceof Weighted))
            return true;
        Weighted w = (Weighted) o;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWeight() != w.getWeight())
                continue;
            boolean ret = list.remove(o);
            if (ret)
                totalWeights += w.getWeight();
            return ret;
        }
        return true;
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        outer:
        for (Object o : c) {
            if (!(o instanceof Weighted))
                return false;
            Weighted w = (Weighted) o;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWeight() != w.getWeight())
                    continue;
                if (!list.contains(c))
                    return false;
                continue outer;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            if (!add(e))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not allowed to choose index of placement.");
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("It's just not supported.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            remove(o);
        }
        return true;
    }

    @Override
    public synchronized void clear() {
        list.clear();
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Cannot pick index. Use get().");
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Cannot pick index. Use add(element).");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Cannot pick index. Use add(element).");
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Cannot pick index. Use remove(element).");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("No object has an index.");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("No object has an index.");
    }

    @Override
    public ListIterator<E> listIterator() {
        return (ListIterator<E>) iterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("Not allowed to select an index.");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not allowed to select a set of indices.");
    }
}
