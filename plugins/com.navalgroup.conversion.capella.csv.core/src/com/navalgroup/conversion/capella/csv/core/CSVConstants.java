/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class CSVConstants {

	// CHECKSTYLE:OFF
	public static final String DEFAULT_DIRECTORY_PREF_ID = "defaultExportDirectory";
	public static final String DEFAULT_FIELD_DELIMITER_PREF_ID = "defaultExportFieldDelimiter";
	public static final String DEFAULT_TEXT_DELIMITER_PREF_ID = "defaultExportTextDelimiter";
	public static final String DEFAULT_CHARACTER_SET_PREF_ID = "defaultExportCharacterSet";
	public static final String DEFAULT_LINE_SEPARATOR_PREF_ID = "defaultExportLineSeparator";

	public static final String SEMI_COLON = "Semi-colon";
	public static final String COLON = "Colon";
	public static final String SPACE = "Space";
	public static final String TAB = "Tab";
	public static final String OTHER = "Other:";
	public static final Character SEMI_COLON_DELIMITER = ';';
	public static final Character COLON_DELIMITER = ',';
	public static final Character SPACE_DELIMITER = ' ';
	public static final Character TAB_DELIMITER = '	';

	public static final String DOUBLE_QUOTE = "DoubleQuote";
	public static final String SIMPLE_QUOTE = "SimpleQuote";
	public static final String NONE = "None";
	public static final String DOUBLE_QUOTE_DELIMITER = "\"";
	public static final String SIMPLE_QUOTE_DELIMITER = "\'";
	public static final String NONE_DELIMITER = "None";

	public static final String SYSTEM = "System";
	public static final String WINDOWS = "Windows";
	public static final String UNIX = "Unix";

	public static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
	public static final String WIN_LINE_SEPARATOR = "\r\n";
	public static final String UNIX_LINE_SEPARATOR = "\n";

	public static final String[] FIELD_DELIMITER_ITEMS = new String[] { String.valueOf(SEMI_COLON_DELIMITER),
			String.valueOf(COLON_DELIMITER), String.valueOf(SPACE_DELIMITER), String.valueOf(TAB_DELIMITER) };
	public static final String[] TEXT_DELIMITER_ITEMS = new String[] { DOUBLE_QUOTE_DELIMITER, SIMPLE_QUOTE_DELIMITER,
			NONE_DELIMITER };
	public static final String[] CHARACTER_SET_ITEMS = new String[] { StandardCharsets.UTF_8.displayName(),
			StandardCharsets.ISO_8859_1.displayName(), Charset.forName("windows-1252").displayName() };
	public static final String[] LINE_SEPARATOR_ITEMS = new String[] { SYSTEM, WINDOWS, UNIX };

	public static final String[][] FIELD_DELIMITER_LABELS_AND_VALUES = new String[][] { { SEMI_COLON, SEMI_COLON },
			{ COLON, COLON }, { SPACE, SPACE }, { TAB, TAB }, { OTHER, OTHER } };

	public static final String[][] TEXT_DELIMITER_LABELS_AND_VALUES = new String[][] {
			{ String.valueOf(DOUBLE_QUOTE_DELIMITER), DOUBLE_QUOTE },
			{ String.valueOf(SIMPLE_QUOTE_DELIMITER), SIMPLE_QUOTE }, { NONE_DELIMITER, NONE } };

	public static final String[][] CHARACTER_SET_LABELS_AND_VALUES = new String[][] {
			{ StandardCharsets.UTF_8.displayName(), StandardCharsets.UTF_8.displayName() },
			{ StandardCharsets.ISO_8859_1.displayName(), StandardCharsets.ISO_8859_1.displayName() },
			{ Charset.forName("windows-1252").displayName(), Charset.forName("windows-1252").displayName() } };

	public static final String[][] LINE_SEPARATOR_LABELS_AND_VALUES = new String[][] { { SYSTEM, SYSTEM },
			{ WINDOWS, WINDOWS }, { UNIX, UNIX } };

	public static final String CSV_EXT = ".csv";
	public static final String BEFORE_SUFFIX = "_before";
	public static final String BEFORE_CSV = BEFORE_SUFFIX + CSV_EXT;
	public static final String AFTER_FOLDER = "after";

	// CHECKSTYLE:ON
	private CSVConstants() {
		// Prevent instanciation
	}
}
