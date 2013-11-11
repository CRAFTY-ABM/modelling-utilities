package com.moseph.modelutils.fastdata;

import java.util.List;


public interface IndexSet<T extends Indexed> extends Iterable<T>
{
	public List<T> getList();
	public int getMaxIndex();
	public boolean containsKey( T t );
	public T get( int index );
	public int size();
}
