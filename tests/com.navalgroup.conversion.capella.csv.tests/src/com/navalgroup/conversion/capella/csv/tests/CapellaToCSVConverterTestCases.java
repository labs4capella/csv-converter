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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.export.CapellaToCSVConverter;

public class CapellaToCSVConverterTestCases extends AbstractConverterTestCases {

	private static final String TMP_FOLDER = "capellaToCSV";
	private static final String EXPORT_FOLDER = "export";
	private static final String MELODYMODELLER = ".melodymodeller";
	private static final String EXPORT_2_TIMES = "export2Times";
	private static final String EXPORT_3_TIMES = "export3Times";
	private static final String EXPORT_MANY_ATTRIBUTE = "exportManyAttribute";
	private static final String EXPORT_MANY_CONTAINMENT = "exportManyContainment";
	private static final String EXPORT_MANY_NON_CONTAINMENT = "exportManyNonContainment";
	private static final String EXPORT_MONO_ATTRIBUTE = "exportMonoAttribute";
	private static final String EXPORT_MONO_CONTAINMENT = "exportMonoContainment";
	private static final String EXPORT_MONO_NON_CONTAINMENT = "exportMonoNonContainment";
	private static final String EXPORT_ON_EXISTING_FILES = "exportOnExistingFiles";
	private static final String LOGICAL_ARCHITECTURE_CSV = "la.LogicalArchitecture.csv";
	private static final String LOGICAL_FUNCTION_CSV = "la.LogicalFunction.csv";
	private static final String LOGICAL_FUNCTION_PKG_CSV = "la.LogicalFunctionPkg.csv";
	private static final String SYSTEM_ENGINEERING_CSV = "capellamodeller.SystemEngineering.csv";

