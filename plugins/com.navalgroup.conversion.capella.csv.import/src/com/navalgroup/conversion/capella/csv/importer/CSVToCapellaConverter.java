/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.viewpoint.DAnalysisSessionEObject;

import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;
import com.navalgroup.conversion.capella.csv.export.CapellaToCSVConverter;

public class CSVToCapellaConverter {

	/**
	 * The feature message.
	 */
	protected static final String THE_FEATURE = "The feature ";

	/**
	 * The class cast error message.
	 */
	protected static final String DOES_NOT_HANDLE_ELEMENT_OF_TYPE = ") does not handle element of type ";

	/**
	 * The class cast error message.
	 */
	protected static final String DOES_NOT_HANDLE_ELEMENTS_OF_TYPE = ") does not handle elements of type ";

	/**
	 * In message.
	 */
	protected static final String IN = " in ";

	/**
	 * Line message (begin part).
	 */
	protected static final String LINE = " (line ";

	/**
	 * Parsing error beginning message.
	 */
	protected static final String PARSING_ERROR_IN_MESSAGE = "Parsing error in ";

	/**
	 * Semi-colon.
	 */
	protected static final String SEMICOLON = " : ";

	/**
	 * The pattern of temporary IDs. A temporary ID must be set for each new element
	 * from a CSV file.
	 */
	protected static final String NEW_TMP_ID_PATTERN = "%.*%";

	/**
	 * The columns that should not be parsed by the update step.
	 */
	protected static Collection<String> columnsToNotParse = Arrays.asList(CSVSettings.TO_CREATE_COLUMN,
			CSVSettings.CREATION_DATE_COLUMN, CSVSettings.CREATION_TIME_COLUMN, CSVSettings.TO_UPDATE_COLUMN,
			CSVSettings.LAST_UPDATE_DATE_COLUMN, CSVSettings.LAST_UPDATE_TIME_COLUMN, CSVSettings.TO_DELETE_COLUMN,
			CSVSettings.DELETION_DATE_COLUMN, CSVSettings.DELETION_TIME_COLUMN, CSVSettings.ID_COLUMN);

	/**
	 * Any element of the existing Capella target model.
	 */
	protected EObject anyElement;

	/**
	 * The {@link CSVSettings} that will be applied to the converter.
	 */
	protected CSVSettings csvSettings;

	/**
	 * The directory containing all CSV files to import.
	 */
	protected File csvInputDirectory;

	/**
	 * The Sirius session associated to the existing Capella target model.
	 */
	protected Session siriusSession;

	/**
	 * A map <ID, EObject> containing all new elements (with an "x" in the "to
	 * create" column). A new element is an eObject that has been created an added
	 * into the Capella target model, with its definitive ID.
	 */
	protected Map<String, EObject> newObjectsMap;

	/**
	 * A map <ID, EObject> containing all unattached elements. An unattached element
	 * may appear in case of a move.
	 */
	protected Map<String, EObject> unattachedObjectsMap;

	/**
	 * The status returning by the whole process.
	 */
	protected IStatus status;

	/**
	 * Constructor.
	 * 
	 * @param anyElement
	 *            EObject
	 * @param settings
	 *            CSVSettings
	 */
	public CSVToCapellaConverter(EObject anyElement, CSVSettings settings) {
		this.anyElement = anyElement;
		this.csvSettings = settings;
		this.newObjectsMap = new HashMap<String, EObject>();
		this.unattachedObjectsMap = new HashMap<String, EObject>();
		this.status = Status.OK_STATUS;
		String importDirectoryPath = this.csvSettings.getConversionDirectoryPath();
		this.csvInputDirectory = new File(importDirectoryPath);
		if (!this.csvInputDirectory.exists()) {
			this.csvInputDirectory.mkdirs();
		}
		Optional<Session> sessionOpt = Session.of(this.anyElement);
		if (sessionOpt.isPresent()) {
			this.siriusSession = sessionOpt.get();
		}
	}

	/**
	 * Import CSVs.
	 * 
	 * @param subMonitor
	 *            SubMonitor
	 * @return IStatus
	 */
	public IStatus importCSVsIntoCapella(SubMonitor subMonitor) {
		this.importCSVsIntoCapella(this.siriusSession, this.csvSettings, subMonitor);
		return this.status;
	}

