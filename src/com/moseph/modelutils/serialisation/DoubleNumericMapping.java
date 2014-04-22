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
package com.moseph.modelutils.serialisation;

import java.util.LinkedHashMap;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Utility class for serialising multiple level maps in a type-safe way
 * @author dmrust
 *
 * @param <T>
 * @param <S>
 */
@Root
public class DoubleNumericMapping<T,S>
{
	@Element
	public T first;
	@Element
	public S second;
	@Attribute
	double value;
	
	public DoubleNumericMapping() {}
	
	public DoubleNumericMapping( T first, S second, double value )
	{
		super();
		this.first = first;
		this.second = second;
		this.value = value;
	}

	public void insert( Map<T,Map<S,Double>> target )
	{
		// TODO check if LinkedHashMap required
		if (!target.containsKey(first)) {
			target.put( first, new LinkedHashMap<S, Double>() );
		}
		target.get( first ).put( second, value );
	}
}