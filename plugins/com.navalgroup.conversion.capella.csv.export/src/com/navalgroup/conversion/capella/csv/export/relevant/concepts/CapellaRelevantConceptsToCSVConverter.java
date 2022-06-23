/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.export.relevant.concepts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.polarsys.kitalpha.emde.model.Element;

import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;
import com.navalgroup.conversion.capella.csv.export.CapellaToCSVConverter;

/**
 * A converter to CSV files from a given {@link EObject}. 
 * @formatter:off
 * The conversion has several steps:
 * - iterate on on the given {@link EObject} children recursively to get only the relevant class ids 
 * 		-> all packages + semantic browser related elements
 * - generate one file for each type of model element
 * 		-> in a file, a model element contains all its attributes + all semantic browser relations 
 * @formatter:on
 * 
 * @author nlepine
 *
 */
public class CapellaRelevantConceptsToCSVConverter extends CapellaToCSVConverter {

	/**
	 * Relevants IDs.
	 */
	Set<String> relevantIDs = new HashSet<>();
	/**
	 * Export containments ?
	 */
	private boolean exportContainments;

	/**
	 * Constructor.
	 * 
	 * @param rootElement
	 *            EObject
	 * @param csvSettings
	 *            CSVSettings
	 * @param exportContainments
	 */
	public CapellaRelevantConceptsToCSVConverter(EObject rootElement, CSVSettings csvSettings,
			boolean exportContainments) {
		super(rootElement, csvSettings);
		this.exportContainments = exportContainments;
	}

	/**
	 * Compute all relevant ids for conversion: Structure + results from Semantic
	 * Browser.
	 * 
	 * @param object
	 *            EObject
	 */
	public void computeRelevantIDs(EObject object) {
		String id = EcoreUtil.getID(object);

		// Check if object is Structure (Semantic Browser has no result for them but
		// they are needed for the conversion)
		if (isRelevantElement(object)) {
			if (id != null) {
				relevantIDs.add(id);
			}
		}

		// Get referenced elements from Semantic Browser
		Map<String, Object> relevantReferences = ConversionUtil.getSemanticBrowserReferences(object);
		relevantReferences.forEach((k, v) -> {
			if (v instanceof EObject) {
				insertRelevantID(object, v);
			} else if (v instanceof List<?>) {
				((List<?>) v).stream().forEach(o -> {
					if (o instanceof EObject) {
						insertRelevantID(object, o);
					}
				});
			}
		});

		// compute for children
		object.eContents().stream().forEach(o -> computeRelevantIDs(o));

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
	protected boolean isRelevantElement(EObject object) {
		return ConversionUtil.isRelevantElement(object);
	}

	private void insertRelevantID(EObject parent, Object referencedElement) {
		if (referencedElement instanceof Element) {
			String id2 = EcoreUtil.getID((EObject) referencedElement);
			if (id2 != null) {
				relevantIDs.add(id2);
				String id = EcoreUtil.getID(parent);
				if (id != null) {
					relevantIDs.add(id);
				}
			}
		} else {
			// Activator.logInfo("This element is not a Capella Element, it will not be
			// converted: " + referencedElement);
		}
	}

	protected void generateEObjectCSV(EObject object, CSVFormat format, SubMonitor monitor) throws IOException {
		monitor.setWorkRemaining(100);
		String objectId = EcoreUtil.getID(object);
		Map<String, Object> containmentReferences = ConversionUtil.getContainmentReferences(object);

		if (objectId != null && relevantIDs.contains(objectId)) {
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
			Map<String, Object> semanticBrowserReferences = ConversionUtil.getSemanticBrowserReferences(object);
			Collection<Object> containementValues = Collections.emptyList();
			if (exportContainments) {
				containementValues = containmentReferences.values();
			}

			CSVFormat csvFormatWithHeader;
			if (csvOutputFile != null && csvOutputFile.exists()) {
				csvFormatWithHeader = format;
			} else {
				Collection<String> headerRecord = createHeaderRecord(attributes.keySet(),
						semanticBrowserReferences.keySet(), containmentReferences.keySet());
				csvFormatWithHeader = format.withHeader(headerRecord.toArray(new String[headerRecord.size()]));
			}

			Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, true),
					this.csvSettings.getCharacterSet());
			try (CSVPrinter printer = new CSVPrinter(fstream, csvFormatWithHeader)) {
				printer.printRecord(createRecord(object, attributes.values(), semanticBrowserReferences.values(),
						containementValues, idToCSVRecordMap));
			}

			if (csvParser != null) {
				csvParser.close();
			}
		}

		handleChildren(containmentReferences, format, monitor);
	}

	protected Collection<Object> createRecord(EObject object, Collection<Object> attributes,
			Collection<Object> references, Collection<Object> containments, Map<String, CSVRecord> idToCSVRecordMap) {
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
		record.addAll(referencesRecordPart(references));
		record.addAll(referencesRecordPart(containments));
		return record;
	}

	protected Collection<Object> referencesRecordPart(Collection<Object> references) {
		Collection<Object> record = new LinkedList<>();
		for (Object ref : references) {
			if (ref instanceof EObject) {
				String id = EcoreUtil.getID((EObject) ref);
				if (relevantIDs.contains(id)) {
					record.add(id);
					nbReferences++;
				}
			} else if (ref instanceof Collection<?>) {
				Collection<String> arrayCell = new LinkedList<>();
				for (Object valueItem : (Collection<?>) ref) {
					if (valueItem instanceof EObject) {
						String id = EcoreUtil.getID((EObject) valueItem);
						if (relevantIDs.contains(id)) {
							arrayCell.add(id);
							nbReferences++;
						}
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

	/**
	 * Get the relevantIDs.
	 * 
	 * @return the relevantIDs
	 */
	public Set<String> getRelevantIDs() {
		return relevantIDs;
	}
}
