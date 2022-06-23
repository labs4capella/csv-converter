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

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;

/**
 * Dialog displayed to users when importing/exporting Capella/CSV.
 * 
 * @author arichard
 *
 */
public abstract class AbstractCSVConversionDialog extends TitleAreaDialog {

	/**
	 * lastImportExportDirectory.
	 */
	private static String lastImportExportDirectory;

	/**
	 * importExportPathText.
	 */
	private Text importExportPathText;
	/**
	 * importExportPathBrowseButton.
	 */
	private Button importExportPathBrowseButton;
	/**
	 * fieldDelimiterGroup.
	 */
	private Group fieldDelimiterGroup;
	/**
	 * colonRadio.
	 */
	private Button colonRadio;
	/**
	 * semiColonRadio.
	 */
	private Button semiColonRadio;
	/**
	 * spaceRadio.
	 */
	private Button spaceRadio;
	/**
	 * tabRadio.
	 */
	private Button tabRadio;
	/**
	 * otherRadio.
	 */
	private Button otherRadio;
	/**
	 * otherDelimiterText.
	 */
	private Text otherDelimiterText;
	/**
	 * textDelimiterCombo.
	 */
	private Combo textDelimiterCombo;
	/**
	 * characterSetCombo.
	 */
	private Combo characterSetCombo;
	/**
	 * lineSeparatorCombo.
	 */
	private Combo lineSeparatorCombo;

	/**
	 * dialogName.
	 */
	private String dialogName;
	/**
	 * dialogMessage.
	 */
	private String dialogMessage;
	/**
	 * actionLabel.
	 */
	private String actionLabel;
	/**
	 * importExportDirectoryPath.
	 */
	private String importExportDirectoryPath;
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
	 * store.
	 */
	private IPreferenceStore store;

