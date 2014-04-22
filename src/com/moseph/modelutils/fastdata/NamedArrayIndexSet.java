 /**
 * This file is part of
 * 
 * ModellingUtilities
 *
 * Copyright (C) 2014 School of GeoScience, University of Edinburgh, Edinburgh, UK
 * 
 * ModellingUtilities is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * ModellingUtilities is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * School of Geoscience, University of Edinburgh, Edinburgh, UK
 * 
 */
package com.moseph.modelutils.fastdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple Named IndexSet. Not modifiable after creation
 * Uses an ArrayList for backing, so quick and cheap. Uses a HashSet to test for containment, and a
 * hash map to get byName
 * @author dmrust
 *
 * @param <T>
 */
public class NamedArrayIndexSet<T extends Indexed & Named> extends ArrayIndexSet<T> implements NamedIndexSet<T>
{
	Map<String, T> byName = new LinkedHashMap<String, T>();
	
	public NamedArrayIndexSet( Collection<? extends T> values )
	{
		super( values );
		for( T t : list ) {
			byName.put( t.getName(), t );
		}
	}
	
	public NamedArrayIndexSet( T... values )
	{
		this( Arrays.asList(values));
	}

	@Override
	public T forName( String name ) { return byName.get( name ); }
	@Override
	public boolean contains( String name ) { return byName.containsKey( name ); }

	// TODO check if HashMap also works... (s.a.)
	@Override
	public Set<String> names() { return byName.keySet(); }
}
