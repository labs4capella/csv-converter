/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.export.ui;

import org.eclipse.swt.widgets.Shell;

import com.navalgroup.conversion.capella.csv.core.ui.AbstractCSVConversionDialog;

/**
 * Dialog displayed to users when exporting a Capella model to a CSV file.
 * 
 * @author arichard
 *
 */
public class CapellaToCSVDialog extends AbstractCSVConversionDialog {

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CapellaToCSVDialog(Shell parentShell) {
		super(parentShell, "Capella to CSV Export", "Export Capella model data into CSV files", "Export");
		setHelpAvailable(false);
	}
}
