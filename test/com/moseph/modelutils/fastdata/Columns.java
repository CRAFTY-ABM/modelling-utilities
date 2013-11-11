package com.moseph.modelutils.fastdata;

import java.util.*;

public enum Columns implements Indexed, Named
{
	A, B, C, D, E;
	public int getIndex() { return ordinal(); }
	public String getName() { return toString(); }
	public static List<Columns> cols = new ArrayList<Columns>( Arrays.asList( Columns.values() ) );
}