/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.importer;

public class CSVToCapellaException extends Exception {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3098120042043552449L;

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            String
	 */
	public CSVToCapellaException(String message) {
		super(message);
	}
}
