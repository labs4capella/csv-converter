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
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.polarsys.capella.common.libraries.LibrariesPackage;
import org.polarsys.capella.common.re.RePackage;
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

public class GenerateEmptyCSVJob implements IRunnableWithProgress {

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
	public GenerateEmptyCSVJob(CSVSettings csvSettings) {
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
		subMonitor.beginTask("Generating empty CSV files...", 100);
		for (EPackage ePackage : allPackages) {
			SubMonitor ePackageMonitor = subMonitor.split(10);
			ePackageMonitor.setWorkRemaining(100);
			Collection<EClass> allClasses = getAllClasses(ePackage);
			for (EClass eClass : allClasses) {
				try {
					SubMonitor emptyCSVMonitor = ePackageMonitor.split(2);
					generateEmptyCSV(eClass, emptyCSVMonitor);
				} catch (IOException e) {
					Activator.logError(e.getMessage());
					break;
				}
			}
		}
		subMonitor.done();
	}

	protected void generateEmptyCSV(EClass eClass, SubMonitor subMonitor) throws IOException {
		File csvOutputFile = new File(
				this.csvSettings.getConversionDirectoryPath() + File.separator + getCSVFileName(eClass));
		String csvOutputFilePath = csvOutputFile.getAbsolutePath();
		Collection<String> attributes = ConversionUtil.getAttributes(eClass);
		Collection<String> nonContainmentReferences = ConversionUtil.getNonContainmentReferences(eClass);
		Collection<String> containmentReferences = ConversionUtil.getContainmentReferences(eClass);
		Writer fstream = new OutputStreamWriter(new FileOutputStream(csvOutputFilePath, false),
				this.csvSettings.getCharacterSet());
		try (CSVPrinter printer = new CSVPrinter(fstream, this.csvFormat)) {
			printer.printRecord(createRecord(eClass, attributes, nonContainmentReferences, containmentReferences));
		}
	}

	protected Collection<String> createRecord(EClass eClass, Collection<String> attributes,
			Collection<String> nonContainmentReferences, Collection<String> containmentReferences) {
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

	protected String getCSVFileName(EClass eClass) {
		return eClass.getEPackage().getName() + "." + eClass.getName() + CSVConstants.CSV_EXT;
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
