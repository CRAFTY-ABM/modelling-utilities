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
package com.moseph.modelutils.distribution;

import org.simpleframework.xml.Attribute;

import de.cesr.uranus.core.UranusRandomService;

public class ConstantDistribution implements Distribution {
	@Attribute(required = false)
	double value = 0;

	@Override
	public double sample() {
		return value;
	}

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#init(de.cesr.uranus.core.UranusRandomService,
	 *      java.lang.String)
	 */
	@Override
	public void init(UranusRandomService rService, String generatorName) {
		// nothing to do
	}
}
