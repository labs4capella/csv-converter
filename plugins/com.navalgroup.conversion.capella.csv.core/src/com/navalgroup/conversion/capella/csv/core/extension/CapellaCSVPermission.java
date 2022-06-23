/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.extension;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;

public interface CapellaCSVPermission {

	/**
	 * Lock all semantic elements in the session.
	 * 
	 * @param session
	 *            The Sirius session.
	 * @return <code>null</code> if all elements have been locked, a
	 *         <code>LockedInstanceException</code> otherwise.
	 */
	LockedInstanceException lockSessionModels(Session session);

	/**
	 * Unlock all semantic elements in the session.
	 * 
	 * @param session
	 *            The Sirius session.
	 * @return <code>true</code> if all elements have been unlocked,
	 *         <code>false</code> otherwise.
	 */
	boolean unlockSessionModels(Session session);

	/**
	 * Get the Capella root object of the given session.
	 * 
	 * @param session
	 *            The Sirius session.
	 * @return the Capella root object of the given session.
	 */
	EObject getCapellaRootObject(Session session);

	/**
	 * Returns the list of locked objects by others.
	 * 
	 * @param lie
	 *            The {@link LockedInstanceException} containing the locked data.
	 * @return the list of locked objects by others.
	 */
	String getLockedObjects(LockedInstanceException lie);

	/**
	 * Returns the list of connected users to the given session.
	 * 
	 * @param session
	 *            The Sirius session.
	 * @return the list of connected users to the given session.
	 */
	String getConnectedUsers(Session session);

}
