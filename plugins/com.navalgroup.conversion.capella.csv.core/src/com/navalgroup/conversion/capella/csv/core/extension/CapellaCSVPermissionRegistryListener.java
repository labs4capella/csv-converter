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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;

public class CapellaCSVPermissionRegistryListener implements IRegistryEventListener {

	/** Name of the extension point to parse for extensions. */
	public static final String PERMISSION_EXTENSION_POINT = "com.navalgroup.conversion.capella.csv.core.permission"; //$NON-NLS-1$

	/** ID of the extension point. */
	private static final String PERMISSION_TAG_EXTENSION = "capellaCSVPermission"; //$NON-NLS-1$

	/**
	 * Init.
	 */
	public void init() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addListener(this, PERMISSION_EXTENSION_POINT);
		parseInitialContributions();
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.removeListener(this);
		CapellaCSVPermissionExtensionRegistry.clearRegistry();
	}

	@Override
	public void added(IExtensionPoint[] extensionPoints) {
		// no need to listen to this event
	}

	@Override
	public void removed(IExtensionPoint[] extensionPoints) {
		// no need to listen to this event
	}

	@Override
	public void added(IExtension[] extensions) {
		for (IExtension extension : extensions) {
			parseExtension(extension);
		}
	}

	/**
	 * Parse contributions.
	 */
	public void parseInitialContributions() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();

		for (IExtension extension : registry.getExtensionPoint(PERMISSION_EXTENSION_POINT).getExtensions()) {
			parseExtension(extension);
		}
	}

	@Override
	public void removed(IExtension[] extensions) {
		for (IExtension extension : extensions) {
			final IConfigurationElement[] configElements = extension.getConfigurationElements();
			for (IConfigurationElement elem : configElements) {
				if (PERMISSION_TAG_EXTENSION.equals(elem.getName())) {
					final String extensionClassName = elem
							.getAttribute(CapellaCSVPermissionExtensionDescriptor.PERMISSION_ATTRIBUTE);
					CapellaCSVPermissionExtensionRegistry.removeExtension(extensionClassName);
				}
			}
		}
	}

	private void parseExtension(IExtension extension) {
		final IConfigurationElement[] configElements = extension.getConfigurationElements();
		for (IConfigurationElement elem : configElements) {
			if (PERMISSION_TAG_EXTENSION.equals(elem.getName())) {

				try {
					CapellaCSVPermissionExtensionRegistry
							.addExtension(new CapellaCSVPermissionExtensionDescriptor(elem));
				} catch (IllegalArgumentException e) {
				}
			}
		}
	}
}
