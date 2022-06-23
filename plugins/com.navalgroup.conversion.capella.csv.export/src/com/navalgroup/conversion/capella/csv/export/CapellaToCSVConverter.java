/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.viewpoint.DAnalysisSessionEObject;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;

/**
 * A converter to CSV files from a given {@link EObject}. The conversion
 * iterates on the given {@link EObject} children recursively. One CSV file is
 * generated/updated for each type of model element.
 * 
 * @author arichard
 *
 */
public class CapellaToCSVConverter {

	/**
	 * The root element of the existing Capella source model.
	 */
	protected EObject rootElement;

	/**
	 * The {@link CSVSettings} that will be applied to the converter.
	 */
	protected CSVSettings csvSettings;

	/**
	 * The {@link CSVFormat} that will be applied to the converter.
	 */
	protected CSVFormat csvFormat;

	/**
	 * Whether the export to CSV conversion is executed after an import from CSVs
	 * files.
	 */
	protected boolean afterImportFromCSVs;

	/**
	 * The Sirius session associated to the existing Capella target model.
	 */
	protected Session siriusSession;

	/**
	 * For debugging.
	 */
	protected int nbReferences;

	/**
	 * Constructor.
	 * 
	 * @param rootElement
	 *            EObject
	 * @param csvSettings
	 *            CSVSettings
	 */
	public CapellaToCSVConverter(EObject rootElement, CSVSettings csvSettings) {
		this.rootElement = rootElement;
		this.csvSettings = csvSettings;
		this.afterImportFromCSVs = false;
		String exportDirectoryPath = this.csvSettings.getConversionDirectoryPath();
		File csvOutputDirectory = new File(exportDirectoryPath);
		if (!csvOutputDirectory.exists()) {
			csvOutputDirectory.mkdirs();
		}
		// @formatter:off
		this.csvFormat = CSVFormat.EXCEL
				.withDelimiter(this.csvSettings.getFieldDelimiter())
				.withQuoteMode(QuoteMode.ALL_NON_NULL)
				.withQuote(this.csvSettings.getTextDelimiter())
				.withRecordSeparator(this.csvSettings.getLineSeparator())
				.withNullString("");
		// @formatter:on
		Optional<Session> sessionOpt = Session.of(this.rootElement);
		if (sessionOpt.isPresent()) {
			this.siriusSession = sessionOpt.get();
		}
	}

