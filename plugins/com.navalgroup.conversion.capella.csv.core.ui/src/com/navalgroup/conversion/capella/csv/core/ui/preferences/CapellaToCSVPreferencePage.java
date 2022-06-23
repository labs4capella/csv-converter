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

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;
import com.navalgroup.conversion.capella.csv.core.ui.Activator;

public class CapellaToCSVPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * directoryFieldEditor.
	 */
	private ImportExportDirectoryFieldEditor directoryFieldEditor;
	/**
	 * fieldDelimiterFieldEditor.
	 */
	private FieldDelimiterFieldEditor fieldDelimiterFieldEditor;

	/**
	 * Create the preference page.
	 */
	public CapellaToCSVPreferencePage() {
		super(FLAT);
		// get the preference store from the Core UI plugin
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		this.directoryFieldEditor = new ImportExportDirectoryFieldEditor(CSVConstants.DEFAULT_DIRECTORY_PREF_ID,
				"Default import/export directory:", getFieldEditorParent());
		this.directoryFieldEditor.setEmptyStringAllowed(false);
		this.directoryFieldEditor.getTextControl().addModifyListener(e -> {
			setValid(isPageValid());
		});
		this.directoryFieldEditor.getTextControl().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				setValid(isPageValid());
			}

			@Override
			public void focusGained(FocusEvent e) {
				setValid(isPageValid());
			}
		});
		this.fieldDelimiterFieldEditor = new FieldDelimiterFieldEditor(CSVConstants.DEFAULT_FIELD_DELIMITER_PREF_ID,
				"Default field delimiter:", getFieldEditorParent());
		this.fieldDelimiterFieldEditor.getOtherFieldDelimiterWidget().addModifyListener(e -> {
			setValid(isPageValid());
		});
		this.fieldDelimiterFieldEditor.getOtherFieldDelimiterWidget().addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setValid(isPageValid());
			}

			@Override
			public void focusGained(FocusEvent e) {
				setValid(isPageValid());
			}
		});
		ComboFieldEditor textDelimiterFieldEditor = new ComboFieldEditor(CSVConstants.DEFAULT_TEXT_DELIMITER_PREF_ID,
				"Default text delimiter:", CSVConstants.TEXT_DELIMITER_LABELS_AND_VALUES, getFieldEditorParent());
		ComboFieldEditor charSetFieldEditor = new ComboFieldEditor(CSVConstants.DEFAULT_CHARACTER_SET_PREF_ID,
				"Default character set:", CSVConstants.CHARACTER_SET_LABELS_AND_VALUES, getFieldEditorParent());
		ComboFieldEditor lineSeparatorFieldEditor = new ComboFieldEditor(CSVConstants.DEFAULT_LINE_SEPARATOR_PREF_ID,
				"Default line separator:", CSVConstants.LINE_SEPARATOR_LABELS_AND_VALUES, getFieldEditorParent());
		addField(this.directoryFieldEditor);
		addField(this.fieldDelimiterFieldEditor);
		addField(textDelimiterFieldEditor);
		addField(charSetFieldEditor);
		addField(lineSeparatorFieldEditor);
	}

	/**
	 * Initialize the preference page.
	 * 
	 * @param workbench
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Initialize the preference page
		setValid(isPageValid());
	}

	/**
	 * Return if page is valid.
	 * 
	 * @return if page is valid.
	 */
	public boolean isPageValid() {
		boolean valid = true;
		if (this.directoryFieldEditor != null) {
			String directoryPath = this.directoryFieldEditor.getStringValue();
			if (!Files.isDirectory(Paths.get(directoryPath), LinkOption.values())) {
				valid = false;
				setErrorMessage("The directory does not exist");
			}
		}
		if (this.fieldDelimiterFieldEditor != null && this.fieldDelimiterFieldEditor.isOtherFieldDelimiterSelected()) {
			if (this.fieldDelimiterFieldEditor.getOtherFieldDelimiterWidget() == null
					|| this.fieldDelimiterFieldEditor.getOtherFieldDelimiterWidget().getText().length() != 1
					|| !ConversionUtil.isAllowedFieldDelimiter(
							this.fieldDelimiterFieldEditor.getOtherFieldDelimiterWidget().getText().charAt(0))) {
				valid = false;
				setErrorMessage("This field delimiter is not allowed");
			}
		}
		if (valid) {
			setErrorMessage(null);
		}
		return valid;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		isValid();
	}
}
