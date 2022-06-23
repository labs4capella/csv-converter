/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ImportExportDirectoryFieldEditor extends DirectoryFieldEditor {

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            String
	 * @param labelText
	 *            String
	 * @param parent
	 *            Composite
	 */
	public ImportExportDirectoryFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	public Text getTextControl() {
		return super.getTextControl();
	}
}
