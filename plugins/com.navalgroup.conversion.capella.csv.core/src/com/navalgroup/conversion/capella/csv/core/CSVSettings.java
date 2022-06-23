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

public class CSVSettings {
	/**
	 * List separator.
	 */
	public static final String LIST_SEPARATOR = ", ";

	/**
	 * List separator regex.
	 */
	public static final String LIST_SEPARATOR_REGEX = ",[ \\t]*";

	/**
	 * List beginning char.
	 */
	public static final String LIST_BEGIN = "[";

	/**
	 * List ending char.
	 */
	public static final String LIST_END = "]";

	/**
	 * CSV column indicating the corresponding row/record is a new model element.
	 */
	public static final String TO_CREATE_COLUMN = "To create";

	/**
	 * CSV column indicating the corresponding row/record is a model element to
	 * delete.
	 */
	public static final String TO_DELETE_COLUMN = "To delete";

	/**
	 * CSV column indicating the corresponding row/record is a model element to
	 * update.
	 */
	public static final String TO_UPDATE_COLUMN = "To update";

	/**
	 * CSV column indicating the creation date of the corresponding row/record.
	 */
	public static final String CREATION_DATE_COLUMN = "Creation date";

	/**
	 * CSV column indicating the deletion date of the corresponding row/record.
	 */
	public static final String DELETION_DATE_COLUMN = "Deletion date";

	/**
	 * CSV column indicating the last update date of the corresponding row/record.
	 */
	public static final String LAST_UPDATE_DATE_COLUMN = "Last update date";

	/**
	 * CSV column indicating the creation time of the corresponding row/record.
	 */
	public static final String CREATION_TIME_COLUMN = "Creation time";

	/**
	 * CSV column indicating the deletion time of the corresponding row/record.
	 */
	public static final String DELETION_TIME_COLUMN = "Deletion time";

	/**
	 * CSV column indicating the last update time of the corresponding row/record.
	 */
	public static final String LAST_UPDATE_TIME_COLUMN = "Last update time";

	/**
	 * CSV column indicating the id of the corresponding row/record.
	 */
	public static final String ID_COLUMN = "id";

	/**
	 * conversionDirectoryPath.
	 */
	private String conversionDirectoryPath;
	/**
	 * fieldDelimiter.
	 */
	private Character fieldDelimiter;
	/**
	 * textDelimiter.
	 */
	private Character textDelimiter;
	/**
	 * characterSet.
	 */
	private Charset characterSet;
	/**
	 * lineSeparator.
	 */
	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param directoryPath
	 *            String
	 * @param fieldDelimiter
	 *            Character
	 * @param textDelimiter
	 *            Character
	 * @param characterSet
	 *            Charset
	 * @param lineSeparator
	 *            String
	 */
	public CSVSettings(String directoryPath, Character fieldDelimiter, Character textDelimiter, Charset characterSet,
			String lineSeparator) {
		this.setConversionDirectoryPath(directoryPath);
		this.setFieldDelimiter(fieldDelimiter);
		this.setTextDelimiter(textDelimiter);
		this.setCharacterSet(characterSet);
		this.setLineSeparator(lineSeparator);
	}

	public String getConversionDirectoryPath() {
		return this.conversionDirectoryPath;
	}

	public void setConversionDirectoryPath(String conversionDirectoryPath) {
		this.conversionDirectoryPath = conversionDirectoryPath;
	}

	public Character getFieldDelimiter() {
		return this.fieldDelimiter;
	}

	public void setFieldDelimiter(Character fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public Character getTextDelimiter() {
		return this.textDelimiter;
	}

	public void setTextDelimiter(Character textDelimiter) {
		this.textDelimiter = textDelimiter;
	}

	public Charset getCharacterSet() {
		return this.characterSet;
	}

	public void setCharacterSet(Charset characterSet) {
		this.characterSet = characterSet;
	}

	public String getLineSeparator() {
		return this.lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
}
