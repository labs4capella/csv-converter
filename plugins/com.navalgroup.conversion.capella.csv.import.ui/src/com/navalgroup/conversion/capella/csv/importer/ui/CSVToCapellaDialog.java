/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.importer.ui;

import org.eclipse.swt.widgets.Shell;

import com.navalgroup.conversion.capella.csv.core.ui.AbstractCSVConversionDialog;

/**
 * Dialog displayed to users when importing a CSV file to a Capella model.
 * 
 * @author arichard
 *
 */
public class CSVToCapellaDialog extends AbstractCSVConversionDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CSVToCapellaDialog(Shell parentShell) {
		super(parentShell, "CSV to Capella Import", "Import Capella model data from CSV files", "Import");
		setHelpAvailable(false);
	}
}
