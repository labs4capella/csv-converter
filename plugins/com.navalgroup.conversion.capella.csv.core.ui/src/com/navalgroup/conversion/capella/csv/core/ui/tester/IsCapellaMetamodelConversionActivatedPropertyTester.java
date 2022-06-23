/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.ui.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.navalgroup.conversion.capella.csv.core.jobs.ConversionOptions;

/**
 * Test if Capella Metamodel conversion is available.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class IsCapellaMetamodelConversionActivatedPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return ConversionOptions.getGenerateCapellaMetamodelOption();
	}

}