	protected void importCSVsIntoCapella(Session session, CSVSettings settings, SubMonitor subMonitor) {
		// @formatter:off
		CSVFormat csvFormat = CSVFormat.EXCEL
				.withDelimiter(settings.getFieldDelimiter())
				.withQuoteMode(QuoteMode.ALL_NON_NULL)
				.withQuote(settings.getTextDelimiter())
				.withRecordSeparator(settings.getLineSeparator())
				.withFirstRecordAsHeader()
				.withIgnoreEmptyLines()
				.withNullString("");
		// @formatter:on

		TransactionalEditingDomain ted = session.getTransactionalEditingDomain();
		ChangeRecorder recorder = new ChangeRecorder(ted.getResourceSet());
		RecordingCommand recordingCommand = new RecordingCommand(ted) {
			@Override
			protected void doExecute() {
				try {
					// Check every file name match with an existing Capella element type
					checkFilesNames(csvFormat, session, subMonitor);
					handleDeletions(settings, csvFormat, subMonitor);
					handleCreations(settings, csvFormat, session, subMonitor);
					handleUpdates(settings, csvFormat, subMonitor);
				} catch (CSVToCapellaException e) {
					Activator.logError(e.getMessage());
					status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
					// Rollback
					recorder.endRecording().apply();
				} catch (OperationCanceledException e) {
					// Rollback
					recorder.endRecording().apply();
				}
			}
		};
		ted.getCommandStack().execute(recordingCommand);

		// if import process has been well executed, save and export to csv in a folder
		// named '_after'
		SubMonitor genMonitor = subMonitor.split(60);
		if (this.status == Status.OK_STATUS) {
			CapellaToCSVConverter exportToCSVConverter = new CapellaToCSVConverter(this.anyElement, settings);
			exportToCSVConverter.setAfterImportFromCSVs(true);
			IStatus exportStatus = exportToCSVConverter.generateEObjectCSV(genMonitor);
			this.status = exportStatus;
		}
		// Display new objects that have not been imported (because they have not been
		// put in a containment reference elsewhere)
		if (this.status == Status.OK_STATUS) {
			IStatus newObjectsStatus = checkNewObjectsHaveBeenImported();
			this.status = newObjectsStatus;
		}
		session.save(new NullProgressMonitor());
	}

	protected Collection<File> getFiles(CSVFormat csvFormat) {
		return Stream.of(this.csvInputDirectory.listFiles())
				.filter(f -> f.isFile() && f.getPath().endsWith(CSVConstants.CSV_EXT)).collect(Collectors.toList());
	}

	protected EObject getEObjectFromID(String id) {
		Collection<Resource> semanticResource = this.siriusSession.getSemanticResources();
		EObject eObject = null;
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
		if (eObject == null) {
			// May be this is a new object with id %ID%, search in the new objects map.
			eObject = this.newObjectsMap.get(id);
			if (eObject == null) {
				// May be this is an unattached object, search in the unattached objects map.
				eObject = this.unattachedObjectsMap.get(id);
			}
		}
		return eObject;
	}

	protected void checkFilesNames(CSVFormat csvFormat, Session session, SubMonitor subMonitor)
			throws CSVToCapellaException {
		Collection<File> files = getFiles(csvFormat);
		for (File file : files) {
			String name = file.getName();
			String packageAndClassName = name.replace(CSVConstants.CSV_EXT, "");
			EObject newObject = ConversionUtil.create(session, packageAndClassName);
			if (newObject == null) {
				throw new CSVToCapellaException("The file " + file.getName()
						+ " does not correspond to any type in Capella. Please fix the file name.");
			}
		}
	}

	protected void handleDeletions(CSVSettings settings, CSVFormat csvFormat, SubMonitor subMonitor)
			throws CSVToCapellaException {
		SubMonitor deletionsMonitor = subMonitor.split(20);
		Collection<File> files = getFiles(csvFormat);
		deletionsMonitor.setWorkRemaining(files.size());
		for (File file : files) {
			try {
				CSVParser.parse(file, settings.getCharacterSet(), csvFormat).forEach(this::handleRecordDeletion);
			} catch (IOException | IllegalArgumentException e) {
				throw new CSVToCapellaException(PARSING_ERROR_IN_MESSAGE + file.getName() + SEMICOLON + e.getMessage());
			}
			deletionsMonitor.split(1);
		}
	}

