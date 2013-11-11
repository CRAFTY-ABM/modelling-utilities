package com.moseph.modelutils.fastdata;

import java.util.Set;


public interface NamedIndexSet<T extends Named & Indexed> extends IndexSet<T>
{
	public T forName( String name );
	public boolean contains( String name );
	public Set<String> names();
}
