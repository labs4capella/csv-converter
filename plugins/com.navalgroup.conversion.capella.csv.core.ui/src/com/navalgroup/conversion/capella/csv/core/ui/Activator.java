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

import java.nio.charset.Charset;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author arichard
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "com.navalgroup.conversion.capella.csv.core.ui"; //$NON-NLS-1$

	/** The shared instance. */
	private static Activator plugin;

	/**
	 * The constructor.
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// Preferences initialization must be done here, in the activation of the plugin
		IPreferenceStore store = plugin.getPreferenceStore();
		store.setDefault(CSVConstants.DEFAULT_DIRECTORY_PREF_ID, System.getProperty("user.home"));
		store.setDefault(CSVConstants.DEFAULT_FIELD_DELIMITER_PREF_ID, CSVConstants.SEMI_COLON);
		store.setDefault(CSVConstants.DEFAULT_TEXT_DELIMITER_PREF_ID, CSVConstants.DOUBLE_QUOTE);
		store.setDefault(CSVConstants.DEFAULT_CHARACTER_SET_PREF_ID, Charset.forName("windows-1252").displayName());
		store.setDefault(CSVConstants.DEFAULT_LINE_SEPARATOR_PREF_ID, CSVConstants.WINDOWS);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * logError.
	 * 
	 * @param message
	 *            String
	 */
	public static void logError(String message) {
		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}

	/**
	 * logWarning.
	 * 
	 * @param message
	 *            String
	 */
	public static void logWarning(String message) {
		plugin.getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	}

	/**
	 * logInfo.
	 * 
	 * @param message
	 *            String
	 */
	public static void logInfo(String message) {
		plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	}
}