	protected void handleRecordDeletion(CSVRecord csvRecord) {
		String needDelete = csvRecord.get(CSVSettings.TO_DELETE_COLUMN);
		if (needDelete != null && !needDelete.isEmpty()) {
			String id = csvRecord.get(CSVSettings.ID_COLUMN);
			if (id != null && !id.isEmpty()) {
				Collection<Resource> semanticResource = this.siriusSession.getSemanticResources();
				EObject eObject = null;
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
				if (eObject != null) {
					EcoreUtil.delete(eObject);
				}
			}
		}
	}

	protected void handleCreations(CSVSettings settings, CSVFormat csvFormat, Session session, SubMonitor subMonitor)
			throws CSVToCapellaException {
		SubMonitor creationsMonitor = subMonitor.split(20);
		Collection<File> files = getFiles(csvFormat);
		creationsMonitor.setWorkRemaining(files.size());

		for (File file : files) {
			CSVParser csvParser = null;
			try {
				csvParser = CSVParser.parse(file, settings.getCharacterSet(), csvFormat);
			} catch (IOException | IllegalArgumentException e) {
				throw new CSVToCapellaException(PARSING_ERROR_IN_MESSAGE + file.getName() + SEMICOLON + e.getMessage());
			}
			for (CSVRecord csvRecord : csvParser) {
				handleRecordCreation(csvRecord, file, session);
			}
			creationsMonitor.split(1);
		}
	}

	protected void handleRecordCreation(CSVRecord csvRecord, File file, Session session) throws CSVToCapellaException {
		String needCreate = csvRecord.get(CSVSettings.TO_CREATE_COLUMN);
		if (needCreate != null && !needCreate.isEmpty()) {
			String id = csvRecord.get(CSVSettings.ID_COLUMN);
			if (id != null && Pattern.matches(NEW_TMP_ID_PATTERN, id.trim())) {
				// Only create the object without setting attributes and references.
				// Attributes and references will be set in a second time when updating objects.
				String name = file.getName();
				String packageAndClassName = name.replace(CSVConstants.CSV_EXT, "");
				EObject newObject = ConversionUtil.create(session, packageAndClassName);
				if (newObject != null) {
					this.newObjectsMap.put(id, newObject);
				} else {
					throw new CSVToCapellaException("Cannot create an instance of " + name + " in Capella.");
				}
			} else {
				long recordNumber = csvRecord.getRecordNumber();
				recordNumber += 1;
				throw new CSVToCapellaException("The ID (" + id + ") in " + file.getName() + LINE + recordNumber
						+ ") is not suitable for importing in Capella.");
			}
		}
	}

	protected void handleUpdates(CSVSettings settings, CSVFormat csvFormat, SubMonitor subMonitor)
			throws CSVToCapellaException {
		SubMonitor updatesMonitor = subMonitor.split(20);
		Collection<File> files = getFiles(csvFormat);
		updatesMonitor.setWorkRemaining(files.size());
		for (File file : files) {
			CSVParser csvParser = null;
			try {
				csvParser = CSVParser.parse(file, settings.getCharacterSet(), csvFormat);
			} catch (IOException | IllegalArgumentException e) {
				throw new CSVToCapellaException(PARSING_ERROR_IN_MESSAGE + file.getName() + SEMICOLON + e.getMessage());
			}
			for (CSVRecord csvRecord : csvParser) {
				handleRecordUpdate(csvRecord, file.getName());
			}
			updatesMonitor.split(1);
		}
	}

	protected void handleRecordUpdate(CSVRecord csvRecord, String fileName) throws CSVToCapellaException {
		String needUpdate = csvRecord.get(CSVSettings.TO_UPDATE_COLUMN);
		String needCreate = csvRecord.get(CSVSettings.TO_CREATE_COLUMN);

		if ("x".equalsIgnoreCase(needUpdate) || "x".equalsIgnoreCase(needCreate)) {
			String id = csvRecord.get(CSVSettings.ID_COLUMN);
			if (id != null && !id.isEmpty()) {
				EObject eObject = getEObjectFromID(id);
				if (eObject != null) {
					Map<String, String> recordMap = csvRecord.toMap();
					for (Entry<String, String> entry : recordMap.entrySet()) {
						handleRecordCellUpdate(eObject, entry.getKey(), entry.getValue(), fileName,
								csvRecord.getRecordNumber() + 1);
					}
				}
			}
		}
	}

