package com.moseph.modelutils.fastdata;

import org.junit.Test;
import static com.moseph.modelutils.fastdata.Columns.*;
import static org.junit.Assert.*;

public class ArrayIndexSetTest
{
	
	@Test
	public void testCreatingArrayIndexSet()
	{
		ArrayIndexSet<Indexed> ais = new ArrayIndexSet<Indexed>( cols );
		assertEquals( 5, ais.size() );
		assertEquals( 4, ais.getMaxIndex() );
	}

}
