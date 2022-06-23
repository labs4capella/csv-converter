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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.navalgroup.conversion.capella.csv.core.CSVSettings;

/**
 * A {@link IRunnableWithProgress} calling the {@link CapellaToCSVConverter}.
 * 
 * @author arichard
 *
 */
public class CapellaToCSVJob implements IRunnableWithProgress {

	/**
	 * rootElement.
	 */
	private EObject rootElement;
	/**
	 * settings.
	 */
	private CSVSettings settings;
	/**
	 * status.
	 */
	private IStatus status;

	/**
	 * Constructor.
	 * 
	 * @param rootElement
	 *            EObject
	 * @param settings
	 *            CSVSettings
	 */
	public CapellaToCSVJob(EObject rootElement, CSVSettings settings) {
		this.rootElement = rootElement;
		this.settings = settings;
	}

	public EObject getRootElement() {
		return this.rootElement;
	}

	public CSVSettings getSettings() {
		return this.settings;
	}

	@Override
	public void run(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		subMonitor.beginTask("Generating CSV files...", 100);
		CapellaToCSVConverter converter = new CapellaToCSVConverter(this.getRootElement(), this.getSettings());
		this.status = converter.generateEObjectCSV(subMonitor);
		subMonitor.done();
	}

	public IStatus getStatus() {
		return this.status;
	}
}
