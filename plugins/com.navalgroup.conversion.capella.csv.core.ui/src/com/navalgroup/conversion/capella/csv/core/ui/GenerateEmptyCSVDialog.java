/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.ui;

import org.eclipse.swt.widgets.Shell;

public class GenerateEmptyCSVDialog extends AbstractCSVConversionDialog {

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            Shell
	 */
	public GenerateEmptyCSVDialog(Shell parentShell) {
		super(parentShell, "Capella Conversion", "Generate empty CSV files", "Generation");
		setHelpAvailable(false);
	}
}
