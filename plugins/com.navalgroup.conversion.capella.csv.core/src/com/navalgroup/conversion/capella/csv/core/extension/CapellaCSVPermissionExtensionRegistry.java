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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class CapellaCSVPermissionExtensionRegistry {

	/**
	 * CapellaCSVPermissionExtensionDescriptor list.
	 */
	private static Collection<CapellaCSVPermissionExtensionDescriptor> extensions = new HashSet<>();

	/**
	 * Utility classes don't need a default constructor.
	 */
	private CapellaCSVPermissionExtensionRegistry() {
		// Prevent instantiation
	}

	/**
	 * Add extension.
	 * 
	 * @param extension
	 *            CapellaCSVPermissionExtensionDescriptor
	 */
	public static void addExtension(CapellaCSVPermissionExtensionDescriptor extension) {
		extensions.add(extension);
	}

	/**
	 * Remove extension.
	 * 
	 * @param extensionClassName
	 *            String
	 */
	public static void removeExtension(String extensionClassName) {
		Collection<CapellaCSVPermissionExtensionDescriptor> values = new HashSet<>(extensions);
		for (CapellaCSVPermissionExtensionDescriptor extension : values) {
			if (extension.getExtensionClassName().equals(extensionClassName)) {
				extensions.remove(extension);
			}
		}
	}

	/**
	 * Returns a copy of the registered extensions list.
	 * 
	 * @return A copy of the registered extensions list.
	 */
	public static Collection<CapellaCSVPermissionExtensionDescriptor> getRegisteredExtensions() {
		Set<CapellaCSVPermissionExtensionDescriptor> registeredExtensions = new LinkedHashSet<>();
		for (CapellaCSVPermissionExtensionDescriptor extension : extensions) {
			registeredExtensions.add(extension);
		}
		return registeredExtensions;
	}

	/**
	 * Returns a copy of the highest priority registered extension.
	 * 
	 * @return A copy of the the highest priority registered extension.
	 */
	public static CapellaCSVPermissionExtensionDescriptor getHighestPriorityRegisteredExtension() {
		CapellaCSVPermissionExtensionDescriptor highestPriorityExtension = null;
		Integer highestPriority = -1;
		for (CapellaCSVPermissionExtensionDescriptor extension : extensions) {
			Integer priority = extension.getPriority();
			if (priority > highestPriority) {
				highestPriority = priority;
				highestPriorityExtension = extension;
			}
		}
		return highestPriorityExtension;
	}

	/**
	 * Returns a copy of the highest priority registered extension.
	 * 
	 * @return A copy of the the highest priority registered extension.
	 */
	public static CapellaCSVPermission getHighestPriorityPermission() {
		CapellaCSVPermissionExtensionDescriptor highestPriorityExtension = getHighestPriorityRegisteredExtension();
		if (highestPriorityExtension != null) {
			return highestPriorityExtension.getPermission();
		}
		return null;
	}

	/**
	 * Clear registry.
	 */
	public static void clearRegistry() {
		extensions.clear();
	}
}
