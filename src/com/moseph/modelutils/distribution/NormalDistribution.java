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

import cern.jet.random.Normal;
import de.cesr.uranus.core.UranusRandomService;


public class NormalDistribution implements Distribution
{
	@Attribute(required=false)
	double mean = 0;
	@Attribute(required=false)
	double sd = 1;

	UranusRandomService rService;
	Normal normal;

	public NormalDistribution() {}

	public NormalDistribution( double mean, double sd ) 
	{
		this.mean = mean;
		this.sd = sd;
	}
	/**
	 * @see com.moseph.modelutils.distribution.Distribution#sample()
	 */
	@Override
	public double sample() { 
		return this.normal.nextDouble();
	}

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#init(de.cesr.uranus.core.UranusRandomService, java.lang.String)
	 */
	@Override
	public void init(UranusRandomService rService, String generatorName) {
		this.rService = rService;
		this.normal = this.rService.createNormal(mean, sd,
				rService.getGenerator(generatorName));
	}
}