	protected void handleRecordCellUpdate(EObject eObject, String key, String value, String fileName, long recordLine)
			throws CSVToCapellaException {
		if (!columnsToNotParse.contains(key)) {
			EStructuralFeature feature = eObject.eClass().getEStructuralFeature(key);
			if (feature instanceof EAttribute) {
				handleAttributeCellUpdate(eObject, (EAttribute) feature, value, fileName, recordLine);
			} else if (feature instanceof EReference) {
				handleReferenceCellUpdate(eObject, (EReference) feature, value, fileName, recordLine);
			} else {
				// The feature does not exist, raise exception
				throw new CSVToCapellaException("The column " + key + IN + fileName + " does not exist in Capella.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void handleAttributeCellUpdate(EObject eObject, EAttribute feature, String value, String fileName,
			long recordLine) throws CSVToCapellaException {
		if (feature.isMany()) {
			EClassifier eType = feature.getEType();
			if (eType instanceof EDataType) {
				eObject.eUnset(feature);
				Object manyAttributesList = eObject.eGet(feature);
				if (manyAttributesList instanceof Collection<?>) {
					Collection<String> elementsToImport = csvAttributesValuesToList(value);
					for (String elementToImport : elementsToImport) {
						try {
							Object valueFromString = EcoreUtil.createFromString((EDataType) eType, elementToImport);
							((Collection<Object>) manyAttributesList).add(valueFromString);
						} catch (ArrayStoreException | IllegalArgumentException e) {
							// Try to add some elements who don't fit in the list
							throw new CSVToCapellaException(THE_FEATURE + feature.getName() + IN + fileName + LINE
									+ recordLine + " only handle elements of type " + eType.getInstanceClassName());
						}
					}
				}
			}
		} else {
			EClassifier eType = feature.getEType();
			if (eType instanceof EDataType) {
				try {
					Object valueFromString = EcoreUtil.createFromString((EDataType) eType, value);
					if (valueFromString == null) {
						Object defaultValue = eType.getDefaultValue();
						if (defaultValue != null) {
							valueFromString = defaultValue;
						}
					}
					eObject.eSet(feature, valueFromString);
				} catch (ClassCastException | IllegalArgumentException e) {
					throw new CSVToCapellaException(THE_FEATURE + feature.getName() + IN + fileName + LINE + recordLine
							+ " only handle elements of type " + eType.getInstanceClassName());
				}
			}
		}
	}

	protected void handleReferenceCellUpdate(EObject eObject, EReference feature, String value, String fileName,
			long recordLine) throws CSVToCapellaException {
		if (feature.isContainment()) {
			handleContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		} else {
			handleNonContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		}
	}

	protected void handleContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (feature.isMany()) {
			handleManyContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		} else {
			handleMonoContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		}
	}

	@SuppressWarnings("unchecked")
	protected void handleManyContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (value == null || value.isEmpty()) {
			// May be the objects have been moved, so we need to keep them until the whole
			// process is done
			Object unattachedObjects = eObject.eGet(feature);
			if (unattachedObjects instanceof Collection<?>) {
				// @formatter:off
				((Collection<?>) unattachedObjects).stream()
					.filter(EObject.class::isInstance)
					.map(EObject.class::cast)
					.forEach(o -> {
						this.unattachedObjectsMap.put(EcoreUtil.getID(o), o);
					});
				// @formatter:on
				eObject.eUnset(feature);
			}
		} else {
			Object eGetResult = eObject.eGet(feature);
			if (eGetResult instanceof Collection<?>) {
				Collection<EObject> containedListElts = (Collection<EObject>) eGetResult;
				Collection<EObject> elementsToImport = csvReferenceValuesToList(value);
				for (EObject elementToImport : elementsToImport) {
					if (!containedListElts.contains(elementToImport)) {
						try {
							containedListElts.add(elementToImport);
						} catch (ArrayStoreException e) {
							// Try to add some elements who don't fit in the list
							throw new CSVToCapellaException(
									THE_FEATURE + feature.getName() + IN + fileName + LINE + recordLine
											+ DOES_NOT_HANDLE_ELEMENTS_OF_TYPE + elementToImport.eClass().getName());
						}
					}
				}
				Collection<EObject> toRemove = new HashSet<>();
				for (Object containedListElt : containedListElts) {
					if (!elementsToImport.contains(containedListElt)) {
						toRemove.add((EObject) containedListElt);
					}
				}
				((Collection<?>) containedListElts).removeAll(toRemove);
			}
		}
	}

	protected void handleMonoContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (value == null || value.isEmpty()) {
			// May be the object has been moved, so we need to keep it until the whole
			// process is done
			Object unattachedObject = eObject.eGet(feature);
			if (unattachedObject instanceof EObject) {
				String unattachedObjectID = EcoreUtil.getID((EObject) unattachedObject);
				this.unattachedObjectsMap.put(unattachedObjectID, (EObject) unattachedObject);
				eObject.eUnset(feature);
			}
		} else {
			EObject eObjectFromID = getEObjectFromID(value);
			try {
				eObject.eSet(feature, eObjectFromID);
			} catch (ClassCastException e) {
				throw new CSVToCapellaException(THE_FEATURE + feature.getName() + IN + fileName + LINE + recordLine
						+ DOES_NOT_HANDLE_ELEMENT_OF_TYPE + eObjectFromID.eClass().getName());
			}
		}
	}

