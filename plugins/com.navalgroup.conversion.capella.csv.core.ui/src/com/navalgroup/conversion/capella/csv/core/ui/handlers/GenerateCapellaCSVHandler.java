/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.jobs.GenerateCapellaMetamodelCSVJob;
import com.navalgroup.conversion.capella.csv.core.ui.Activator;
import com.navalgroup.conversion.capella.csv.core.ui.GenerateEmptyCSVDialog;

/**
 * Generate Capella Metamodel CSV Handler.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class GenerateCapellaCSVHandler extends AbstractCapellaConversionHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		IProject project = getProject(event, activeShell);
		if (project != null) {
			callDialog(activeShell);
		}
		return null;
	}

	protected void callDialog(Shell activeShell) {
		GenerateEmptyCSVDialog dialog = new GenerateEmptyCSVDialog(activeShell);
		int returnButton = dialog.open();
		if (returnButton == IDialogConstants.OK_ID) {
			String exportDirectoryPath = dialog.getImportExportDirectoryPath();
			Character fieldDelimiter = dialog.getFieldDelimiter();
			Character textDelimiter = dialog.getTextDelimiter();
			Charset characterSet = dialog.getCharacterSet();
			String lineSeparator = dialog.getLineSeparator();
			CSVSettings csvSettings = new CSVSettings(exportDirectoryPath, fieldDelimiter, textDelimiter, characterSet,
					lineSeparator);
			GenerateCapellaMetamodelCSVJob job = new GenerateCapellaMetamodelCSVJob(csvSettings);

			ProgressMonitorDialog pmd = new ProgressMonitorDialog(activeShell);
			try {
				pmd.run(true, true, job);
			} catch (InvocationTargetException | InterruptedException e) {
				Activator.logError(e.getMessage());
			}
		}
	}
}