	@Test
	public void exportMonoAttribute() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MONO_ATTRIBUTE,
				EXPORT_MONO_ATTRIBUTE + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();

		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_MONO_ATTRIBUTE, SYSTEM_ENGINEERING_CSV);
	}

	@Test
	public void exportManyAttribute() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MANY_ATTRIBUTE,
				EXPORT_MANY_ATTRIBUTE + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_MANY_ATTRIBUTE, "datavalue.OpaqueExpression.csv");
	}

	@Test
	public void exportMonoContainment() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MONO_CONTAINMENT,
				EXPORT_MONO_CONTAINMENT + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		Arrays.sort(outputFiles, Comparator.comparing(f -> f.getName()));
		assertEquals(2, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_MONO_CONTAINMENT, "oa.OperationalAnalysis.csv");
		compareCSVs(outputFiles[1], EXPORT_MONO_CONTAINMENT, "oa.RolePkg.csv");
	}

	@Test
	public void exportManyContainment() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MANY_CONTAINMENT,
				EXPORT_MANY_CONTAINMENT + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(2, outputFiles.length);
		Arrays.sort(outputFiles, Comparator.comparing(f -> f.getName()));
		compareCSVs(outputFiles[0], EXPORT_MANY_CONTAINMENT, "capellacore.Constraint.csv");
		compareCSVs(outputFiles[1], EXPORT_MANY_CONTAINMENT, SYSTEM_ENGINEERING_CSV);
	}

	@Test
	public void exportMonoNonContainment() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MONO_NON_CONTAINMENT,
				EXPORT_MONO_NON_CONTAINMENT + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(3, outputFiles.length);
		Arrays.sort(outputFiles, Comparator.comparing(f -> f.getName()));
		compareCSVs(outputFiles[0], EXPORT_MONO_NON_CONTAINMENT, "cs.Part.csv");
		compareCSVs(outputFiles[1], EXPORT_MONO_NON_CONTAINMENT, "pa.PhysicalComponent.csv");
		compareCSVs(outputFiles[2], EXPORT_MONO_NON_CONTAINMENT, "pa.PhysicalComponentPkg.csv");
	}

	@Test
	public void exportManyNonContainment() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_MANY_NON_CONTAINMENT,
				EXPORT_MANY_NON_CONTAINMENT + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());

		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(3, outputFiles.length);
		Arrays.sort(outputFiles, Comparator.comparing(f -> f.getName()));
		compareCSVs(outputFiles[0], EXPORT_MANY_NON_CONTAINMENT, "capellacore.BooleanPropertyValue.csv");
		compareCSVs(outputFiles[1], EXPORT_MANY_NON_CONTAINMENT, "capellacore.FloatPropertyValue.csv");
		compareCSVs(outputFiles[2], EXPORT_MANY_NON_CONTAINMENT, SYSTEM_ENGINEERING_CSV);
	}

	@Test
	public void exportOnExistingFiles() {
		try {
			final Path directoryPath = Files.createTempDirectory(TMP_FOLDER);
			CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER,
					CHARACTER_SET, LINE_SEPARATOR);

			ResourceSet rSet = new ResourceSetImpl();
			Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER,
					EXPORT_ON_EXISTING_FILES, EXPORT_ON_EXISTING_FILES + MELODYMODELLER);
			URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
			Resource resource = rSet.createResource(uri);
			try {
				resource.load(Collections.emptyMap());
			} catch (IOException e) {
				fail(e.getMessage());
			}
			assertNotNull(resource);

			EObject object = resource.getContents().get(0);
			assertNotNull(object);

			// Put existing csv files into export directory
			Path existingCSVsPath = Paths.get(RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_ON_EXISTING_FILES, "existing");
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			InputStream stream = FileLocator.openStream(bundle, new org.eclipse.core.runtime.Path(
					Paths.get(existingCSVsPath.toString(), LOGICAL_ARCHITECTURE_CSV).toString()), false);
			Files.copy(stream, Paths.get(directoryPath.toString(), LOGICAL_ARCHITECTURE_CSV));
			stream.close();
			stream = FileLocator.openStream(bundle, new org.eclipse.core.runtime.Path(
					Paths.get(existingCSVsPath.toString(), LOGICAL_FUNCTION_CSV).toString()), false);
			Files.copy(stream, Paths.get(directoryPath.toString(), LOGICAL_FUNCTION_CSV));
			stream.close();
			stream = FileLocator.openStream(bundle, new org.eclipse.core.runtime.Path(
					Paths.get(existingCSVsPath.toString(), LOGICAL_FUNCTION_PKG_CSV).toString()), false);
			Files.copy(stream, Paths.get(directoryPath.toString(), LOGICAL_FUNCTION_PKG_CSV));
			stream.close();
			stream = FileLocator.openStream(bundle, new org.eclipse.core.runtime.Path(
					Paths.get(existingCSVsPath.toString(), SYSTEM_ENGINEERING_CSV).toString()), false);
			Files.copy(stream, Paths.get(directoryPath.toString(), SYSTEM_ENGINEERING_CSV));
			stream.close();

			CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
			IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
			assertEquals(IStatus.OK, status.getSeverity());

			File outputDirectory = directoryPath.toFile();
			File[] outputFiles = outputDirectory.listFiles();
			assertEquals(4, outputFiles.length);
			Arrays.sort(outputFiles, Comparator.comparing(f -> f.getName()));
			compareCSVs(outputFiles[0], EXPORT_ON_EXISTING_FILES, SYSTEM_ENGINEERING_CSV);
			compareCSVs(outputFiles[1], EXPORT_ON_EXISTING_FILES, LOGICAL_ARCHITECTURE_CSV);
			compareCSVs(outputFiles[2], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_CSV);
			compareCSVs(outputFiles[3], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_PKG_CSV);

			// The creation date & time cells should remain the same than the existing csv
			// files
			compareCSVCell(outputFiles[0], EXPORT_ON_EXISTING_FILES, SYSTEM_ENGINEERING_CSV, 1);
			compareCSVCell(outputFiles[0], EXPORT_ON_EXISTING_FILES, SYSTEM_ENGINEERING_CSV, 2);
			compareCSVCell(outputFiles[1], EXPORT_ON_EXISTING_FILES, LOGICAL_ARCHITECTURE_CSV, 1);
			compareCSVCell(outputFiles[1], EXPORT_ON_EXISTING_FILES, LOGICAL_ARCHITECTURE_CSV, 2);
			compareCSVCell(outputFiles[2], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_CSV, 1);
			compareCSVCell(outputFiles[2], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_CSV, 2);
			compareCSVCell(outputFiles[3], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_PKG_CSV, 1);
			compareCSVCell(outputFiles[3], EXPORT_ON_EXISTING_FILES, LOGICAL_FUNCTION_PKG_CSV, 2);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void export2Times() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_2_TIMES,
				EXPORT_2_TIMES + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());
		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_2_TIMES, SYSTEM_ENGINEERING_CSV);

		converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());
		outputDirectory = directoryPath.toFile();
		outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_2_TIMES, SYSTEM_ENGINEERING_CSV);
	}

	@Test
	public void export3Times() {
		Path directoryPath = null;
		try {
			directoryPath = Files.createTempDirectory(TMP_FOLDER);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		CSVSettings settings = new CSVSettings(directoryPath.toString(), FIELD_DELIMITER, TEXT_DELIMITER, CHARACTER_SET,
				LINE_SEPARATOR);

		ResourceSet rSet = new ResourceSetImpl();
		Path resourcePath = Paths.get(Activator.PLUGIN_ID, RESOURCES_FOLDER, EXPORT_FOLDER, EXPORT_3_TIMES,
				EXPORT_3_TIMES + MELODYMODELLER);
		URI uri = URI.createPlatformPluginURI(resourcePath.toString(), true);
		Resource resource = rSet.createResource(uri);
		try {
			resource.load(Collections.emptyMap());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(resource);

		EObject object = resource.getContents().get(0);
		assertNotNull(object);

		CapellaToCSVConverter converter = new CapellaToCSVConverter(object, settings);
		IStatus status = converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());
		File outputDirectory = directoryPath.toFile();
		File[] outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_3_TIMES, SYSTEM_ENGINEERING_CSV);

		converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());
		outputDirectory = directoryPath.toFile();
		outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_3_TIMES, SYSTEM_ENGINEERING_CSV);

		converter.generateEObjectCSV(SubMonitor.convert(new NullProgressMonitor()));
		assertEquals(IStatus.OK, status.getSeverity());
		outputDirectory = directoryPath.toFile();
		outputFiles = outputDirectory.listFiles();
		assertEquals(1, outputFiles.length);
		compareCSVs(outputFiles[0], EXPORT_3_TIMES, SYSTEM_ENGINEERING_CSV);
	}

	protected void compareCSVs(File actualFile, String expectedFolder, String expectedFile) {
		try {
			Path expectedCSVPath = Paths.get(RESOURCES_FOLDER, EXPORT_FOLDER, expectedFolder, expectedFile);
			URL expectedCSVURL = Platform.getBundle(Activator.PLUGIN_ID).getEntry(expectedCSVPath.toString());
			java.net.URI expectedCSVURI = FileLocator.toFileURL(expectedCSVURL).toURI();
			byte[] expectedBytes = Files.readAllBytes(Paths.get(expectedCSVURI));
			byte[] actualBytes = Files.readAllBytes(Paths.get(actualFile.getAbsolutePath()));
			String expectedFileAsString = new String(expectedBytes, CHARACTER_SET);
			String actualFileAsString = new String(actualBytes, CHARACTER_SET);
			// Remove all date & time fields, because they are generated with today date &
			// time, so they not correspond to expected ones
			expectedFileAsString = expectedFileAsString.replaceAll(
					"\"[0-9]{4}[0-9]{2}[0-9]{2}\"" + FIELD_DELIMITER
							+ "\"[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{1,8}\\+[0-9]{2}:[0-9]{2}\"",
					String.valueOf(FIELD_DELIMITER));
			actualFileAsString = actualFileAsString.replaceAll(
					"\"[0-9]{8}\"" + FIELD_DELIMITER
							+ "\"[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{1,8}\\+[0-9]{2}:[0-9]{2}\"",
					String.valueOf(FIELD_DELIMITER));
			assertEquals(expectedFileAsString, actualFileAsString);
		} catch (IOException | URISyntaxException e) {
			fail(e.getMessage());
		}
	}

	protected void compareCSVCell(File actualFile, String expectedFolder, String expectedFile, int columnCell) {
		try {
			Path expectedCSVPath = Paths.get(RESOURCES_FOLDER, EXPORT_FOLDER, expectedFolder, expectedFile);
			URL expectedCSVURL = Platform.getBundle(Activator.PLUGIN_ID).getEntry(expectedCSVPath.toString());
			java.net.URI expectedCSVURI = FileLocator.toFileURL(expectedCSVURL).toURI();
			byte[] expectedBytes = Files.readAllBytes(Paths.get(expectedCSVURI));
			byte[] actualBytes = Files.readAllBytes(Paths.get(actualFile.getAbsolutePath()));
			String expectedFileAsString = new String(expectedBytes, CHARACTER_SET);
			String actualFileAsString = new String(actualBytes, CHARACTER_SET);

			String[] expectedRows = expectedFileAsString.split(String.valueOf(LINE_SEPARATOR));
			String[] actualRows = actualFileAsString.split(String.valueOf(LINE_SEPARATOR));

			for (int i = 0; i < expectedRows.length; i++) {
				String expectedRow = expectedRows[i];
				String actualRow = actualRows[i];

				String[] expectedCells = expectedRow.split(String.valueOf(FIELD_DELIMITER));
				String[] actualCells = actualRow.split(String.valueOf(FIELD_DELIMITER));

				assertTrue(expectedCells.length > columnCell);
				assertTrue(actualCells.length > columnCell);

				String expectedCell = expectedCells[columnCell];
				String actualCell = actualCells[columnCell];
				assertEquals(expectedCell, actualCell);
			}
		} catch (IOException | URISyntaxException e) {
			fail(e.getMessage());
		}
	}
}
