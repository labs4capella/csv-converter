/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.tests;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractConverterTestCases {

	/**
	 * The tests resources folder.
	 */
	protected static final String RESOURCES_FOLDER = "resources";

	/**
	 * Default field delimiter for tests.
	 */
	protected static final char FIELD_DELIMITER = ';';

	/**
	 * Default text delimiter for tests.
	 */
	protected static final char TEXT_DELIMITER = '\"';

	/**
	 * Default character set for tests.
	 */
	protected static final Charset CHARACTER_SET = StandardCharsets.UTF_8;

	/**
	 * Default line separator for tests.
	 */
	protected static final String LINE_SEPARATOR = "\n";
}
