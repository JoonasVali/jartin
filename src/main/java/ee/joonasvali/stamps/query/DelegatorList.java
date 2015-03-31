/*
 * Copyright (c) 2015, Jartin. All rights reserved. This application is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. Do not remove this header.
 */

package ee.joonasvali.stamps.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Temporary access object.
 * References the original list, so adding or removing anything from it crashes something probably.
 *
 * TODO this list is so stupid it should be removed and find a solid way to achieve the same thing.
 *
 * @author Joonas Vali
 */
public class DelegatorList<E> implements List<E> {
  private final List<E> original;
  private final int start, end;
  public DelegatorList(List<E> original, int start, int end) {
    if(start < 0 || start > original.size() - 1) throw new IllegalArgumentException("Start can't be " + start);
    if(end < 1 || end <= start || end > original.size()) throw new IllegalArgumentException("End can't be " + end);
    this.original = original;
    this.end = end;
    this.start = start;
  }

  @Override
  public int size() {
    return end - start;
  }

  @Override
  public boolean isEmpty() {
    return original.size() > 0 && end <= start;
  }

  @Override
  public boolean contains(Object o) {
    return original.subList(start, end).contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return original.subList(start, end).iterator();
  }

  @Override
  public Object[] toArray() {
    return original.subList(start, end).toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return original.subList(start, end).toArray(a);
  }

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return original.subList(start, end).containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public E get(int index) {
    if(index >= size()) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for size " + size());
    }
    return original.get(index + start);
  }

  @Override
  public E set(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o) {
    return subList(start, end).indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return subList(start, end).lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return subList(start, end).listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return original.listIterator(index + start);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return original.subList(start, end).subList(fromIndex, toIndex);
  }

  @Override
  public String toString() {
    return original.subList(start, end).toString();
  }
}
