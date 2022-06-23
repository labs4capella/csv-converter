/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.junit.Test;
import org.polarsys.capella.core.data.capellamodeller.Project;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.information.datatype.NumericType;
import org.polarsys.capella.core.data.information.datatype.NumericTypeKind;
import org.polarsys.capella.core.data.information.datavalue.OpaqueExpression;
import org.polarsys.capella.core.data.la.CapabilityRealizationPkg;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.oa.OperationalAnalysis;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalComponent;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.importer.CSVToCapellaConverter;

public class CSVToCapellaConverterTestCases extends AbstractConverterTestCases {

	private static final String THIS_IS_A_REVIEW = "this is a review";
	private static final String THIS_IS_THE_SUMMARY = "this is the summary";
	private static final String CLASS_CAST = "classCast";
	private static final String TMP_FOLDER = "csvToCapella";
	private static final String IMPORT_FOLDER = "import";
	private static final String CREATE_MANY_CONTAINMENT = "createManyContainment";
	private static final String CREATE_MANY_CONTAINMENT_AIRD = "importCreateManyContainment.aird";
	private static final String CREATE_MANY_CONTAINMENT_OA_ID = "27c0d4e4-dd40-4cae-b161-1479b4679c12";
	private static final String CREATE_MONO_CONTAINMENT = "createMonoContainment";
	private static final String CREATE_MONO_CONTAINMENT_AIRD = "importCreateMonoContainment.aird";
	private static final String CREATE_MONO_CONTAINMENT_OA_ID = "191cd81d-4c62-401a-be93-52cffb417f1d";
	private static final String UPDATE_MANY_ATTRIBUTE = "updateManyAttribute";
	private static final String UPDATE_MANY_CONTAINMENT = "updateManyContainment";
	private static final String UPDATE_MANY_CONTAINMENT_AIRD = "importUpdateManyContainment.aird";
	private static final String UPDATE_MANY_NON_CONTAINMENT = "updateManyNonContainment";
	private static final String UPDATE_MANY_NON_CONTAINMENT_AIRD = "importUpdateManyNonContainment.aird";
	private static final String UPDATE_MONO_ATTRIBUTE = "updateMonoAttribute";
	private static final String UPDATE_MONO_CONTAINMENT = "updateMonoContainment";
	private static final String DELETE_MANY_CONTAINMENT = "deleteManyContainment";
	private static final String DELETE_MONO_CONTAINMENT = "deleteMonoContainment";
	private static final String SET_MONO_NON_CONTAINMENT = "setMonoNonContainment";
	private static final String SET_MONO_NON_CONTAINMENT_AIRD = "importSetMonoNonContainment.aird";
	private static final String UNEXISTING_STRUCTURAL_FEATURE = "unexistingStructuralFeature";
	private static final String UNSET_MONO_NON_CONTAINMENT = "unsetMonoNonContainment";
	private static final String UNSET_MONO_NON_CONTAINMENT_AIRD = "importUnsetMonoNonContainment.aird";
	private static final String CAPABILITY_REALIZATION_PKG_CSV = "la.CapabilityRealizationPkg.csv";
	private static final String CONSTRAINT_CSV = "capellacore.Constraint.csv";
	private static final String DATA_PKG_CSV = "information.DataPkg.csv";
	private static final String ENUMERATION_PROPERTY_LITERAL_CSV = "capellacore.EnumerationPropertyLiteral.csv";
	private static final String ENUMERATION_PROPERTY_TYPE_CSV = "capellacore.EnumerationPropertyType.csv";
	private static final String KEY_VALUE_CSV = "capellacore.KeyValue.csv";
	private static final String LOGICAL_ARCHITECTURE_CSV = "la.LogicalArchitecture.csv";
	private static final String MODEL_INFORMATION_CSV = "libraries.ModelInformation.csv";
	private static final String OPAQUE_EXPRESSION_CSV = "datavalue.OpaqueExpression.csv";
	private static final String OPERATIONAL_ANALYSIS_CSV = "oa.OperationalAnalysis.csv";
	private static final String PART_CSV = "cs.Part.csv";
	private static final String PHYSICAL_ARCHITECTURE_CSV = "pa.PhysicalArchitecture.csv";
	private static final String PROJECT_CSV = "capellamodeller.Project.csv";
	private static final String PROPERTY_VALUE_PKG_CSV = "capellacore.PropertyValuePkg.csv";
	private static final String ROLE_PKG_CSV = "oa.RolePkg.csv";
	private static final String STRING_PROPERTY_VALUE_CSV = "capellacore.StringPropertyValue.csv";
	private static final String SYSTEM_ANALYSIS_CSV = "ctx.SystemAnalysis.csv";
	private static final String SYSTEM_ENGINEERING_CSV = "capellamodeller.SystemEngineering.csv";
	private static final String ROLE_PKG1 = "RolePkg1";

