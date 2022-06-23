/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.ui.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.polarsys.capella.core.model.handler.command.CapellaResourceHelper;

public class CapellaProjectPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject && CapellaResourceHelper.isCapellaProject((IProject) receiver)
				&& ((IProject) receiver).isOpen()) {
			return true;
		}
		return false;
	}

}
