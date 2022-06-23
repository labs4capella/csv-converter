/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.export.ui.handlers;

import java.nio.charset.Charset;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.ui.handlers.AbstractCapellaConversionHandler;
import com.navalgroup.conversion.capella.csv.export.relevant.concepts.CapellaRelevantConceptsToCSVJob;
import com.navalgroup.conversion.capella.csv.export.ui.Activator;
import com.navalgroup.conversion.capella.csv.export.ui.CapellaToCSVDialog;

/**
 * Capella Relevant Concepts to CSV Handler.
 * 
 * @author nlepine
 *
 */
public class CapellaRelevantConceptsToCSVHandler extends AbstractCapellaConversionHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		IProject project = getProject(event, activeShell);
		if (project != null) {
			Session openedSession = getOpenedSession(project, activeShell);
			if (openedSession != null) {
				EObject rootObject = getCapellaRootObject(openedSession);
				if (rootObject != null) {
					LockedInstanceException lockedByOthers = lockSession(openedSession);
					if (lockedByOthers == null) {
						callDialog(activeShell, openedSession, rootObject);
					} else {
						MessageDialog.openError(activeShell, DIALOG_TITLE, whoLocksWhat(openedSession, lockedByOthers));
					}
				} else {
					MessageDialog.openError(activeShell, DIALOG_TITLE, EMPTY_RESOURCE_MESSAGE);
				}
			}
		}
		return null;
	}

	protected void callDialog(Shell activeShell, Session openedSession, EObject rootObject) {
		CapellaToCSVDialog dialog = new CapellaToCSVDialog(activeShell);
		int returnButton = dialog.open();
		if (returnButton == IDialogConstants.OK_ID) {
			String exportDirectoryPath = dialog.getImportExportDirectoryPath();
			Character fieldDelimiter = dialog.getFieldDelimiter();
			Character textDelimiter = dialog.getTextDelimiter();
			Charset characterSet = dialog.getCharacterSet();
			String lineSeparator = dialog.getLineSeparator();
			CSVSettings csvSettings = new CSVSettings(exportDirectoryPath, fieldDelimiter, textDelimiter, characterSet,
					lineSeparator);
			CapellaRelevantConceptsToCSVJob job = new CapellaRelevantConceptsToCSVJob(rootObject, csvSettings, false);
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(activeShell);
			try {
				pmd.run(true, true, job);
				// CHECKSTYLE:OFF
			} catch (Exception e) {
				Activator.logError(e.getMessage());
			}
			IStatus status = job.getStatus();
			if (status != null && !status.isOK()) {
				MessageDialog.openError(activeShell, DIALOG_TITLE, status.getMessage());
			} else {
				Activator.logInfo("Capella Conversion: CSV files imported with success!");
			}
		}
		boolean sessionUnlocked = unlockSession(openedSession);
		if (!sessionUnlocked) {
			MessageDialog.openError(activeShell, DIALOG_TITLE, UNLOCK_FAILED_MESSAGE);
		}
	}
}