	@Test
	public void importCheckFilesNames() {
		CSVSettings settings = getCSVSettings("checkFilesNames",
				Arrays.asList(PROJECT_CSV, "capellamodeller.SystemEngineeringX.csv", MODEL_INFORMATION_CSV,
						KEY_VALUE_CSV, ENUMERATION_PROPERTY_TYPE_CSV, ENUMERATION_PROPERTY_LITERAL_CSV));

		Session session = getSession("checkFilesNames", "importCheckFilesNames.aird");

		Project rootElement = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("fcb28f29-ea1f-4adf-8796-47aca4480b59");
			if (eObject instanceof Project) {
				rootElement = (Project) eObject;
				break;
			}
		}
		assertNotNull(rootElement);
		CSVToCapellaConverter converter = new CSVToCapellaConverter(rootElement, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(
				"The file capellamodeller.SystemEngineeringX.csv does not correspond to any type in Capella. Please fix the file name.",
				status.getMessage());

	}

	@Test
	public void importUpdateMonoAttribute() {
		CSVSettings settings = getCSVSettings(UPDATE_MONO_ATTRIBUTE, Collections.singleton(SYSTEM_ENGINEERING_CSV));

		Session session = getSession(UPDATE_MONO_ATTRIBUTE, "importUpdateMonoAttribute.aird");

		SystemEngineering rootElement = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("7b48bf09-a0e6-4985-8cd4-a2ffa6061b04");
			if (eObject instanceof SystemEngineering) {
				rootElement = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(rootElement);

		assertEquals(THIS_IS_THE_SUMMARY, rootElement.getSummary());
		assertEquals(THIS_IS_A_REVIEW, rootElement.getReview());
		assertNull(rootElement.getDescription());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(rootElement, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		// 'summary' mono-attribute has been set to null (was 'this is a summary')
		assertNull(rootElement.getSummary());
		// 'review' mono-attribute has been set to 'this is a new review' (was 'this is
		// a review')
		assertEquals("this is a new review", rootElement.getReview());
		// 'description' mono-attribute has been set to 'this is a description' (was
		// empty)
		assertEquals("this is a description", rootElement.getDescription());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateMonoAttributeClassCast() {
		CSVSettings settings = getCSVSettings(Paths.get(UPDATE_MONO_ATTRIBUTE, CLASS_CAST).toString(),
				Collections.singleton(SYSTEM_ENGINEERING_CSV));

		Session session = getSession(UPDATE_MONO_ATTRIBUTE, "importUpdateMonoAttribute.aird");

		SystemEngineering rootElement = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("7b48bf09-a0e6-4985-8cd4-a2ffa6061b04");
			if (eObject instanceof SystemEngineering) {
				rootElement = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(rootElement);

		assertEquals(THIS_IS_THE_SUMMARY, rootElement.getSummary());
		assertEquals(THIS_IS_A_REVIEW, rootElement.getReview());
		assertNull(rootElement.getDescription());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(rootElement, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertEquals(THIS_IS_THE_SUMMARY, rootElement.getSummary());
		assertEquals(THIS_IS_A_REVIEW, rootElement.getReview());
		assertNull(rootElement.getDescription());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateManyAttribute() {
		CSVSettings settingsChange = getCSVSettings(Paths.get(UPDATE_MANY_ATTRIBUTE, "change").toString(),
				Collections.singleton(OPAQUE_EXPRESSION_CSV));
		CSVSettings settingsUnset = getCSVSettings(Paths.get(UPDATE_MANY_ATTRIBUTE, "unset").toString(),
				Collections.singleton(OPAQUE_EXPRESSION_CSV));
		CSVSettings settingsSet = getCSVSettings(Paths.get(UPDATE_MANY_ATTRIBUTE, "set").toString(),
				Collections.singleton(OPAQUE_EXPRESSION_CSV));

		Session session = getSession(UPDATE_MANY_ATTRIBUTE, "importUpdateManyAttribute.aird");

		OpaqueExpression element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("6d1e5136-7a9e-44ad-a787-080d8b8148cb");
			if (eObject instanceof OpaqueExpression) {
				element = (OpaqueExpression) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals("[L1, L2]", element.getLanguages().toString());
		assertEquals("[B1, B2]", element.getBodies().toString());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settingsChange);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		// 'languages' many-attribute has been set to '[L1, L2, L3]' (was '[L1, L2]')
		assertEquals("[L1, L2, L3]", element.getLanguages().toString());
		// 'bodies' many-attribute has been set to '[B1, B2, B3]' (was '[B1, B2]')
		assertEquals("[B1, B2, B3]", element.getBodies().toString());

		converter = new CSVToCapellaConverter(element, settingsUnset);
		status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		// 'languages' many-attribute has been set to '' (was '[L1, L2, L3]')
		assertEquals("[]", element.getLanguages().toString());
		// 'bodies' many-attribute has been set to '' (was '[B1, B2, B3]')
		assertEquals("[]", element.getBodies().toString());

		converter = new CSVToCapellaConverter(element, settingsSet);
		status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		// 'languages' many-attribute has been set to '[L1]' (was '')
		assertEquals("[L1]", element.getLanguages().toString());
		// 'bodies' many-attribute has been set to '[B1]' (was '')
		assertEquals("[B1]", element.getBodies().toString());

		// 'languages' many-attribute has been set to '[L1]' (was '')
		assertEquals("[L1]", element.getLanguages().toString());
		// 'bodies' many-attribute has been set to '[B1]' (was '')
		assertEquals("[B1]", element.getBodies().toString());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateMonoContainment() {
		CSVSettings settings = getCSVSettings(CREATE_MONO_CONTAINMENT,
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, ROLE_PKG_CSV));

		Session session = getSession(CREATE_MONO_CONTAINMENT, CREATE_MONO_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MONO_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertNull(element.getOwnedRolePkg());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNotNull(element.getOwnedRolePkg());
		assertEquals(element.getOwnedRolePkg().getName(), ROLE_PKG1);

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateMonoContainmentClassCast() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MONO_CONTAINMENT, CLASS_CAST).toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, ROLE_PKG_CSV));

		Session session = getSession(CREATE_MONO_CONTAINMENT, CREATE_MONO_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MONO_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);
		assertNull(element.getOwnedRolePkg());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertNull(element.getOwnedRolePkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateMonoContainmentWithWrongNewTempID() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MONO_CONTAINMENT, "wrongNewTempID").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, ROLE_PKG_CSV));

		Session session = getSession(CREATE_MONO_CONTAINMENT, CREATE_MONO_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MONO_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertNull(element.getOwnedRolePkg());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		// The whole import process has failed.
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertNull(element.getOwnedRolePkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateMonoContainmentWithWrongClassName() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MONO_CONTAINMENT, "wrongClassName").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, "wp.WrongClassName.csv"));

		Session session = getSession(CREATE_MONO_CONTAINMENT, CREATE_MONO_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MONO_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertNull(element.getOwnedRolePkg());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertNull(element.getOwnedRolePkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * An OperationalAnalysis contains one RolePkg. Add a "x" to the "To delete"
	 * column of the RolePkg. Do not modify the OperationalAnalysis.
	 */
	@Test
	public void importDeleteMonoContainment() {
		CSVSettings settings = getCSVSettings(DELETE_MONO_CONTAINMENT,
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, ROLE_PKG_CSV));

		Session session = getSession(DELETE_MONO_CONTAINMENT, "importDeleteMonoContainment.aird");

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("0fb32582-649e-494d-a6b3-513df67db14c");
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertNotNull(element.getOwnedRolePkg());
		assertEquals(element.getOwnedRolePkg().getName(), ROLE_PKG1);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNull(element.getOwnedRolePkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * An OperationalAnalysis contains one RolePkg. Add a "x" to the "To update"
	 * column of OperationalAnalysis and remove the ID from the 'ownedRolePkg' cell.
	 * Do not modify the RolePkg.
	 */
	@Test
	public void importDeleteMonoContainment2() {
		CSVSettings settings = getCSVSettings(
				Paths.get(DELETE_MONO_CONTAINMENT, "onlyRemoveIDFromReference").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, ROLE_PKG_CSV));

		Session session = getSession(DELETE_MONO_CONTAINMENT, "importDeleteMonoContainment.aird");

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("0fb32582-649e-494d-a6b3-513df67db14c");
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertNotNull(element.getOwnedRolePkg());
		assertEquals(element.getOwnedRolePkg().getName(), ROLE_PKG1);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNull(element.getOwnedRolePkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importMoveMonoContainment() {
		CSVSettings settings = getCSVSettings("moveMonoContainment",
				Arrays.asList(DATA_PKG_CSV, OPERATIONAL_ANALYSIS_CSV, SYSTEM_ANALYSIS_CSV));

		Session session = getSession("moveMonoContainment", "importMoveMonoContainment.aird");

		OperationalAnalysis oa = null;
		SystemAnalysis sa = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("e8da64fc-73b5-49b5-9f3b-eb16d5697873");
			if (eObject instanceof OperationalAnalysis) {
				oa = (OperationalAnalysis) eObject;
			}
			eObject = semanticResource.getEObject("be8954ba-78f8-4684-82cf-bbaee132b724");
			if (eObject instanceof SystemAnalysis) {
				sa = (SystemAnalysis) eObject;
			}
		}
		assertNotNull(oa);
		assertNotNull(sa);

		assertNotNull(oa.getOwnedDataPkg());
		assertNull(sa.getOwnedDataPkg());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(oa, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNull(oa.getOwnedDataPkg());
		assertNotNull(sa.getOwnedDataPkg());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateManyContainment() {
		CSVSettings settings = getCSVSettings(CREATE_MANY_CONTAINMENT,
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(0, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(2, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateManyContainmentClassCast() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MANY_CONTAINMENT, CLASS_CAST).toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(0, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertEquals(0, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importForgetUpdateNewElements() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MANY_CONTAINMENT, "forgetUpdate").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(0, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.INFO, status.getSeverity());

		assertEquals(1, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importCreateManyContainmentWithWrongNewTempID() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MANY_CONTAINMENT, "wrongNewTempID").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(0, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		// The whole import process has failed.
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(0, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * If a creation fails, all others creations should not be taken into account.
	 * The whole process should fail, and the Capella model should not be modified.
	 */
	@Test
	public void importCreateManyContainmentWithWrongNewTempID2() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MANY_CONTAINMENT, "wrongNewTempID2").toString(), Arrays
				.asList(SYSTEM_ENGINEERING_CSV, PROPERTY_VALUE_PKG_CSV, OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		SystemEngineering se = null;
		OperationalAnalysis oa = null;

		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				oa = (OperationalAnalysis) eObject;
			}
			eObject = semanticResource.getEObject("38a8ee0c-f350-40b1-9b13-fdc3dbc47773");
			if (eObject instanceof SystemEngineering) {
				se = (SystemEngineering) eObject;
			}
		}
		assertNotNull(se);
		assertNotNull(oa);
		assertEquals(1, se.getOwnedPropertyValuePkgs().size());
		assertEquals(0, oa.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(se, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		// The whole import process has failed.
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(1, se.getOwnedPropertyValuePkgs().size());
		assertEquals(0, oa.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * If a creation fails, all others creations should not be taken into account.
	 * The whole process should fail, and the Capella model should not be modified.
	 */
	@Test
	public void importCreateManyContainmentWithWrongNewTempID3() {
		CSVSettings settings = getCSVSettings(Paths.get(CREATE_MANY_CONTAINMENT, "wrongNewTempID3").toString(), Arrays
				.asList(SYSTEM_ENGINEERING_CSV, PROPERTY_VALUE_PKG_CSV, OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(CREATE_MANY_CONTAINMENT, CREATE_MANY_CONTAINMENT_AIRD);

		SystemEngineering se = null;
		OperationalAnalysis oa = null;

		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject(CREATE_MANY_CONTAINMENT_OA_ID);
			if (eObject instanceof OperationalAnalysis) {
				oa = (OperationalAnalysis) eObject;
			}
			eObject = semanticResource.getEObject("38a8ee0c-f350-40b1-9b13-fdc3dbc47773");
			if (eObject instanceof SystemEngineering) {
				se = (SystemEngineering) eObject;
			}
		}
		assertNotNull(se);
		assertNotNull(oa);
		assertEquals(1, se.getOwnedPropertyValuePkgs().size());
		assertEquals(0, oa.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(se, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		// The whole import process has failed.
		assertEquals(IStatus.ERROR, status.getSeverity());
		assertEquals(1, se.getOwnedPropertyValuePkgs().size());
		assertEquals(0, oa.getOwnedConstraints().size());
		assertEquals("The ID (null) in capellacore.Constraint.csv (line 3) is not suitable for importing in Capella.",
				status.getMessage());
		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * An OperationalAnalysis contains two Constraints. Add a "x" to the "To delete"
	 * column of each Constraint. Do not modify the OperationalAnalysis.
	 */
	@Test
	public void importDeleteManyContainment() {
		CSVSettings settings = getCSVSettings(DELETE_MANY_CONTAINMENT,
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(DELETE_MANY_CONTAINMENT, "importDeleteManyContainment.aird");

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("7c087156-b26d-4d60-aad0-bf4ca3e73afd");
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(0, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * An OperationalAnalysis contains two Constraints. Add a "x" to the "To update"
	 * column of OperationalAnalysis and remove the IDs from the 'ownedConstraints'
	 * cell. Do not modify the Constraints.
	 */
	@Test
	public void importDeleteManyContainment2() {
		CSVSettings settings = getCSVSettings(
				Paths.get(DELETE_MANY_CONTAINMENT, "onlyRemoveIDsFromReference").toString(),
				Arrays.asList(OPERATIONAL_ANALYSIS_CSV, CONSTRAINT_CSV));

		Session session = getSession(DELETE_MANY_CONTAINMENT, "importDeleteManyContainment.aird");

		OperationalAnalysis element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("7c087156-b26d-4d60-aad0-bf4ca3e73afd");
			if (eObject instanceof OperationalAnalysis) {
				element = (OperationalAnalysis) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(0, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importMoveManyContainment() {
		CSVSettings settings = getCSVSettings("moveManyContainment",
				Arrays.asList(CONSTRAINT_CSV, OPERATIONAL_ANALYSIS_CSV, SYSTEM_ANALYSIS_CSV, LOGICAL_ARCHITECTURE_CSV));

		Session session = getSession("moveManyContainment", "importMoveManyContainment.aird");

		OperationalAnalysis oa = null;
		SystemAnalysis sa = null;
		LogicalArchitecture la = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("a5b8a9e7-fd65-4234-a9a6-959b98004c03");
			if (eObject instanceof OperationalAnalysis) {
				oa = (OperationalAnalysis) eObject;
			}
			eObject = semanticResource.getEObject("3616e8e6-3504-4a39-8b7d-87bb255c22d9");
			if (eObject instanceof SystemAnalysis) {
				sa = (SystemAnalysis) eObject;
			}
			eObject = semanticResource.getEObject("f218c55a-925a-4a7d-869e-2485e95d0a8c");
			if (eObject instanceof LogicalArchitecture) {
				la = (LogicalArchitecture) eObject;
			}
		}
		assertNotNull(oa);
		assertNotNull(sa);
		assertNotNull(la);

		assertEquals(2, oa.getOwnedConstraints().size());
		assertEquals(1, sa.getOwnedConstraints().size());
		assertEquals(0, la.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(oa, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(0, oa.getOwnedConstraints().size());
		assertEquals(2, sa.getOwnedConstraints().size());
		assertEquals(1, la.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importRemoveManyContainment() {
		CSVSettings settings = getCSVSettings("removeManyContainment",
				Arrays.asList(SYSTEM_ENGINEERING_CSV, CONSTRAINT_CSV));

		Session session = getSession("removeManyContainment", "importRemoveManyContainment.aird");

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("f0960cda-4425-4b68-81e9-637573354df5");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(1, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importRemoveManyNonContainment() {
		CSVSettings settings = getCSVSettings("removeManyNonContainment",
				Arrays.asList(SYSTEM_ENGINEERING_CSV, STRING_PROPERTY_VALUE_CSV));

		Session session = getSession("removeManyNonContainment", "importRemoveManyNonContainment.aird");

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("e52ae9c6-8ee7-4da8-a377-195cba516662");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getAppliedPropertyValues().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(1, element.getAppliedPropertyValues().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importSetMonoNonContainment() {
		CSVSettings settings = getCSVSettings(SET_MONO_NON_CONTAINMENT, Arrays.asList(PART_CSV));

		Session session = getSession(SET_MONO_NON_CONTAINMENT, SET_MONO_NON_CONTAINMENT_AIRD);

		Part part = null;
		PhysicalComponent pc1 = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("724d1ac7-bf5b-479c-9b3c-7e5c5c6fa29a");
			if (eObject instanceof Part) {
				part = (Part) eObject;
			}
			eObject = semanticResource.getEObject("be0bdc71-b1e1-42bf-bc82-eaec83534d2b");
			if (eObject instanceof PhysicalComponent) {
				pc1 = (PhysicalComponent) eObject;
			}
		}
		assertNotNull(part);
		assertNull(part.getAbstractType());
		assertNotNull(pc1);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(part, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNotNull(part.getAbstractType());
		assertEquals(pc1, part.getAbstractType());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importSetMonoNonContainmentClassCast() {
		CSVSettings settings = getCSVSettings(Paths.get(SET_MONO_NON_CONTAINMENT, CLASS_CAST).toString(),
				Arrays.asList(PART_CSV));

		Session session = getSession(SET_MONO_NON_CONTAINMENT, SET_MONO_NON_CONTAINMENT_AIRD);

		Part part = null;
		PhysicalComponent pc1 = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("724d1ac7-bf5b-479c-9b3c-7e5c5c6fa29a");
			if (eObject instanceof Part) {
				part = (Part) eObject;
			}
			eObject = semanticResource.getEObject("be0bdc71-b1e1-42bf-bc82-eaec83534d2b");
			if (eObject instanceof PhysicalComponent) {
				pc1 = (PhysicalComponent) eObject;
			}
		}
		assertNotNull(part);
		assertNull(part.getAbstractType());
		assertNotNull(pc1);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(part, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertNotNull(part);
		assertNull(part.getAbstractType());
		assertNotNull(pc1);

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUnsetMonoNonContainment() {
		CSVSettings settings = getCSVSettings(UNSET_MONO_NON_CONTAINMENT, Arrays.asList(PART_CSV));

		Session session = getSession(UNSET_MONO_NON_CONTAINMENT, UNSET_MONO_NON_CONTAINMENT_AIRD);

		Part part = null;
		PhysicalComponent pc1 = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("f8fa9b77-4c88-4a93-85fb-08bd08bde7f9");
			if (eObject instanceof Part) {
				part = (Part) eObject;
			}
			eObject = semanticResource.getEObject("f00889d7-450a-427a-b6c9-cdaad5b54bce");
			if (eObject instanceof PhysicalComponent) {
				pc1 = (PhysicalComponent) eObject;
			}
		}
		assertNotNull(part);
		assertNotNull(part.getAbstractType());
		assertNotNull(pc1);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(part, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertNull(part.getAbstractType());
		assertNotNull(pc1);

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUnexistingStructuralFeatureOnUpdate() {
		CSVSettings settings = getCSVSettings(Paths.get(UNEXISTING_STRUCTURAL_FEATURE, "onUpdate").toString(),
				Arrays.asList(SYSTEM_ENGINEERING_CSV));

		Session session = getSession(UNEXISTING_STRUCTURAL_FEATURE, "unexistingStructuralFeature.aird");

		SystemEngineering se = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("6b9b2648-662d-479d-a6a9-dd4edcd3b97f");
			if (eObject instanceof SystemEngineering) {
				se = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(se);
		assertEquals(UNEXISTING_STRUCTURAL_FEATURE, se.getName());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(se, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		// The name feature has not been update because of the unexistingFeature
		// "UnexistingDescription"
		assertEquals(UNEXISTING_STRUCTURAL_FEATURE, se.getName());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUnexistingStructuralFeatureOnCreate() {
		CSVSettings settings = getCSVSettings(Paths.get(UNEXISTING_STRUCTURAL_FEATURE, "onCreate").toString(),
				Arrays.asList(SYSTEM_ENGINEERING_CSV, SYSTEM_ANALYSIS_CSV));

		Session session = getSession(UNEXISTING_STRUCTURAL_FEATURE, "unexistingStructuralFeature.aird");

		SystemEngineering se = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("6b9b2648-662d-479d-a6a9-dd4edcd3b97f");
			if (eObject instanceof SystemEngineering) {
				se = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(se);
		assertEquals(0, se.getOwnedArchitecturePkgs().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(se, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		// The System Analysis has not been created because of the unexisting feature
		assertEquals(0, se.getOwnedArchitecturePkgs().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateManyContainment() {
		CSVSettings settings = getCSVSettings(UPDATE_MANY_CONTAINMENT,
				Arrays.asList(SYSTEM_ENGINEERING_CSV, CONSTRAINT_CSV));

		Session session = getSession(UPDATE_MANY_CONTAINMENT, UPDATE_MANY_CONTAINMENT_AIRD);

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("56729a1b-658b-4546-9694-0da5fed17a76");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedConstraints().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(3, element.getOwnedConstraints().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateManyNonContainment() {
		CSVSettings settings = getCSVSettings(UPDATE_MANY_NON_CONTAINMENT,
				Arrays.asList(SYSTEM_ENGINEERING_CSV, STRING_PROPERTY_VALUE_CSV));

		Session session = getSession(UPDATE_MANY_NON_CONTAINMENT, UPDATE_MANY_NON_CONTAINMENT_AIRD);

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("d0f792c5-1d7b-45bb-8d95-3344813c6891");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedPropertyValues().size());
		assertEquals(2, element.getAppliedPropertyValues().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(3, element.getOwnedPropertyValues().size());
		assertEquals(3, element.getAppliedPropertyValues().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateManyNonContainmentClasCast() {
		CSVSettings settings = getCSVSettings(Paths.get(UPDATE_MANY_NON_CONTAINMENT, CLASS_CAST).toString(),
				Arrays.asList(SYSTEM_ENGINEERING_CSV, STRING_PROPERTY_VALUE_CSV));

		Session session = getSession(UPDATE_MANY_NON_CONTAINMENT, UPDATE_MANY_NON_CONTAINMENT_AIRD);

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("d0f792c5-1d7b-45bb-8d95-3344813c6891");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);

		assertEquals(2, element.getOwnedPropertyValues().size());
		assertEquals(2, element.getAppliedPropertyValues().size());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.ERROR, status.getSeverity());

		assertEquals(2, element.getOwnedPropertyValues().size());
		assertEquals(2, element.getAppliedPropertyValues().size());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateMonoContainment() {
		CSVSettings settings = getCSVSettings(UPDATE_MONO_CONTAINMENT,
				Arrays.asList(PHYSICAL_ARCHITECTURE_CSV, LOGICAL_ARCHITECTURE_CSV, CAPABILITY_REALIZATION_PKG_CSV));

		Session session = getSession(UPDATE_MONO_CONTAINMENT, "importUpdateMonoContainment.aird");

		LogicalArchitecture la = null;
		PhysicalArchitecture pa = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("4d49d9e6-0737-4d9f-bc89-e2b056684389");
			if (eObject instanceof LogicalArchitecture) {
				la = (LogicalArchitecture) eObject;
			}
			eObject = semanticResource.getEObject("ee457abb-1b97-4504-91f4-02ca67ec1ba0");
			if (eObject instanceof PhysicalArchitecture) {
				pa = (PhysicalArchitecture) eObject;
			}
		}
		assertNotNull(la);
		assertNotNull(pa);

		CapabilityRealizationPkg laPkg = la.getContainedCapabilityRealizationPkg();
		assertNotNull(laPkg);
		CapabilityRealizationPkg paPkg = pa.getContainedCapabilityRealizationPkg();
		assertNotNull(paPkg);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(la, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		CapabilityRealizationPkg updatedLaPkg = la.getContainedCapabilityRealizationPkg();
		assertNotNull(updatedLaPkg);
		assertEquals(paPkg, updatedLaPkg);
		CapabilityRealizationPkg updatedPaPkg = pa.getContainedCapabilityRealizationPkg();
		assertNull(updatedPaPkg);

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	/**
	 * After an import, an export is executed. All export files have the '_after'
	 * suffix.
	 */
	@Test
	public void importAfterFiles() {
		CSVSettings settings = getCSVSettings("afterFiles",
				Arrays.asList(SYSTEM_ENGINEERING_CSV, PROJECT_CSV, MODEL_INFORMATION_CSV, KEY_VALUE_CSV,
						ENUMERATION_PROPERTY_TYPE_CSV, ENUMERATION_PROPERTY_LITERAL_CSV, CONSTRAINT_CSV));

		Session session = getSession("afterFiles", "afterFiles.aird");

		Project project = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("5b85ce98-a3f2-40ab-9cd8-872e28b19ac6");
			if (eObject instanceof Project) {
				project = (Project) eObject;
				break;
			}
		}
		assertNotNull(project);

		CSVToCapellaConverter converter = new CSVToCapellaConverter(project, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		String conversionDirectoryPath = settings.getConversionDirectoryPath() + File.separator
				+ CSVConstants.AFTER_FOLDER;
		File directory = new File(conversionDirectoryPath);
		// There are no constraints after the import so no Constraints_after.csv is
		// generated
		assertSame(6, directory.listFiles().length);

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateEnumAttribute() {
		CSVSettings settings = getCSVSettings("updateEnumAttribute", Arrays.asList("datatype.NumericType.csv"));

		Session session = getSession("updateEnumAttribute", "updateEnumAttribute.aird");

		NumericType element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("112af168-fad7-49e6-bef8-a4ccf9bff66a");
			if (eObject instanceof NumericType) {
				element = (NumericType) eObject;
				break;
			}
		}
		assertNotNull(element);
		assertEquals(NumericTypeKind.INTEGER, element.getKind());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertEquals(NumericTypeKind.FLOAT, element.getKind());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	@Test
	public void importUpdateBooleanAttribute() {
		CSVSettings settings = getCSVSettings("updateBooleanAttribute", Arrays.asList(SYSTEM_ENGINEERING_CSV));

		Session session = getSession("updateBooleanAttribute", "importUpdateBooleanAttribute.aird");

		SystemEngineering element = null;
		Collection<Resource> semanticResources = session.getSemanticResources();
		for (Resource semanticResource : semanticResources) {
			EObject eObject = semanticResource.getEObject("496b1214-95c4-4491-9794-7d3460930650");
			if (eObject instanceof SystemEngineering) {
				element = (SystemEngineering) eObject;
				break;
			}
		}
		assertNotNull(element);
		assertTrue(element.isVisibleInDoc());

		CSVToCapellaConverter converter = new CSVToCapellaConverter(element, settings);
		IStatus status = converter.importCSVsIntoCapella(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		assertFalse(element.isVisibleInDoc());

		session.setSavingPolicy((resourcesToSave, options, monitor) -> null);
		session.close(new NullProgressMonitor());
	}

	protected CSVSettings getCSVSettings(String csvFolder, Collection<String> csvFiles) {
		Path importTmpDirectory = null;
		try {
			importTmpDirectory = Files.createTempDirectory(TMP_FOLDER);
			for (String csvFile : csvFiles) {
				Path csvSource = Paths.get(RESOURCES_FOLDER, IMPORT_FOLDER, csvFolder, csvFile);
				URL csvSourceURL = Platform.getBundle(Activator.PLUGIN_ID).getEntry(csvSource.toString());
				Path target = Paths.get(importTmpDirectory.toString(), csvFile);
				Files.copy(csvSourceURL.openStream(), target, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(importTmpDirectory.toString(), FIELD_DELIMITER, TEXT_DELIMITER,
				CHARACTER_SET, LINE_SEPARATOR);
		return settings;
	}

	protected Session getSession(String airdFolder, String airdFile) {
		Path sessionResourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, IMPORT_FOLDER, airdFolder,
				airdFile);
		URI sessionResourceURI = URI.createPlatformPluginURI(sessionResourcePath.toString(), true);
		Session session = SessionManager.INSTANCE.getSession(sessionResourceURI, new NullProgressMonitor());
		assertNotNull(session);
		session.open(new NullProgressMonitor());
		assertTrue(session.isOpen());
		return session;
	}
}
