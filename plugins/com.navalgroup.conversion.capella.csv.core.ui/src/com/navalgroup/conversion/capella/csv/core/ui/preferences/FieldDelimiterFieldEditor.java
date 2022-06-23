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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;

/**
 * This class is a copy of {@link RadioGroupFieldEditor} with an extra text
 * field widget.
 * 
 * @author arichard
 *
 */
public class FieldDelimiterFieldEditor extends FieldEditor {

	/**
	 * List of radio button entries of the form [label,value].
	 */
	private String[][] labelsAndValues;

	/**
	 * Number of columns into which to arrange the radio buttons.
	 */
	private int numRadios;

	/**
	 * The current value, or <code>null</code> if none.
	 */
	private String value;

	/**
	 * The box of radio buttons, or <code>null</code> if none (before creation and
	 * after disposal).
	 */
	private Composite radioBox;

	/**
	 * The radio buttons, or <code>null</code> if none (before creation and after
	 * disposal).
	 */
	private Button[] radioButtons;

	/**
	 * The text field holding the custom field delimiter.
	 */
	private Text otherFieldDelimiterText;

	/**
	 * Creates a new radio group field editor.
	 */
	protected FieldDelimiterFieldEditor() {
	}

	/**
	 * Creates a field delimiter field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public FieldDelimiterFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		Assert.isTrue(checkArray(CSVConstants.FIELD_DELIMITER_LABELS_AND_VALUES));
		this.labelsAndValues = CSVConstants.FIELD_DELIMITER_LABELS_AND_VALUES;
		this.numRadios = CSVConstants.FIELD_DELIMITER_LABELS_AND_VALUES.length;
		createControl(parent);
	}

	/**
	 * Return isOtherFieldDelimiterSelected.
	 * 
	 * @return isOtherFieldDelimiterSelected.
	 */
	public boolean isOtherFieldDelimiterSelected() {
		for (Button radioBtn : this.radioButtons) {
			if (CSVConstants.OTHER.equals(radioBtn.getText())) {
				return radioBtn.getSelection();
			}
		}
		return false;
	}

	public Text getOtherFieldDelimiterWidget() {
		return this.otherFieldDelimiterText;
	}

	public String getOtherFieldDelimiter() {
		return this.otherFieldDelimiterText.getText();
	}

	@Override
	protected void adjustForNumColumns(int numberOfRadios) {
		Control control = getLabelControl();
		if (control != null) {
			((GridData) control.getLayoutData()).horizontalSpan = numberOfRadios + 1;
		}
		((GridData) this.radioBox.getLayoutData()).horizontalSpan = numberOfRadios + 1;
	}

