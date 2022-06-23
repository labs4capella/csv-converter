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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class CapellaCSVPermissionExtensionDescriptor {

	/** Name of the lock strategy extension point's tag "permission" attribute. */
	public static final String PERMISSION_ATTRIBUTE = "permission"; //$NON-NLS-1$

	/** Name of the lock strategy extension point's tag "permission" attribute. */
	public static final String PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$

	/** Configuration element of this descriptor. */
	private final IConfigurationElement element;

	/**
	 * Qualified class name of the permission extension.
	 */
	private final String extensionClassName;

	/**
	 * Priority attribute of the permission extension.
	 */
	private final String priority;

	/**
	 * We only need to create the instance once, this will keep reference to it.
	 */
	private CapellaCSVPermission extension;

	/**
	 * Instantiates a descriptor with all information.
	 * 
	 * @param configuration
	 *            Configuration element from which to create this descriptor.
	 */
	public CapellaCSVPermissionExtensionDescriptor(IConfigurationElement configuration) {
		this.element = configuration;
		this.extensionClassName = configuration.getAttribute(PERMISSION_ATTRIBUTE);
		this.priority = configuration.getAttribute(PRIORITY_ATTRIBUTE);
	}

	/**
	 * Returns this descriptor's "extension" class name.
	 * 
	 * @return This descriptor's "extension" class name.
	 */
	public String getExtensionClassName() {
		return this.extensionClassName;
	}

	/**
	 * Returns the priority of the contributed permission.
	 * 
	 * @return the priority of the contributed permission.
	 */
	public Integer getPriority() {
		Integer priorityValue;
		try {
			priorityValue = Integer.valueOf(this.priority);
		} catch (NumberFormatException e) {
			priorityValue = -1;
		}
		return priorityValue;
	}

	/**
	 * Creates an instance of this descriptor's {@link CapellaCSVPermission} .
	 * 
	 * @return A new instance of this descriptor's {@link CapellaCSVPermission}.
	 */
	public CapellaCSVPermission getPermission() {
		if (this.extension == null) {
			try {
				this.extension = (CapellaCSVPermission) element.createExecutableExtension(PERMISSION_ATTRIBUTE);
			} catch (CoreException e) {
			}
		}
		return this.extension;
	}
}