	/**
	 * Generate CSVs.
	 * 
	 * @param monitor
	 *            SubMonitor
	 * @return IStatus
	 */
	public IStatus generateEObjectCSV(SubMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			if (this.rootElement != null) {
				monitor.setWorkRemaining(100);
				SubMonitor prepareMonitor = monitor.split(10);
				// Create before copy of all existing csv file
				prepareExport(prepareMonitor);
				SubMonitor conversion = monitor.split(80);
				this.generateEObjectCSV(this.rootElement, this.csvFormat, conversion);
				SubMonitor deleted = monitor.split(10);
				this.generateDeletedEObjectCSV(this.csvFormat, deleted);
			} else {
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"The root element of the main semantic resource cannot be found.");
			}
		} catch (FileAlreadyExistsException e) {
			Activator.logError(e.getMessage(), e);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"A file named " + e.getMessage() + " already exists. Please delete it.");
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		} catch (OperationCanceledException e) {
			// No need to log something
		}
		try {
			deleteTempExportFiles();
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		}
		return status;
	}

	protected void prepareExport(SubMonitor monitor) throws IOException {
		if (!this.afterImportFromCSVs) {
			String conversionDirectoryPath = this.csvSettings.getConversionDirectoryPath();
			List<Path> conversionFilesPaths = Files.list(Paths.get(conversionDirectoryPath))
					.collect(Collectors.toList());
			for (Path filePath : conversionFilesPaths) {
				String filePathAsString = filePath.toString();
				if (filePathAsString.endsWith(CSVConstants.BEFORE_CSV)) {
					File f = filePath.toFile();
					f.delete();
				}
			}
			conversionFilesPaths = Files.list(Paths.get(conversionDirectoryPath)).collect(Collectors.toList());
			for (Path filePath : conversionFilesPaths) {
				String filePathAsString = filePath.toString();
				if (filePathAsString.endsWith(CSVConstants.CSV_EXT)) {
					String beforeCopyPath = filePath.toAbsolutePath().toString().replace(CSVConstants.CSV_EXT,
							CSVConstants.BEFORE_CSV);
					Files.copy(filePath, Paths.get(beforeCopyPath), StandardCopyOption.REPLACE_EXISTING);
					File f = filePath.toFile();
					f.delete();
				}
			}
		} else {
			String conversionDirectoryPath = this.csvSettings.getConversionDirectoryPath();
			conversionDirectoryPath += File.separator + CSVConstants.AFTER_FOLDER;
			Path afterFolder = Paths.get(conversionDirectoryPath);
			if (Files.exists(afterFolder) && Files.isDirectory(afterFolder)) {
				List<Path> afterFilesPaths = Files.list(afterFolder).collect(Collectors.toList());
				for (Path afterfilePath : afterFilesPaths) {
					String filePathAsString = afterfilePath.toString();
					if (filePathAsString.endsWith(CSVConstants.CSV_EXT)) {
						File f = afterfilePath.toFile();
						f.delete();
					}
				}
			} else {
				Files.createDirectory(afterFolder);
			}
		}
	}

	protected void deleteTempExportFiles() throws IOException {
		if (!this.afterImportFromCSVs) {
			String conversionDirectoryPath = this.csvSettings.getConversionDirectoryPath();
			List<Path> conversionFilesPaths = Files.list(Paths.get(conversionDirectoryPath))
					.collect(Collectors.toList());
			for (Path filePath : conversionFilesPaths) {
				String filePathAsString = filePath.toString();
				if (filePathAsString.endsWith(CSVConstants.BEFORE_CSV)) {
					File f = filePath.toFile();
					f.delete();
				}
			}
		}
	}

	protected void generateEObjectCSV(EObject object, CSVFormat format, SubMonitor monitor) throws IOException {
		monitor.setWorkRemaining(100);
		File csvInputFile = getInputFile(object);
		File csvOutputFile = getOutputFile(object);
		String csvOutputFilePath = csvOutputFile.getAbsolutePath();

		Map<String, CSVRecord> idToCSVRecordMap = new HashMap<>();
		CSVParser csvParser = null;
		if (csvInputFile != null && csvInputFile.exists()) {
			csvParser = getCSVParser(csvInputFile);
			if (csvParser != null) {
				Collection<CSVRecord> existingCSVRecords = csvParser.getRecords();
				existingCSVRecords.stream().forEach(rec -> {
					String id = rec.get(CSVSettings.ID_COLUMN);
					if (id != null && !id.isEmpty()) {
						idToCSVRecordMap.put(id, rec);
					}
				});
			}
		}

		Map<String, Object> attributes = ConversionUtil.getAttributes(object);
		Map<String, Object> nonContainmentReferences = ConversionUtil.getNonContainmentReferences(object);
		Map<String, Object> containmentReferences = ConversionUtil.getContainmentReferences(object);

		CSVFormat csvFormatWithHeader;
		if (csvOutputFile != null && csvOutputFile.exists()) {
			csvFormatWithHeader = format;
		} else {
			Collection<String> headerRecord = createHeaderRecord(attributes.keySet(), nonContainmentReferences.keySet(),
					containmentReferences.keySet());
			csvFormatWithHeader = format.withHeader(headerRecord.toArray(new String[headerRecord.size()]));
		}

		Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, true),
				this.csvSettings.getCharacterSet());
		try (CSVPrinter printer = new CSVPrinter(fstream, csvFormatWithHeader)) {
			printer.printRecord(createRecord(object, attributes.values(), nonContainmentReferences.values(),
					containmentReferences.values(), idToCSVRecordMap));
		}

		if (csvParser != null) {
			csvParser.close();
		}

		handleChildren(containmentReferences, format, monitor);
	}

	protected void generateDeletedEObjectCSV(CSVFormat format, SubMonitor monitor) throws IOException {
		String conversionDirectoryPath = this.csvSettings.getConversionDirectoryPath();
		Collection<Path> inputFilePaths = new HashSet<>();
		List<Path> conversionFilesPaths = Files.list(Paths.get(conversionDirectoryPath)).collect(Collectors.toList());
		if (this.afterImportFromCSVs) {
			for (Path filePath : conversionFilesPaths) {
				String filePathAsString = filePath.toString();
				if (!filePathAsString.endsWith(CSVConstants.BEFORE_CSV)
						&& filePathAsString.endsWith(CSVConstants.CSV_EXT)) {
					inputFilePaths.add(filePath);
				}
			}
		} else {
			for (Path filePath : conversionFilesPaths) {
				String filePathAsString = filePath.toString();
				if (filePathAsString.endsWith(CSVConstants.BEFORE_CSV)) {
					inputFilePaths.add(filePath);
				}
			}
		}
		for (Path csvInputFilePath : inputFilePaths) {
			generateDeletedRecords(format, csvInputFilePath);
		}
	}

	protected EObject getEObjectById(String id) {
		EObject eObject = null;
		if (this.siriusSession == null) {
			eObject = this.rootElement.eResource().getEObject(id);
		} else {
			Collection<Resource> semanticResource = this.siriusSession.getSemanticResources();
			for (Resource eResource : semanticResource) {
				eObject = eResource.getEObject(id);
				if (eObject != null) {
					break;
				}
			}
			// search in fragments
			if (eObject == null) {
				EList<Resource> controlledResources = ((DAnalysisSessionEObject) this.siriusSession)
						.getControlledResources();
				for (Resource eResource : controlledResources) {
					eObject = eResource.getEObject(id);
					if (eObject != null) {
						break;
					}
				}
			}
		}
		return eObject;
	}

	protected void generateDeletedRecords(CSVFormat format, Path csvInputFilePath)
			throws IOException, FileNotFoundException {
		File csvInputFile = csvInputFilePath.toFile();
		Collection<CSVRecord> deletedRecords = new HashSet<>();
		CSVParser csvParser = null;
		if (csvInputFile != null && csvInputFile.exists()) {
			csvParser = getCSVParser(csvInputFile);
			if (csvParser != null) {
				Collection<CSVRecord> inputCSVRecords = csvParser.getRecords();
				inputCSVRecords.stream().forEach(rec -> {
					// @CHECKSTYLE:OFF
					String id = rec.get(CSVSettings.ID_COLUMN);
					String toDelete = rec.get(CSVSettings.TO_DELETE_COLUMN);
					if (id != null && !id.isEmpty() && toDelete != null && !toDelete.isEmpty()
							&& !id.trim().startsWith("%") && getEObjectById(id) == null) {
						deletedRecords.add(rec);
					}
					// @CHECKSTYLE:ON
				});
			}
		}
		final String csvOutputFilePath;
		if (this.afterImportFromCSVs) {
			csvOutputFilePath = Paths.get(csvInputFilePath.getParent().toString(), CSVConstants.AFTER_FOLDER,
					csvInputFilePath.getFileName().toString()).toString();
		} else {
			csvOutputFilePath = csvInputFilePath.toString().replace(CSVConstants.BEFORE_CSV, CSVConstants.CSV_EXT);
		}
		if (!Files.exists(Paths.get(csvOutputFilePath))) {
			return;
		}
		Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, true),
				this.csvSettings.getCharacterSet());
		try (CSVPrinter printer = new CSVPrinter(fstream, format)) {
			for (CSVRecord deletedRecord : deletedRecords) {
				printer.printRecord(deletedRecord);
			}
		}
		if (csvParser != null) {
			csvParser.close();
		}
	}

	protected File getInputFile(EObject object) {
		String csvInputFilePath = this.csvSettings.getConversionDirectoryPath() + File.separator
				+ getCSVFileName(object);
		File csvInputFile = new File(csvInputFilePath);

		if (!this.afterImportFromCSVs) {
			String beforeCopyPath = this.csvSettings.getConversionDirectoryPath() + File.separator
					+ getCSVFileNameWithSuffix(object, CSVConstants.BEFORE_SUFFIX);
			csvInputFile = new File(beforeCopyPath);
		}
		return csvInputFile;
	}

	protected File getOutputFile(EObject object) {
		final File csvOutputFile;
		if (this.afterImportFromCSVs) {
			csvOutputFile = new File(this.csvSettings.getConversionDirectoryPath() + File.separator
					+ CSVConstants.AFTER_FOLDER + File.separator + getCSVFileName(object));
		} else {
			csvOutputFile = new File(
					this.csvSettings.getConversionDirectoryPath() + File.separator + getCSVFileName(object));
		}
		return csvOutputFile;
	}

	protected CSVParser getCSVParser(File csvOutputFile) throws IOException {
		// @formatter:off
		CSVFormat csvParserFormat = CSVFormat.EXCEL
				.withDelimiter(this.csvSettings.getFieldDelimiter())
				.withQuoteMode(QuoteMode.ALL_NON_NULL)
				.withQuote(this.csvSettings.getTextDelimiter())
				.withRecordSeparator(this.csvSettings.getLineSeparator())
				.withFirstRecordAsHeader()
				.withIgnoreEmptyLines()
				.withNullString("");
		// @formatter:on
		return new CSVParser(new FileReader(csvOutputFile), csvParserFormat);
	}

	protected Collection<String> createHeaderRecord(Set<String> attributes, Set<String> nonContainmentReferences,
			Set<String> containmentReferences) {
		Collection<String> record = new LinkedList<>();
		record.add(CSVSettings.TO_CREATE_COLUMN);
		record.add(CSVSettings.CREATION_DATE_COLUMN);
		record.add(CSVSettings.CREATION_TIME_COLUMN);
		record.add(CSVSettings.TO_UPDATE_COLUMN);
		record.add(CSVSettings.LAST_UPDATE_DATE_COLUMN);
		record.add(CSVSettings.LAST_UPDATE_TIME_COLUMN);
		record.add(CSVSettings.TO_DELETE_COLUMN);
		record.add(CSVSettings.DELETION_DATE_COLUMN);
		record.add(CSVSettings.DELETION_TIME_COLUMN);
		record.addAll(attributes);
		record.addAll(nonContainmentReferences);
		record.addAll(containmentReferences);
		return record;
	}

	protected Collection<Object> createRecord(EObject object, Collection<Object> attributes,
			Collection<Object> nonContainmentReferences, Collection<Object> containmentReferences,
			Map<String, CSVRecord> idToCSVRecordMap) {
		CSVRecord existingRecord = idToCSVRecordMap.get(EcoreUtil.getID(object));

		LinkedList<Object> record = new LinkedList<>();
		record.add(handleCreationField(object));
		record.add(handleCreationDateField(object, existingRecord));
		record.add(handleCreationTimeField(object, existingRecord));
		record.add(handleUpdateField(object));
		record.add(handleLastUpdateDateField(object, existingRecord));
		record.add(handleLastUpdateTimeField(object, existingRecord));
		record.add(handleDeletionField(object));
		record.add(handleDeletionDateField(object, existingRecord));
		record.add(handleDeletionTimeField(object, existingRecord));
		record.addAll(attributes);
		record.addAll(referencesRecordPart(nonContainmentReferences));
		record.addAll(referencesRecordPart(containmentReferences));
		return record;
	}

	protected String genTodayDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return OffsetDateTime.now().format(formatter);
	}

	protected String genNowTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;
		return OffsetTime.now().format(formatter);
	}

	protected String handleCreationField(EObject object) {
		return null;
	}

	protected String handleCreationDateField(EObject object, CSVRecord existingRecord) {
		if (existingRecord != null) {
			String existingDate = existingRecord.get(CSVSettings.CREATION_DATE_COLUMN);
			if (existingDate != null && !existingDate.isEmpty()) {
				return existingDate;
			}
		}
		return genTodayDate();
	}

	protected String handleCreationTimeField(EObject object, CSVRecord existingRecord) {
		if (existingRecord != null) {
			String existingTime = existingRecord.get(CSVSettings.CREATION_TIME_COLUMN);
			if (existingTime != null && !existingTime.isEmpty()) {
				return existingTime;
			}
		}
		return genNowTime();
	}

	protected String handleUpdateField(EObject object) {
		return null;
	}

	protected String handleLastUpdateDateField(EObject object, CSVRecord existingRecord) {
		return genTodayDate();
	}

	protected String handleLastUpdateTimeField(EObject object, CSVRecord existingRecord) {
		return genNowTime();
	}

	protected String handleDeletionField(EObject object) {
		return null;
	}

	protected String handleDeletionDateField(EObject object, CSVRecord existingRecord) {
		if (existingRecord != null) {
			String existingDate = existingRecord.get(CSVSettings.DELETION_DATE_COLUMN);
			if (existingDate != null && !existingDate.isEmpty()) {
				return existingDate;
			}
		}
		return null;
	}

	protected String handleDeletionTimeField(EObject object, CSVRecord existingRecord) {
		if (existingRecord != null) {
			String existingTime = existingRecord.get(CSVSettings.DELETION_TIME_COLUMN);
			if (existingTime != null && !existingTime.isEmpty()) {
				return existingTime;
			}
		}
		return null;
	}

	protected Collection<Object> referencesRecordPart(Collection<Object> references) {
		Collection<Object> record = new LinkedList<>();
		for (Object ref : references) {
			if (ref instanceof EObject) {
				record.add(EcoreUtil.getID((EObject) ref));
				nbReferences++;
			} else if (ref instanceof Collection<?>) {
				Collection<String> arrayCell = new LinkedList<>();
				for (Object valueItem : (Collection<?>) ref) {
					if (valueItem instanceof EObject) {
						arrayCell.add(EcoreUtil.getID((EObject) valueItem));
						nbReferences++;
					}
				}
				if (arrayCell.isEmpty()) {
					record.add(null);
				} else {
					record.add(CSVSettings.LIST_BEGIN + String.join(CSVSettings.LIST_SEPARATOR, arrayCell)
							+ CSVSettings.LIST_END);
				}
			} else if (ref == null) {
				record.add(null);
			}
		}
		return record;
	}

	protected void handleChildren(Map<String, Object> containmentReferences, CSVFormat format, SubMonitor monitor)
			throws IOException {
		SubMonitor subMonitor = monitor.split(1);
		for (Entry<String, Object> entry : containmentReferences.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof EObject) {
				generateEObjectCSV((EObject) value, format, subMonitor);
			} else if (value instanceof Collection<?>) {
				handleManyChildren(value, format, subMonitor);
			}
		}
	}

	protected void handleManyChildren(Object value, CSVFormat format, SubMonitor monitor) throws IOException {
		for (Object valueItem : (Collection<?>) value) {
			if (valueItem instanceof EObject) {
				generateEObjectCSV((EObject) valueItem, format, monitor);
			}
		}
	}

	protected String getCSVFileName(EObject object) {
		return object.eClass().getEPackage().getName() + "." + object.eClass().getName() + CSVConstants.CSV_EXT;
	}

	protected String getCSVFileNameWithSuffix(EObject object, String suffix) {
		return object.eClass().getEPackage().getName() + "." + object.eClass().getName() + suffix
				+ CSVConstants.CSV_EXT;
	}

	public void setAfterImportFromCSVs(boolean after) {
		this.afterImportFromCSVs = after;
	}

	/**
	 * Get nb references.
	 * 
	 * @return the nbReferences
	 */
	public int getNbReferences() {
		return nbReferences;
	}
}
