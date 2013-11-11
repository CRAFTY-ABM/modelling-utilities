package com.moseph.modelutils.fastdata;

import java.util.*;

public interface NumberMap<T extends Indexed> extends UnmodifiableNumberMap<T>
{
	public void putDouble( T key, double value );
	public void addDouble( T key, double value );
	public void clear();
	public void copyFrom( UnmodifiableNumberMap<T> source );
	/**
	 * Subtracts subtractor from this, and puts the result into target
	 * @param subtractor
	 * @param target
	 */
	public void subtractInto( UnmodifiableNumberMap<T> subtractor, NumberMap<T> target );
	
}
