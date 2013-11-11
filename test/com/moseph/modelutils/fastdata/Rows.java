package com.moseph.modelutils.fastdata;

import java.util.*;

public enum Rows implements Indexed, Named
{
	X, Y, Z;
	public int getIndex() { return ordinal(); }
	public String getName() { return toString(); }
	public static HashSet<Rows> rows = new HashSet<Rows>( Arrays.asList( Rows.values() ) );
}