	/**
	 * okButton.
	 */
	private Button okButton;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param dialogName
	 * @param dialogMessage
	 * @param okButtonLabel
	 */
	public AbstractCSVConversionDialog(Shell parentShell, String dialogName, String dialogMessage,
			String okButtonLabel) {
		super(parentShell);
		this.dialogName = dialogName;
		this.dialogMessage = dialogMessage;
		this.actionLabel = okButtonLabel;
		this.store = Activator.getDefault().getPreferenceStore();
		setHelpAvailable(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle(this.dialogName);
		setMessage(this.dialogMessage, IMessageProvider.INFORMATION);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite mainContainer = new Composite(area, SWT.NONE);
		mainContainer.setLayout(new GridLayout(3, false));
		GridData mainContainerGridData = new GridData(GridData.FILL_BOTH);
		mainContainerGridData.heightHint = 199;
		mainContainer.setLayoutData(mainContainerGridData);

		Label importExportPathLabel = new Label(mainContainer, SWT.NONE);
		importExportPathLabel.setText(this.actionLabel + " directory:");

		this.importExportPathText = new Text(mainContainer, SWT.BORDER);
		this.importExportPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.importExportPathBrowseButton = new Button(mainContainer, SWT.NONE);
		this.importExportPathBrowseButton.setText("Browse...");

		this.fieldDelimiterGroup = new Group(mainContainer, SWT.NONE);
		this.fieldDelimiterGroup.setLayout(new GridLayout(6, false));
		this.fieldDelimiterGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		this.fieldDelimiterGroup.setText("Field delimiter:");

		this.semiColonRadio = new Button(this.fieldDelimiterGroup, SWT.RADIO);
		this.semiColonRadio.setText(CSVConstants.SEMI_COLON);

		this.colonRadio = new Button(this.fieldDelimiterGroup, SWT.RADIO);
		this.colonRadio.setText(CSVConstants.COLON);

		this.spaceRadio = new Button(this.fieldDelimiterGroup, SWT.RADIO);
		this.spaceRadio.setText(CSVConstants.SPACE);

		this.tabRadio = new Button(this.fieldDelimiterGroup, SWT.RADIO);
		this.tabRadio.setText(CSVConstants.TAB);

		this.otherRadio = new Button(this.fieldDelimiterGroup, SWT.RADIO);
		this.otherRadio.setText(CSVConstants.OTHER);

		this.otherDelimiterText = new Text(this.fieldDelimiterGroup, SWT.BORDER);
		this.otherDelimiterText.setTextLimit(1);
		this.otherDelimiterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label textDelimiterLabel = new Label(mainContainer, SWT.NONE);
		textDelimiterLabel.setText("Text delimiter:");

		this.textDelimiterCombo = new Combo(mainContainer, SWT.READ_ONLY);
		this.textDelimiterCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		Label characterSetLabel = new Label(mainContainer, SWT.NONE);
		characterSetLabel.setText("Character set:");

		this.characterSetCombo = new Combo(mainContainer, SWT.READ_ONLY);
		this.characterSetCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		Label lineSeparatorLabel = new Label(mainContainer, SWT.NONE);
		lineSeparatorLabel.setText("Line separator:");

		this.lineSeparatorCombo = new Combo(mainContainer, SWT.READ_ONLY);
		this.lineSeparatorCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		return area;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// init widgets only after creating the buttons for button bar
		initWidgets();
		return contents;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		this.okButton.setText(this.actionLabel);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		Button restoreDefaults = createButton(parent, 1081986, "Restore Defaults", false);

		restoreDefaults.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				restoreDefaultDirectory();
				restoreDefaultFieldDelimiter();
				restoreDefaultTextDelimiter();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void restoreDefaultTextDelimiter() {
		String defaultTextDelimiter = store.getString(CSVConstants.DEFAULT_TEXT_DELIMITER_PREF_ID);
		if (CSVConstants.NONE.equals(defaultTextDelimiter)) {
			textDelimiterCombo.select(textDelimiterCombo.indexOf(CSVConstants.NONE_DELIMITER));
		} else if (CSVConstants.SIMPLE_QUOTE.equals(defaultTextDelimiter)) {
			textDelimiterCombo.select(textDelimiterCombo.indexOf(CSVConstants.SIMPLE_QUOTE_DELIMITER));
		} else {
			textDelimiterCombo.select(textDelimiterCombo.indexOf(CSVConstants.DOUBLE_QUOTE_DELIMITER));
		}
		setTextDelimiter(defaultTextDelimiter);

		String defaultCharacterSet = store.getString(CSVConstants.DEFAULT_CHARACTER_SET_PREF_ID);
		characterSetCombo.select(characterSetCombo.indexOf(defaultCharacterSet));
		setCharacterSet(defaultCharacterSet);

		String defaultLineSeparator = store.getString(CSVConstants.DEFAULT_LINE_SEPARATOR_PREF_ID);
		lineSeparatorCombo.select(lineSeparatorCombo.indexOf(defaultLineSeparator));
		setLineSeparator(defaultLineSeparator);
		isValid();
	}

	private void restoreDefaultFieldDelimiter() {
		String defaultFieldDelimiter = store.getString(CSVConstants.DEFAULT_FIELD_DELIMITER_PREF_ID);
		switch (defaultFieldDelimiter) {
		case CSVConstants.SEMI_COLON:
			semiColonRadio.setSelection(true);
			colonRadio.setSelection(false);
			spaceRadio.setSelection(false);
			tabRadio.setSelection(false);
			otherRadio.setSelection(false);
			otherDelimiterText.setEnabled(false);
			otherDelimiterText.setEditable(false);
			break;
		case CSVConstants.COLON:
			semiColonRadio.setSelection(false);
			colonRadio.setSelection(true);
			spaceRadio.setSelection(false);
			tabRadio.setSelection(false);
			otherRadio.setSelection(false);
			otherDelimiterText.setEnabled(false);
			otherDelimiterText.setEditable(false);
			break;
		case CSVConstants.SPACE:
			semiColonRadio.setSelection(false);
			colonRadio.setSelection(false);
			spaceRadio.setSelection(true);
			tabRadio.setSelection(false);
			otherRadio.setSelection(false);
			otherDelimiterText.setEnabled(false);
			otherDelimiterText.setEditable(false);
			break;
		case CSVConstants.TAB:
			semiColonRadio.setSelection(false);
			colonRadio.setSelection(false);
			spaceRadio.setSelection(false);
			tabRadio.setSelection(true);
			otherRadio.setSelection(false);
			otherDelimiterText.setEnabled(false);
			otherDelimiterText.setEditable(false);
			break;
		default:
			semiColonRadio.setSelection(false);
			colonRadio.setSelection(false);
			spaceRadio.setSelection(false);
			tabRadio.setSelection(false);
			otherRadio.setSelection(true);
			otherDelimiterText.setEnabled(true);
			otherDelimiterText.setEditable(true);
			otherDelimiterText.setText(defaultFieldDelimiter);
			break;
		}
		setFieldDelimiter(defaultFieldDelimiter);
	}

	private void restoreDefaultDirectory() {
		String defaultExportDirectory = store.getString(CSVConstants.DEFAULT_DIRECTORY_PREF_ID);
		importExportPathText.setText(defaultExportDirectory);
		setImportExportDirectoryPath(defaultExportDirectory);
		handleValidDirectory(defaultExportDirectory);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(650, 500);
	}

	protected void initWidgets() {
		initExportDirectory();
		initFieldDelimiter();
		initTextDelimiter();
		initCharacterSet();
		initLineSeparator();
	}

	protected void initExportDirectory() {
		DirectoryDialog dlg = new DirectoryDialog(getParentShell());
		dlg.setText("Capella CSV conversion");
		dlg.setMessage("Select the " + this.actionLabel + " directory");
		if (lastImportExportDirectory != null) {
			this.importExportPathText.setText(lastImportExportDirectory);
			setImportExportDirectoryPath(lastImportExportDirectory);
			handleValidDirectory(lastImportExportDirectory);
		} else {
			String defaultExportDirectory = this.store.getString(CSVConstants.DEFAULT_DIRECTORY_PREF_ID);
			if (defaultExportDirectory != null && !defaultExportDirectory.isEmpty()) {
				this.importExportPathText.setText(defaultExportDirectory);
				setImportExportDirectoryPath(defaultExportDirectory);
				dlg.setFilterPath(defaultExportDirectory);
			}
			isValid();
		}
		this.importExportPathBrowseButton.addSelectionListener(widgetSelectedAdapter(event -> {
			String selectedDirectory = dlg.open();
			if (selectedDirectory != null) {
				importExportPathText.setText(selectedDirectory);
				setImportExportDirectoryPath(selectedDirectory);
				setLastImportExportDirectoryPath(selectedDirectory);
			}
			isValid();
		}));

		this.importExportPathText.addModifyListener(e -> {
			Text source = (Text) e.getSource();
			String directoryPath = source.getText();
			setImportExportDirectoryPath(directoryPath);
			setLastImportExportDirectoryPath(directoryPath);
			isValid();
		});
	}

	protected boolean isValid() {
		boolean valid = true;
		boolean validDirectory = handleValidDirectory(getImportExportDirectoryPath());
		if (!validDirectory) {
			valid = false;
			this.okButton.setEnabled(false);
		}
		boolean validFieldDelimter = handleValidFieldDelimiter(getFieldDelimiter());
		if (!validFieldDelimter) {
			valid = false;
			this.okButton.setEnabled(false);
		}
		if (valid) {
			this.setErrorMessage(null);
			this.okButton.setEnabled(true);
		}
		return valid;
	}

	protected boolean handleValidFieldDelimiter(Character delimiter) {
		if (delimiter != null) {
			return true;
		} else {
			AbstractCSVConversionDialog.this.setErrorMessage("This field delimiter is not allowed");
			return false;
		}
	}

	protected boolean handleValidDirectory(String directoryPath) {
		if (directoryPath != null && Files.isDirectory(Paths.get(directoryPath), LinkOption.values())) {
			return true;
		} else {
			AbstractCSVConversionDialog.this.setErrorMessage("The directory does not exist");
			return false;
		}
	}

	protected void initFieldDelimiter() {
		initFieldDelimiterSemiColon();

		initFieldDelimiterColon();

		initFieldDelimiterSpace();

		initFieldDelimiterTab();

		initFieldDelimiterOther();

		String defaultFieldDelimiter = this.store.getString(CSVConstants.DEFAULT_FIELD_DELIMITER_PREF_ID);
		switch (defaultFieldDelimiter) {
		case CSVConstants.SEMI_COLON:
			this.semiColonRadio.setSelection(true);
			this.otherDelimiterText.setEditable(false);
			this.otherDelimiterText.setEnabled(false);
			break;
		case CSVConstants.COLON:
			this.colonRadio.setSelection(true);
			this.otherDelimiterText.setEditable(false);
			this.otherDelimiterText.setEnabled(false);
			break;
		case CSVConstants.SPACE:
			this.spaceRadio.setSelection(true);
			this.otherDelimiterText.setEditable(false);
			this.otherDelimiterText.setEnabled(false);
			break;
		case CSVConstants.TAB:
			this.tabRadio.setSelection(true);
			this.otherDelimiterText.setEditable(false);
			this.otherDelimiterText.setEnabled(false);
			break;
		default:
			this.otherRadio.setSelection(true);
			this.otherDelimiterText.setEditable(true);
			this.otherDelimiterText.setEnabled(true);
			this.otherDelimiterText.setText(defaultFieldDelimiter);
			break;
		}
		setFieldDelimiter(defaultFieldDelimiter);
		isValid();
	}

	private void initFieldDelimiterOther() {
		this.otherRadio.addSelectionListener(widgetSelectedAdapter(e -> {
			Button source = (Button) e.getSource();
			if (source.getSelection()) {
				otherDelimiterText.setEditable(true);
				otherDelimiterText.setEnabled(true);
				otherDelimiterText.setFocus();
				String delimiter = otherDelimiterText.getText();
				if (delimiter != null && delimiter.length() == 1
						&& ConversionUtil.isAllowedFieldDelimiter(delimiter.charAt(0))) {
					setFieldDelimiter(delimiter);
				} else {
					setFieldDelimiter(null);
				}
				isValid();
			}
		}));
		this.otherDelimiterText.addModifyListener(e -> {
			Text source = (Text) e.getSource();
			String delimiter = source.getText();
			if (delimiter != null && delimiter.length() == 1
					&& ConversionUtil.isAllowedFieldDelimiter(delimiter.charAt(0))) {
				setFieldDelimiter(delimiter);
			} else {
				setFieldDelimiter(null);
			}
			isValid();
		});
	}

	private void initFieldDelimiterTab() {
		this.tabRadio.addSelectionListener(widgetSelectedAdapter(e -> {
			Button source = (Button) e.getSource();
			if (source.getSelection()) {
				otherDelimiterText.setEditable(false);
				otherDelimiterText.setEnabled(false);
				setFieldDelimiter(CSVConstants.TAB);
				isValid();
			}
		}));
	}

	private void initFieldDelimiterSpace() {
		this.spaceRadio.addSelectionListener(widgetSelectedAdapter(e -> {
			Button source = (Button) e.getSource();
			if (source.getSelection()) {
				otherDelimiterText.setEditable(false);
				otherDelimiterText.setEnabled(false);
				setFieldDelimiter(CSVConstants.SPACE);
				isValid();
			}
		}));
	}

	private void initFieldDelimiterColon() {
		this.colonRadio.addSelectionListener(widgetSelectedAdapter(e -> {
			Button source = (Button) e.getSource();
			if (source.getSelection()) {
				otherDelimiterText.setEditable(false);
				otherDelimiterText.setEnabled(false);
				setFieldDelimiter(CSVConstants.COLON);
				isValid();
			}
		}));
	}

	private void initFieldDelimiterSemiColon() {
		this.semiColonRadio.addSelectionListener(widgetSelectedAdapter(e -> {
			Button source = (Button) e.getSource();
			if (source.getSelection()) {
				otherDelimiterText.setEditable(false);
				otherDelimiterText.setEnabled(false);
				setFieldDelimiter(CSVConstants.SEMI_COLON);
				isValid();
			}
		}));
	}

	protected void initTextDelimiter() {
		this.textDelimiterCombo.setItems(CSVConstants.TEXT_DELIMITER_ITEMS);
		String defaultTextDelimiter = this.store.getString(CSVConstants.DEFAULT_TEXT_DELIMITER_PREF_ID);
		if (CSVConstants.NONE.equals(defaultTextDelimiter)) {
			this.textDelimiterCombo.select(this.textDelimiterCombo.indexOf(CSVConstants.NONE_DELIMITER));
		} else if (CSVConstants.SIMPLE_QUOTE.equals(defaultTextDelimiter)) {
			this.textDelimiterCombo.select(this.textDelimiterCombo.indexOf(CSVConstants.SIMPLE_QUOTE_DELIMITER));
		} else {
			this.textDelimiterCombo.select(this.textDelimiterCombo.indexOf(CSVConstants.DOUBLE_QUOTE_DELIMITER));
		}
		setTextDelimiter(defaultTextDelimiter);
		this.textDelimiterCombo.addSelectionListener(widgetSelectedAdapter(e -> {
			Combo source = (Combo) e.getSource();
			String item = source.getItem(source.getSelectionIndex());
			setTextDelimiter(item);
		}));
	}

	protected void initCharacterSet() {
		this.characterSetCombo.setItems(CSVConstants.CHARACTER_SET_ITEMS);
		String defaultCharacterSet = this.store.getString(CSVConstants.DEFAULT_CHARACTER_SET_PREF_ID);
		this.characterSetCombo.select(this.characterSetCombo.indexOf(defaultCharacterSet));
		setCharacterSet(defaultCharacterSet);
		this.characterSetCombo.addSelectionListener(widgetSelectedAdapter(e -> {
			Combo source = (Combo) e.getSource();
			String item = source.getItem(source.getSelectionIndex());
			setCharacterSet(item);
		}));
	}

	protected void initLineSeparator() {
		this.lineSeparatorCombo.setItems(CSVConstants.LINE_SEPARATOR_ITEMS);
		String defaultLineSeparator = this.store.getString(CSVConstants.DEFAULT_LINE_SEPARATOR_PREF_ID);
		this.lineSeparatorCombo.select(this.lineSeparatorCombo.indexOf(defaultLineSeparator));
		setLineSeparator(defaultLineSeparator);
		this.lineSeparatorCombo.addSelectionListener(widgetSelectedAdapter(e -> {
			Combo source = (Combo) e.getSource();
			String item = source.getItem(source.getSelectionIndex());
			setLineSeparator(item);
		}));
	}

	public String getImportExportDirectoryPath() {
		return this.importExportDirectoryPath;
	}

	protected void setImportExportDirectoryPath(String absolutePath) {
		this.importExportDirectoryPath = absolutePath;
	}

	protected static void setLastImportExportDirectoryPath(String absolutePath) {
		lastImportExportDirectory = absolutePath;
	}

	public Character getFieldDelimiter() {
		return this.fieldDelimiter;
	}

	protected void setFieldDelimiter(String delimiter) {
		if (CSVConstants.SEMI_COLON.equals(delimiter)
				|| CSVConstants.SEMI_COLON_DELIMITER.toString().equals(delimiter)) {
			this.fieldDelimiter = CSVConstants.SEMI_COLON_DELIMITER;
		} else if (CSVConstants.COLON.equals(delimiter) || CSVConstants.COLON_DELIMITER.toString().equals(delimiter)) {
			this.fieldDelimiter = CSVConstants.COLON_DELIMITER;
		} else if (CSVConstants.SPACE.equals(delimiter) || CSVConstants.SPACE_DELIMITER.toString().equals(delimiter)) {
			this.fieldDelimiter = CSVConstants.SPACE_DELIMITER;
		} else if (CSVConstants.TAB.equals(delimiter) || CSVConstants.TAB_DELIMITER.toString().equals(delimiter)) {
			this.fieldDelimiter = CSVConstants.TAB_DELIMITER;
		} else if (delimiter != null && delimiter.length() == 1) {
			this.fieldDelimiter = delimiter.charAt(0);
		} else {
			this.fieldDelimiter = null;
		}
	}

	public Character getTextDelimiter() {
		return this.textDelimiter;
	}

	protected void setTextDelimiter(String delimiter) {
		if (CSVConstants.DOUBLE_QUOTE.equals(delimiter) || CSVConstants.DOUBLE_QUOTE_DELIMITER.equals(delimiter)) {
			this.textDelimiter = CSVConstants.DOUBLE_QUOTE_DELIMITER.charAt(0);
		} else if (CSVConstants.SIMPLE_QUOTE.equals(delimiter)
				|| CSVConstants.SIMPLE_QUOTE_DELIMITER.equals(delimiter)) {
			this.textDelimiter = CSVConstants.SIMPLE_QUOTE_DELIMITER.charAt(0);
		} else {
			this.textDelimiter = null;
		}
	}

	public Charset getCharacterSet() {
		return this.characterSet;
	}

	protected void setCharacterSet(String charSet) {
		this.characterSet = Charset.forName(charSet);
	}

	public String getLineSeparator() {
		return this.lineSeparator;
	}

	protected void setLineSeparator(String lineSeparator) {
		if (CSVConstants.WINDOWS.equals(lineSeparator)) {
			this.lineSeparator = CSVConstants.WIN_LINE_SEPARATOR;
		} else if (CSVConstants.UNIX.equals(lineSeparator)) {
			this.lineSeparator = CSVConstants.UNIX_LINE_SEPARATOR;
		} else {
			this.lineSeparator = CSVConstants.SYSTEM_LINE_SEPARATOR;
		}
	}
}
