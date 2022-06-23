/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.jobs;

/**
 * Options.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public final class ConversionOptions {

	/**
	 * Capella Metamodel conversion option.
	 */
	private static final String CAPELLA_METAMODEL_CSV_CONVERSION = "conversion.capella.metamodel";

	/**
	 * Constructor.
	 */
	private ConversionOptions() {
	}

	/**
	 * Return if Capella Metamodel conversion action is displayed.
	 * 
	 * @return if Capella Metamodel conversion action is displayed.
	 */
	public static boolean getGenerateCapellaMetamodelOption() {
		return Boolean.getBoolean(CAPELLA_METAMODEL_CSV_CONVERSION);
	}

}
