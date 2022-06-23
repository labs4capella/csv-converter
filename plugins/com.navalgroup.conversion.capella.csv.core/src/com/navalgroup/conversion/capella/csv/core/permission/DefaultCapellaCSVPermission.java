/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.permission;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;
import org.polarsys.capella.core.data.capellamodeller.util.CapellamodellerResourceImpl;

import com.navalgroup.conversion.capella.csv.core.extension.CapellaCSVPermission;

public class DefaultCapellaCSVPermission implements CapellaCSVPermission {

	@Override
	public LockedInstanceException lockSessionModels(Session session) {
		return null;
	}

	@Override
	public boolean unlockSessionModels(Session session) {
		return true;
	}

	@Override
	public EObject getCapellaRootObject(Session session) {
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource resource : semanticResources) {
			if (resource instanceof CapellamodellerResourceImpl) {
				return resource.getContents().get(0);
			}
		}
		return null;
	}

	@Override
	public String getLockedObjects(LockedInstanceException lie) {
		return null;
	}

	@Override
	public String getConnectedUsers(Session session) {
		return null;
	}
}
