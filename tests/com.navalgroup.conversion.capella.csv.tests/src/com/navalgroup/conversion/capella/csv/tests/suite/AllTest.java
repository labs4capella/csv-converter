/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.tests.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.navalgroup.conversion.capella.csv.tests.CSVToCapellaConverterTestCases;
import com.navalgroup.conversion.capella.csv.tests.CapellaToCSVConverterTestCases;

/**
 * Test suite used to run all the unit tests of capella/csv import/export.
 *
 * @author arichard
 */
@RunWith(Suite.class)
@SuiteClasses({ CapellaToCSVConverterTestCases.class, CSVToCapellaConverterTestCases.class })
public final class AllTest {
	private AllTest() {
		// Prevent instantiation
	}
}
