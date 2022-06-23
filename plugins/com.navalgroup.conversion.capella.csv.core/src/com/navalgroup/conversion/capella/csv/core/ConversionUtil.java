/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ecore.extender.business.api.accessor.exception.MetaClassNotFoundException;
import org.polarsys.capella.common.ui.toolkit.browser.category.CategoryRegistry;
import org.polarsys.capella.common.ui.toolkit.browser.category.ICategory;
import org.polarsys.capella.common.ui.toolkit.browser.content.provider.IBrowserContentProvider;
import org.polarsys.capella.core.data.capellacore.Structure;
import org.polarsys.capella.core.data.capellamodeller.Project;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;

/**
 * Utility methods to navigate through EMF models.
 * 
 * @author arichard
 *
 */
public final class ConversionUtil {

	private ConversionUtil() {
	}

	/**
	 * Returns all attributes of the given {@link EObject}. The derived, and
	 * volatile attributes are excluded from the results.
	 * 
	 * @param object
	 *            the given {@link EObject}.
	 * @return a map with attributes names as map-keys and attributes values as
	 *         map-values.
	 */
	public static Map<String, Object> getAttributes(EObject object) {
		if (object == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = new HashMap<>();
		EList<EAttribute> eAllAttributes = object.eClass().getEAllAttributes();
		for (EAttribute eAttribute : eAllAttributes) {
			if (!eAttribute.isDerived() && !eAttribute.isVolatile()) {
				map.put(eAttribute.getName(), object.eGet(eAttribute));
			}
		}
		return map;
	}

	/**
	 * Returns all attributes of the given {@link EClass}. The derived, and volatile
	 * attributes are excluded from the results.
	 * 
	 * @param eClass
	 *            the given {@link EClass}.
	 * @return a map with attributes names as map-keys and attributes values as
	 *         map-values.
	 */
	public static Collection<String> getAttributes(EClass eClass) {
		if (eClass == null) {
			return Collections.emptySet();
		}
		Collection<String> set = new HashSet<>();
		EList<EAttribute> eAllAttributes = eClass.getEAllAttributes();
		for (EAttribute eAttribute : eAllAttributes) {
			if (!eAttribute.isDerived() && !eAttribute.isVolatile()) {
				set.add(eAttribute.getName());
			}
		}
		return set;
	}

	/**
	 * Returns non-containment references of the given {@link EObject}. The derived,
	 * volatile and containment references are excluded from the results.
	 * 
	 * @param object
	 *            the given {@link EObject}.
	 * @return a map with references names as map-keys and references values as
	 *         map-values.
	 */
	public static Map<String, Object> getNonContainmentReferences(EObject object) {
		if (object == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = new HashMap<>();
		EList<EReference> eAllReferences = object.eClass().getEAllReferences();
		for (EReference eReference : eAllReferences) {
			if (!eReference.isContainment() && !eReference.isDerived() && !eReference.isVolatile()) {
				map.put(eReference.getName(), object.eGet(eReference));
			}
		}
		return map;
	}

	/**
	 * Returns Semantic Browser references of the given {@link EObject}.
	 * 
	 * @param object
	 *            the given {@link EObject}.
	 * @return a map with references names as map-keys and references values as
	 *         map-values.
	 */
	public static Map<String, Object> getSemanticBrowserReferences(EObject object) {
		if (object == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = new LinkedHashMap<>();

		Set<ICategory> categories = CategoryRegistry.getInstance()
				.gatherCategories(IBrowserContentProvider.ID_REFERENCED_CP, object);

		// sort categories to always have the same outputs.
		TreeSet<ICategory> sortedCategories = new TreeSet<ICategory>(Comparator.comparing(ICategory::getCategoryId));
		sortedCategories.addAll(categories);

		sortedCategories.forEach(category -> {
			try {
				List<Object> compute = category.compute(object);
				map.put(getCategoryName(category), compute);
				// CHECKSTYLE:OFF
			} catch (Exception e) {
				// Sometimes, category.compute failed, catch exception to continue the
				// conversion.
				// Activator.logWarning("Exception in Semantic Browser referenced elements
				// computing: " + e.getMessage());
			}
			// CHECKSTYLE:ON
		});
		return map;
	}

	/**
	 * Compute category name.
	 * 
	 * @param category
	 * @return
	 */
	public static String getCategoryName(ICategory category) {
		return category.getName().trim().replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", "_");
	}

	/**
	 * Check if if object is package instance..
	 * 
	 * @param object
	 *            EObject
	 * @return if object is package instance.
	 */
	public static boolean isPackage(EObject object) {
		return object instanceof Structure && object.getClass().getName().contains("Pkg");
	}

	/**
	 * Check if object is relevant for conversion -> elements which do not appear in
	 * Semantic Browser but are essentials for model structure (ex: Architectures,
	 * Packages...).
	 * 
	 * @param object
	 *            EObject
	 * @return if object is relevant for conversion.
	 */
	// CHECKSTYLE:OFF
	public static boolean isRelevantElement(EObject object) {
		return object instanceof Project || object instanceof SystemEngineering || object instanceof BlockArchitecture
				|| (object instanceof Structure && object.getClass().getName().contains("Pkg"));
		// CHECKSTYLE:ON
	}

	/**
	 * Returns non-containment references of the given {@link EClass}. The derived,
	 * volatile and containment references are excluded from the results.
	 * 
	 * @param eClass
	 *            the given {@link EClass}.
	 * @return a map with references names as map-keys and references values as
	 *         map-values.
	 */
	public static Collection<String> getNonContainmentReferences(EClass eClass) {
		if (eClass == null) {
			return Collections.emptySet();
		}
		Collection<String> set = new HashSet<>();
		EList<EReference> eAllReferences = eClass.getEAllReferences();
		for (EReference eReference : eAllReferences) {
			if (!eReference.isContainment() && !eReference.isDerived() && !eReference.isVolatile()) {
				set.add(eReference.getName());
			}
		}
		return set;
	}

	/**
	 * Returns containment references of the given {@link EObject}. The derived and
	 * volatile references are excluded from the results.
	 * 
	 * @param object
	 *            the given {@link EObject}.
	 * @return a map with references names as map-keys and references values as
	 *         map-values.
	 */
	public static Map<String, Object> getContainmentReferences(EObject object) {
		if (object == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = new HashMap<>();
		EList<EReference> eAllReferences = object.eClass().getEAllContainments();
		for (EReference eReference : eAllReferences) {
			if (!eReference.isDerived() && !eReference.isDerived() && !eReference.isVolatile()) {
				map.put(eReference.getName(), object.eGet(eReference));
			}
		}
		return map;
	}

	/**
	 * Returns containment references of the given {@link EClass}. The derived and
	 * volatile references are excluded from the results.
	 * 
	 * @param eClass
	 *            the given {@link EClass}.
	 * @return a map with references names as map-keys and references values as
	 *         map-values.
	 */
	public static Collection<String> getContainmentReferences(EClass eClass) {
		if (eClass == null) {
			return Collections.emptySet();
		}
		Collection<String> set = new HashSet<>();
		EList<EReference> eAllReferences = eClass.getEAllContainments();
		for (EReference eReference : eAllReferences) {
			if (!eReference.isDerived() && !eReference.isDerived() && !eReference.isVolatile()) {
				set.add(eReference.getName());
			}
		}
		return set;
	}

	/**
	 * Create session.
	 * 
	 * @param session
	 *            session
	 * @param packageAndClassName
	 *            String
	 * @return EObject
	 */
	public static EObject create(Session session, String packageAndClassName) {
		EObject newObject = null;
		String[] packageAndClass = packageAndClassName.split("\\.");
		if (packageAndClass != null && packageAndClass.length == 2) {
			Collection<Object> ePackages = EPackage.Registry.INSTANCE.values();
			for (Object ePackage : ePackages) {
				if (ePackage instanceof EPackage) {
					String ePackageName = ((EPackage) ePackage).getName();
					if (ePackageName.equals(packageAndClass[0])) {
						EClassifier eClassifier = ((EPackage) ePackage).getEClassifier(packageAndClass[1]);
						if (eClassifier instanceof EClass) {
							newObject = EcoreUtil.create((EClass) eClassifier);
							break;
						}
					}
				}
			}
		}
		try {
			if (session != null && newObject == null) {
				newObject = session.getModelAccessor().createInstance(packageAndClassName);
			}
		} catch (MetaClassNotFoundException e) {
			Activator.logError(e.getMessage());
		}
		return newObject;
	}

	/**
	 * Return if delimiter is allowed.
	 * 
	 * @param delimiter
	 *            Character
	 * @return if delimiter is allowed.
	 */
	public static boolean isAllowedFieldDelimiter(Character delimiter) {
		if (delimiter != null) {
			return Pattern.matches("\\S", String.valueOf(delimiter));
		}
		return false;
	}
}
