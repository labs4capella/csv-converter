/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.conversion.capella.csv.core.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.polarsys.capella.common.libraries.LibrariesPackage;
import org.polarsys.capella.common.re.RePackage;
import org.polarsys.capella.common.ui.toolkit.browser.category.CategoryRegistry;
import org.polarsys.capella.common.ui.toolkit.browser.category.ICategory;
import org.polarsys.capella.core.data.capellacommon.CapellacommonPackage;
import org.polarsys.capella.core.data.capellacore.CapellacorePackage;
import org.polarsys.capella.core.data.capellamodeller.CapellamodellerPackage;
import org.polarsys.capella.core.data.cs.CsPackage;
import org.polarsys.capella.core.data.ctx.CtxPackage;
import org.polarsys.capella.core.data.epbs.EpbsPackage;
import org.polarsys.capella.core.data.fa.FaPackage;
import org.polarsys.capella.core.data.information.InformationPackage;
import org.polarsys.capella.core.data.information.datatype.DatatypePackage;
import org.polarsys.capella.core.data.information.datavalue.DatavaluePackage;
import org.polarsys.capella.core.data.interaction.InteractionPackage;
import org.polarsys.capella.core.data.la.LaPackage;
import org.polarsys.capella.core.data.oa.OaPackage;
import org.polarsys.capella.core.data.pa.PaPackage;
import org.polarsys.capella.core.data.pa.deployment.DeploymentPackage;
import org.polarsys.capella.core.data.requirement.RequirementPackage;

import com.navalgroup.conversion.capella.csv.core.Activator;
import com.navalgroup.conversion.capella.csv.core.CSVConstants;
import com.navalgroup.conversion.capella.csv.core.CSVSettings;
import com.navalgroup.conversion.capella.csv.core.ConversionUtil;

