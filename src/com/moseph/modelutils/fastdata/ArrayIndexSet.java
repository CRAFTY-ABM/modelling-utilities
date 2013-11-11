package com.moseph.modelutils.fastdata;

import java.util.*;
import static java.lang.Math.*;

/**
 * Simple IndexSet. Not modifiable after creation
 * Uses an ArrayList for backing, so quick and cheap. Uses a HashSet to test for containment
 * @author dmrust
 *
 * @param <T>
 */
public class ArrayIndexSet<T extends Indexed> implements IndexSet<T>, Iterable<T>
{
	List<T> list;
	Set<T> set;
	int maxIndex = 0;
	
	public ArrayIndexSet( Collection<? extends T> values )
	{
		list = Collections.unmodifiableList( new ArrayList<T>( values ) );
		set = Collections.unmodifiableSet( new HashSet<T>( values ) );
		for( T t : list ) maxIndex = max( maxIndex, t.getIndex() );
	}
	
	public ArrayIndexSet( T... values )
	{
		this( Arrays.asList(values));
	}

	public List<T> getList() { return list; }
	public Iterator<T> iterator() { return list.iterator(); }
	public int getMaxIndex() { return maxIndex; }
	public boolean containsKey( T t ) { return set.contains( t ); }
	public T get( int index ) { return list.get( index ); }
	public int size() { return list.size(); }
	
	public String toString()
	{
		String s = "[";
		for( T t : list ) s += " " + t;
		return s + " ]";
	}

}