	protected void handleNonContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (feature.isMany()) {
			handleManyNonContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		} else {
			handleMonoNonContainmentReferenceCellUpdate(eObject, feature, value, fileName, recordLine);
		}
	}

	@SuppressWarnings("unchecked")
	protected void handleManyNonContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (value == null || value.isEmpty()) {
			eObject.eUnset(feature);
		} else {
			Object eGetResult = eObject.eGet(feature);
			if (eGetResult instanceof Collection<?>) {
				Collection<EObject> nonContainedListElts = (Collection<EObject>) eGetResult;
				Collection<EObject> elementsToImport = csvReferenceValuesToList(value);
				for (EObject elementToImport : elementsToImport) {
					if (!nonContainedListElts.contains(elementToImport)) {
						try {
							nonContainedListElts.add(elementToImport);
						} catch (ArrayStoreException e) {
							// Try to add some elements who don't fit in the list
							throw new CSVToCapellaException(
									THE_FEATURE + feature.getName() + IN + fileName + LINE + recordLine
											+ DOES_NOT_HANDLE_ELEMENTS_OF_TYPE + elementToImport.eClass().getName());
						}
					}
				}
				Collection<EObject> toRemove = new HashSet<>();
				for (Object nonContainedListElt : nonContainedListElts) {
					if (!elementsToImport.contains(nonContainedListElt)) {
						toRemove.add((EObject) nonContainedListElt);
					}
				}
				((Collection<?>) nonContainedListElts).removeAll(toRemove);
			}
		}
	}

	protected void handleMonoNonContainmentReferenceCellUpdate(EObject eObject, EReference feature, String value,
			String fileName, long recordLine) throws CSVToCapellaException {
		if (value == null || value.isEmpty()) {
			eObject.eUnset(feature);
		} else {
			EObject eObjectFromID = getEObjectFromID(value);
			try {
				eObject.eSet(feature, eObjectFromID);
			} catch (ClassCastException e) {
				throw new CSVToCapellaException(THE_FEATURE + feature.getName() + IN + fileName + LINE + recordLine
						+ DOES_NOT_HANDLE_ELEMENT_OF_TYPE + eObjectFromID.eClass().getName());
			}
		}
	}

	protected Collection<EObject> csvReferenceValuesToList(String values) {
		if (values == null || values.isEmpty() || !values.startsWith(CSVSettings.LIST_BEGIN)) {
			return Collections.emptySet();
		}
		String[] ids = values.substring(1, values.length() - 1).split(CSVSettings.LIST_SEPARATOR_REGEX);
		return Stream.of(ids).map(id -> getEObjectFromID(id)).filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	protected Collection<String> csvAttributesValuesToList(String values) {
		if (values == null || values.isEmpty() || !values.startsWith(CSVSettings.LIST_BEGIN)) {
			return Collections.emptySet();
		}
		String[] ids = values.substring(1, values.length() - 1).split(CSVSettings.LIST_SEPARATOR_REGEX);
		return Stream.of(ids).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	protected IStatus checkNewObjectsHaveBeenImported() {
		// IStatus status = new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO,
		// newChildren, message, exception);
		Collection<IStatus> statuses = new ArrayList<>();
		Set<Entry<String, EObject>> entrySet = this.newObjectsMap.entrySet();
		for (Entry<String, EObject> entry : entrySet) {
			EObject value = entry.getValue();
			if (value.eContainer() == null) {
				String key = entry.getKey();
				statuses.add(
						new Status(IStatus.INFO, Activator.PLUGIN_ID, key + " (" + value.eClass().getName() + ")"));
			}
		}
		if (!statuses.isEmpty()) {
			return new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO, statuses.toArray(new IStatus[0]),
					"The following new objects have not been imported (not added to any containment references):",
					null);
		}
		return Status.OK_STATUS;
	}
}