/**
 * Job to generate Capella Metamodel CSV.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class GenerateCapellaMetamodelCSVJob implements IRunnableWithProgress {

	/**
	 * Attribute type column.
	 */
	private static final String ATTRIBUTE_TYPE_COLUMN = "attribute_type";
	/**
	 * Feature name column.
	 */
	private static final String FEATURE_NAME_COLUMN = "feature_name";
	/**
	 * Feature type column.
	 */
	private static final String FEATURE_TYPE_COLUMN = "feature_type";
	/**
	 * Class name column.
	 */
	private static final String CLASS_NAME_COLUMN = "class_name";
	/**
	 * Reference.
	 */
	private static final String FEATURE_TYPE_REFERENCE = "reference";
	/**
	 * Attribute.
	 */
	private static final String FEATURE_TYPE_ATTRIBUTE = "attribute";
	/**
	 * Output file name.
	 */
	private static final String OUTPUT_FILE_NAME = "CapellaMetamodel";
	/**
	 * csvSettings.
	 */
	private CSVSettings csvSettings;
	/**
	 * csvFormat.
	 */
	private CSVFormat csvFormat;

	/**
	 * Constructor.
	 * 
	 * @param csvSettings
	 *            CSVSettings
	 */
	public GenerateCapellaMetamodelCSVJob(CSVSettings csvSettings) {
		this.csvSettings = csvSettings;
		// @formatter:off
		this.csvFormat = CSVFormat.EXCEL
				.withDelimiter(this.csvSettings.getFieldDelimiter())
				.withQuoteMode(QuoteMode.ALL_NON_NULL)
				.withQuote(this.csvSettings.getTextDelimiter())
				.withRecordSeparator(this.csvSettings.getLineSeparator())
				.withNullString("");
		// @formatter:on
	}

	@Override
	public void run(IProgressMonitor monitor) {
		Collection<EPackage> allPackages = getAllPackages();
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		subMonitor.beginTask("Generating Capella Metamodel CSV file...", 100);

		for (EPackage ePackage : allPackages) {
			SubMonitor ePackageMonitor = subMonitor.split(10);
			ePackageMonitor.setWorkRemaining(100);
			Collection<EClass> allClasses = getAllClasses(ePackage);
			for (EClass eClass : allClasses) {
				for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
					try {
						SubMonitor emptyCSVMonitor = ePackageMonitor.split(2);
						generateCapellaCSV(eClass, feature, this.csvFormat, emptyCSVMonitor);
					} catch (IOException e) {
						Activator.logError(e.getMessage());
						break;
					}
				}
				Set<ICategory> categories = CategoryRegistry.getInstance().gatherCategories(EcoreUtil.create(eClass));
				for (ICategory category : categories) {
					try {
						SubMonitor emptyCSVMonitor = ePackageMonitor.split(2);
						generateCapellaCSV(eClass, category, this.csvFormat, emptyCSVMonitor);
					} catch (IOException e) {
						Activator.logError(e.getMessage());
						break;
					}
				}
			}
		}
		subMonitor.done();
	}

	protected void generateCapellaCSV(EClass eClass, ICategory category, CSVFormat format, SubMonitor subMonitor)
			throws IOException {
		File csvOutputFile = new File(this.csvSettings.getConversionDirectoryPath() + File.separator + OUTPUT_FILE_NAME
				+ CSVConstants.CSV_EXT);
		String csvOutputFilePath = csvOutputFile.getAbsolutePath();
		CSVFormat csvFormatWithHeader;
		if (csvOutputFile != null && csvOutputFile.exists()) {
			csvFormatWithHeader = format;
		} else {
			Collection<String> headerRecord = createHeaderRecord();
			csvFormatWithHeader = format.withHeader(headerRecord.toArray(new String[headerRecord.size()]));
		}

		Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, true),
				this.csvSettings.getCharacterSet());
		try (CSVPrinter printer = new CSVPrinter(fstream, csvFormatWithHeader)) {
			printer.printRecord(createRecord(eClass, category));
		}
	}

	protected Collection<String> createRecord(EClass eClass, ICategory category) {
		Collection<String> record = new LinkedList<>();
		record.add(getClassQualifiedName(eClass));
		record.add(FEATURE_TYPE_REFERENCE);
		record.add(ConversionUtil.getCategoryName(category));
		record.add("");
		return record;
	}

	protected void generateCapellaCSV(EClass eClass, EStructuralFeature feature, CSVFormat format,
			SubMonitor subMonitor) throws IOException {
		File csvOutputFile = new File(this.csvSettings.getConversionDirectoryPath() + File.separator + OUTPUT_FILE_NAME
				+ CSVConstants.CSV_EXT);
		String csvOutputFilePath = csvOutputFile.getAbsolutePath();
		CSVFormat csvFormatWithHeader;
		if (csvOutputFile != null && csvOutputFile.exists()) {
			csvFormatWithHeader = format;
		} else {
			Collection<String> headerRecord = createHeaderRecord();
			csvFormatWithHeader = format.withHeader(headerRecord.toArray(new String[headerRecord.size()]));
		}

		Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, true),
				this.csvSettings.getCharacterSet());
		try (CSVPrinter printer = new CSVPrinter(fstream, csvFormatWithHeader)) {
			printer.printRecord(createRecord(eClass, feature));
		}
	}

	protected Collection<String> createRecord(EClass eClass, EStructuralFeature feature) {
		Collection<String> record = new LinkedList<>();
		record.add(getClassQualifiedName(eClass));
		record.add(getFeatureType(feature));
		record.add(feature.getName());
		record.add(getAttributeType(feature));
		return record;
	}

	/**
	 * Get class qualified name.
	 * 
	 * @param eClass
	 *            EClass
	 * @return class qualified name.
	 */
	protected String getClassQualifiedName(EClass eClass) {
		return eClass.getEPackage().getName() + "." + eClass.getName();
	}

	protected String getAttributeType(EStructuralFeature feature) {
		if (feature instanceof EAttribute) {
			return feature.getEType().getName();
		}
		return "";
	}

	protected String getFeatureType(EStructuralFeature feature) {
		return feature instanceof EAttribute ? FEATURE_TYPE_ATTRIBUTE : FEATURE_TYPE_REFERENCE;
	}

	protected Collection<String> createHeaderRecord() {
		Collection<String> record = new LinkedList<>();
		record.add(CLASS_NAME_COLUMN);
		record.add(FEATURE_TYPE_COLUMN);
		record.add(FEATURE_NAME_COLUMN);
		record.add(ATTRIBUTE_TYPE_COLUMN);
		return record;
	}

	protected Collection<EPackage> getAllPackages() {
		Collection<EPackage> ePackages = new HashSet<>();
		ePackages.add(CapellacommonPackage.eINSTANCE);
		ePackages.add(CapellacorePackage.eINSTANCE);
		ePackages.add(CapellamodellerPackage.eINSTANCE);
		ePackages.add(CsPackage.eINSTANCE);
		ePackages.add(CtxPackage.eINSTANCE);
		ePackages.add(DatatypePackage.eINSTANCE);
		ePackages.add(DatavaluePackage.eINSTANCE);
		ePackages.add(DeploymentPackage.eINSTANCE);
		ePackages.add(EpbsPackage.eINSTANCE);
		ePackages.add(FaPackage.eINSTANCE);
		ePackages.add(InformationPackage.eINSTANCE);
		ePackages.add(InteractionPackage.eINSTANCE);
		ePackages.add(LaPackage.eINSTANCE);
		ePackages.add(LibrariesPackage.eINSTANCE);
		ePackages.add(OaPackage.eINSTANCE);
		ePackages.add(PaPackage.eINSTANCE);
		ePackages.add(RePackage.eINSTANCE);
		ePackages.add(RequirementPackage.eINSTANCE);
		return ePackages;
	}

	/**
	 * Get all concrete classes from the given {@link EPackage}.
	 * 
	 * @param ePackage
	 *            the given {@link EPackage}.
	 * @return all concrete classes.
	 */
	protected Collection<EClass> getAllClasses(EPackage ePackage) {
		// @formatter:off
		return ePackage.getEClassifiers().stream()
				.filter(c -> c instanceof EClass)
				.map(c -> (EClass) c)
				.filter(c -> !c.isAbstract())
				.collect(Collectors.toList());
		// @formatter:on
	}
}
