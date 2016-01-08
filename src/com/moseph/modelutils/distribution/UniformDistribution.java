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

import cern.jet.random.Uniform;
import de.cesr.uranus.core.UranusRandomService;

public class UniformDistribution implements Distribution {
	@Attribute(required = false)
	double min = 0;
	@Attribute(required = false)
	double max = 1;

	boolean initialised = false;

	UranusRandomService rService = null;
	Uniform uniform;

	public UniformDistribution() {
	}

	public UniformDistribution(double min, double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public double sample() {
		return this.uniform.nextDoubleFromTo(min, max);
	}

	@Override
	public void init(UranusRandomService rService, String generatorName) {
		this.rService = rService;
		this.uniform = rService.getNewUniformDistribution(rService
				.getGenerator(generatorName));
		this.initialised = true;
	}

	/**
	 * @see com.moseph.modelutils.distribution.Distribution#isInitialised()
	 */
	@Override
	public boolean isInitialised() {
		return this.initialised;
	}
}
