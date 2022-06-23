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

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.polarsys.capella.core.sirius.ui.helper.SessionHelper;

import com.navalgroup.conversion.capella.csv.core.extension.CapellaCSVPermission;
import com.navalgroup.conversion.capella.csv.core.extension.CapellaCSVPermissionExtensionRegistry;

public abstract class AbstractCapellaConversionHandler extends AbstractHandler {

	/**
	 * Dialog title.
	 */
	protected static final String DIALOG_TITLE = "Capella conversion";

	/**
	 * Multiple sessions message.
	 */
	protected static final String MULTIPLE_SESSIONS_MESSAGE = "The conversion tool does not handle several sessions in the same project.";

	/**
	 * Session closed message.
	 */
	protected static final String SESSION_CLOSED_MESSAGE = "The session is closed. Please open the session.";

	/**
	 * Empty resource message.
	 */
	protected static final String EMPTY_RESOURCE_MESSAGE = "The root element of the main semantic resource cannot be found.";

	/**
	 * Locked by others message.
	 */
	protected static final String LOCKED_BY_OTHERS_MESSAGE = "Some Capella objects are locked by others users.";

	/**
	 * Unlock failed message.
	 */
	protected static final String UNLOCK_FAILED_MESSAGE = "Some elements have not been unlocked after the conversion. Please unlock manually.";

	protected LockedInstanceException lockSession(Session openedSession) {
		CapellaCSVPermission permissionExtension = CapellaCSVPermissionExtensionRegistry.getHighestPriorityPermission();
		if (permissionExtension != null) {
			return permissionExtension.lockSessionModels(openedSession);
		}
		return null;
	}

	protected boolean unlockSession(Session openedSession) {
		CapellaCSVPermission permissionExtension = CapellaCSVPermissionExtensionRegistry.getHighestPriorityPermission();
		if (permissionExtension != null) {
			boolean sessionLocked = permissionExtension.unlockSessionModels(openedSession);
			if (!sessionLocked) {
				return false;
			}
		}
		return true;
	}

	protected String whoLocksWhat(Session openedSession, LockedInstanceException lie) {
		StringBuilder whoLocksWhat = new StringBuilder();
		CapellaCSVPermission permissionExtension = CapellaCSVPermissionExtensionRegistry.getHighestPriorityPermission();
		if (permissionExtension != null) {
			String lockedObjects = permissionExtension.getLockedObjects(lie);
			String connectedUsers = permissionExtension.getConnectedUsers(openedSession);
			whoLocksWhat.append("All objects must be unlocked before conversion.");
			whoLocksWhat.append(System.lineSeparator());
			whoLocksWhat.append(System.lineSeparator());
			whoLocksWhat.append("The following objects are locked:");
			whoLocksWhat.append(System.lineSeparator());
			whoLocksWhat.append(lockedObjects);
			whoLocksWhat.append(System.lineSeparator());
			whoLocksWhat.append("These objects are locked by:");
			whoLocksWhat.append(System.lineSeparator());
			whoLocksWhat.append(connectedUsers);
		}
		return whoLocksWhat.toString();
	}

	protected IProject getProject(ExecutionEvent event, Shell activeShell) {
		IProject project = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			Object firstElement = ((TreeSelection) selection).getFirstElement();
			if (firstElement instanceof IProject) {
				project = (IProject) firstElement;
			}
		}
		if (project == null) {
			MessageDialog.openError(activeShell, DIALOG_TITLE, "The project is not opened.");
		}
		return project;
	}

	protected Session getOpenedSession(IProject project, Shell shell) {
		boolean oneOpenedSession = false;
		Session session = null;
		Collection<Session> existingSessions = SessionHelper.getExistingSessions(project);
		if (existingSessions.isEmpty()) {
			MessageDialog.openError(shell, DIALOG_TITLE, SESSION_CLOSED_MESSAGE);
		} else if (existingSessions.size() > 1) {
			MessageDialog.openError(shell, DIALOG_TITLE, MULTIPLE_SESSIONS_MESSAGE);
		} else {
			oneOpenedSession = true;
		}
		if (oneOpenedSession) {
			for (Session existingSession : existingSessions) {
				if (!existingSession.isOpen()) {
					MessageDialog.openError(shell, DIALOG_TITLE, SESSION_CLOSED_MESSAGE);
					break;
				} else {
					session = existingSession;
					break;
				}
			}
		}
		return session;
	}

	protected EObject getCapellaRootObject(Session session) {
		CapellaCSVPermission highestPriorityPermission = CapellaCSVPermissionExtensionRegistry
				.getHighestPriorityPermission();
		if (highestPriorityPermission != null) {
			return highestPriorityPermission.getCapellaRootObject(session);
		}
		return null;
	}
}