	/**
	 * Checks whether given <code>String[][]</code> is of "type"
	 * <code>String[][2]</code>.
	 * 
	 * @param table
	 *
	 * @return <code>true</code> if it is ok, and <code>false</code> otherwise
	 */
	private boolean checkArray(String[][] table) {
		boolean isValid = true;
		if (table == null) {
			isValid = false;
		}
		for (String[] array : table) {
			if (array == null || array.length != 2) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numberOfColumns) {
		Control control = getRadioBoxControl(parent);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		control.setLayoutData(gd);
	}

	@Override
	protected void doLoad() {
		String defaultString = getPreferenceStore().getString(getPreferenceName());
		if (CSVConstants.SEMI_COLON.equals(defaultString) || CSVConstants.COLON.equals(defaultString)
				|| CSVConstants.SPACE.equals(defaultString) || CSVConstants.TAB.equals(defaultString)) {
			updateValue(defaultString);
			this.otherFieldDelimiterText.setEditable(false);
			this.otherFieldDelimiterText.setEnabled(false);
		} else {
			updateValue(CSVConstants.OTHER);
			this.otherFieldDelimiterText.setText(defaultString);
			this.otherFieldDelimiterText.setEditable(true);
			this.otherFieldDelimiterText.setEnabled(true);
			this.otherFieldDelimiterText.setFocus();
		}
	}

	@Override
	protected void doLoadDefault() {
		String defaultString = getPreferenceStore().getDefaultString(getPreferenceName());
		if (CSVConstants.SEMI_COLON.equals(defaultString) || CSVConstants.COLON.equals(defaultString)
				|| CSVConstants.SPACE.equals(defaultString) || CSVConstants.TAB.equals(defaultString)) {
			updateValue(defaultString);
			this.otherFieldDelimiterText.setEditable(false);
			this.otherFieldDelimiterText.setEnabled(false);
		} else {
			updateValue(CSVConstants.OTHER);
			this.otherFieldDelimiterText.setText(defaultString);
			this.otherFieldDelimiterText.setEditable(true);
			this.otherFieldDelimiterText.setEnabled(true);
			this.otherFieldDelimiterText.setFocus();
		}
	}

	@Override
	protected void doStore() {
		if (this.value == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}

		if (CSVConstants.SEMI_COLON.equals(this.value) || CSVConstants.COLON.equals(this.value)
				|| CSVConstants.SPACE.equals(this.value) || CSVConstants.TAB.equals(this.value)) {
			getPreferenceStore().setValue(getPreferenceName(), this.value);
		} else {
			getPreferenceStore().setValue(getPreferenceName(), this.otherFieldDelimiterText.getText());
		}
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Returns this field editor's radio group control.
	 * 
	 * @param parent
	 *            The parent to create the radioBox in
	 * @return the radio group control
	 */
	public Composite getRadioBoxControl(Composite parent) {
		if (this.radioBox == null) {

			Font font = parent.getFont();
			Group group = new Group(parent, SWT.NONE);
			group.setFont(font);
			String text = getLabelText();
			if (text != null) {
				group.setText(text);
			}
			this.radioBox = group;
			GridLayout layout = new GridLayout();
			layout.horizontalSpacing = HORIZONTAL_GAP;
			layout.numColumns = this.numRadios + 1;
			this.radioBox.setLayout(layout);

			this.radioButtons = new Button[this.labelsAndValues.length];
			for (int i = 0; i < this.labelsAndValues.length; i++) {
				Button radio = new Button(this.radioBox, SWT.RADIO | SWT.LEFT);
				this.radioButtons[i] = radio;
				String[] labelAndValue = this.labelsAndValues[i];
				radio.setText(labelAndValue[0]);
				radio.setData(labelAndValue[1]);
				radio.setFont(font);
				radio.addSelectionListener(widgetSelectedAdapter(event -> {
					String oldValue = value;
					value = (String) event.widget.getData();
					setPresentsDefaultValue(false);
					if (CSVConstants.OTHER.equals(value)) {
						fireValueChanged(VALUE, oldValue, otherFieldDelimiterText.getText());
						otherFieldDelimiterText.setEditable(true);
						otherFieldDelimiterText.setEnabled(true);
						otherFieldDelimiterText.setFocus();
					} else {
						fireValueChanged(VALUE, oldValue, value);
						otherFieldDelimiterText.setEditable(false);
						otherFieldDelimiterText.setEnabled(false);
					}
				}));
			}
			this.radioBox.addDisposeListener(event -> {
				radioBox = null;
				radioButtons = null;
			});
			this.otherFieldDelimiterText = new Text(this.radioBox, SWT.BORDER);
			this.otherFieldDelimiterText.setTextLimit(1);
			this.otherFieldDelimiterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		} else {
			checkParent(this.radioBox, parent);
		}
		return this.radioBox;
	}

	/**
	 * Select the radio button that conforms to the given value.
	 *
	 * @param selectedValue
	 *            the selected value
	 */
	private void updateValue(String selectedValue) {
		this.value = selectedValue;
		if (radioButtons == null) {
			return;
		}

		if (this.value != null) {
			boolean found = false;
			for (Button radio : this.radioButtons) {
				boolean selection = false;
				if (((String) radio.getData()).equals(this.value)) {
					selection = true;
					found = true;
				}
				radio.setSelection(selection);
			}
			if (found) {
				return;
			}
		}

		// We weren't able to find the value. So we select the first
		// radio button as a default.
		if (this.radioButtons.length > 0) {
			this.radioButtons[0].setSelection(true);
			this.value = (String) this.radioButtons[0].getData();
		}
	}
}
