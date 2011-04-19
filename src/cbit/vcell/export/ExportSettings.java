package cbit.vcell.export;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (1/18/00 12:04:07 PM)
 * @author: Ion I. Moraru
 */
public class ExportSettings implements ASCIISettingsPanelListener, RasterSettingsPanelListener, ExportConstants, java.beans.PropertyChangeListener {
	private ASCIISettingsPanel ivjASCIISettingsPanel1 = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldSelectedFormat = 0;
	private cbit.vcell.export.server.FormatSpecificSpecs fieldFormatSpecificSpecs = null;
	private int fieldSimDataType = 0;
	private JPanel ivjJDialogContentPane = null;
	private JDialog ivjJDialogASCIISettings = null;
	private JDialog ivjJDialogMediaSettings = null;
	private boolean closedOK = false;
	private JPanel ivjJDialogContentPane3 = null;
	private JDialog ivjJDialogRasterSettings = null;
	private RasterSettingsPanel ivjRasterSettingsPanel1 = null;

/**
 * ExportSettings constructor comment.
 */
public ExportSettings() {
	super();
	initialize();
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Comment
 */
private void closeCancel(JDialog dialog) {
	setClosedOK(false);
	dialog.dispose();
}


/**
 * Comment
 */
private void closeOK(JDialog dialog) {
	setClosedOK(true);
	dialog.dispose();
}

/**
 * connEtoC10:  (ASCIISettingsPanel1.ASCIISettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(ivjJDialogASCIISettings);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (RasterSettingsPanel1.rasterSettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(ivjJDialogRasterSettings);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  ( (ASCIISettingsPanel1,ASCIISettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(ivjJDialogASCIISettings);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  ( (RasterSettingsPanel1,rasterSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(ivjJDialogRasterSettings);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (RasterSettingsPanel1.rasterSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getRasterSettingsPanel1().getRasterSpecs());
		connEtoC7();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (ASCIISettingsPanel1.ASCIISettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getASCIISettingsPanel1().getAsciiSpecs());
		connEtoC6();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP7SetTarget:  (ExportSettings.simDataType <--> ASCIISettingsPanel1.simDataType)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		getASCIISettingsPanel1().setSimDataType(this.getSimDataType());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

/**
 * Return the ASCIISettingsPanel1 property value.
 * @return cbit.vcell.export.ASCIISettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ASCIISettingsPanel getASCIISettingsPanel1() {
	if (ivjASCIISettingsPanel1 == null) {
		try {
			ivjASCIISettingsPanel1 = new cbit.vcell.export.ASCIISettingsPanel();
			ivjASCIISettingsPanel1.setName("ASCIISettingsPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjASCIISettingsPanel1;
}

/**
 * Gets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @return The formatSpecificSpecs property value.
 * @see #setFormatSpecificSpecs
 */
public cbit.vcell.export.server.FormatSpecificSpecs getFormatSpecificSpecs() {
	return fieldFormatSpecificSpecs;
}


/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogASCIISettings(Frame reference) {
	if (ivjJDialogASCIISettings == null) {
		try {
			ivjJDialogASCIISettings = new javax.swing.JDialog(reference);
			ivjJDialogASCIISettings.setName("JDialogASCIISettings");
			ivjJDialogASCIISettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogASCIISettings.setBounds(123, 502, 176, 183);
			ivjJDialogASCIISettings.setModal(true);
			ivjJDialogASCIISettings.setTitle("ASCII Settings");
			ivjJDialogASCIISettings.add(getJDialogContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogASCIISettings;
}

/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getASCIISettingsPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}

/**
 * Return the JDialogContentPane3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane3() {
	if (ivjJDialogContentPane3 == null) {
		try {
			ivjJDialogContentPane3 = new javax.swing.JPanel();
			ivjJDialogContentPane3.setName("JDialogContentPane3");
			ivjJDialogContentPane3.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane3().add(getRasterSettingsPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane3;
}

/**
 * Return the JDialog3 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogMediaSettings(Frame reference,int mediaType,boolean selectionHasVolumeVars,boolean selectionHasMembraneVars,String descriptor) {
	if (ivjJDialogMediaSettings == null) {
		try {
			ivjJDialogMediaSettings = new javax.swing.JDialog(reference);
			ivjJDialogMediaSettings.setName("JDialogMediaSettings");
			ivjJDialogMediaSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogMediaSettings.setBounds(422, 85, 234, 368);
			ivjJDialogMediaSettings.setModal(true);
			ivjJDialogMediaSettings.setContentPane(getMediaSettingsPanel1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	getMediaSettingsPanel1().configure(mediaType, selectionHasVolumeVars,selectionHasMembraneVars);
	ivjJDialogMediaSettings.setTitle(descriptor+" Settings");
	return ivjJDialogMediaSettings;
}

/**
 * Return the JDialogRasterSettings property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogRasterSettings(Frame reference) {
	if (ivjJDialogRasterSettings == null) {
		try {
			ivjJDialogRasterSettings = new javax.swing.JDialog(reference);
			ivjJDialogRasterSettings.setName("JDialogRasterSettings");
			ivjJDialogRasterSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogRasterSettings.setBounds(393, 536, 180, 214);
			ivjJDialogRasterSettings.setModal(true);
			ivjJDialogRasterSettings.setTitle("Raster Settings");
			ivjJDialogRasterSettings.add(getJDialogContentPane3());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogRasterSettings;
}

/**
 * Return the MovieSettingsPanel1 property value.
 * @return cbit.vcell.export.MovieSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MediaSettingsPanel ivjMediaSettingsPanel1;
private MediaSettingsPanel getMediaSettingsPanel1() {
	if (ivjMediaSettingsPanel1 == null) {
		try {
			ivjMediaSettingsPanel1 = new MediaSettingsPanel();
			ivjMediaSettingsPanel1.setName("MediaSettingsPanel1");
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMediaSettingsPanel1;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Return the RasterSettingsPanel1 property value.
 * @return cbit.vcell.export.RasterSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private RasterSettingsPanel getRasterSettingsPanel1() {
	if (ivjRasterSettingsPanel1 == null) {
		try {
			ivjRasterSettingsPanel1 = new cbit.vcell.export.RasterSettingsPanel();
			ivjRasterSettingsPanel1.setName("RasterSettingsPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRasterSettingsPanel1;
}


/**
 * Gets the selectedFormat property (int) value.
 * @return The selectedFormat property value.
 * @see #setSelectedFormat
 */
public int getSelectedFormat() {
	return fieldSelectedFormat;
}


/**
 * Gets the simDataType property (int) value.
 * @return The simDataType property value.
 * @see #setSimDataType
 */
public int getSimDataType() {
	return fieldSimDataType;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getASCIISettingsPanel1().addASCIISettingsPanelListener(this);
	getMediaSettingsPanel1().addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals(MediaSettingsPanel.OK_ACTION_COMMAND)){
				setFormatSpecificSpecs(getMediaSettingsPanel1().getSpecs());
				closeOK(ivjJDialogMediaSettings);
			}else if(e.getActionCommand().equals(MediaSettingsPanel.CANCEL_ACTION_COMMAND)){
				closeCancel(ivjJDialogMediaSettings);
			}
		}
	});
	getRasterSettingsPanel1().addRasterSettingsPanelListener(this);
	connPtoP7SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:59:37 AM)
 * @return boolean
 */
private boolean isClosedOK() {
	return closedOK;
}


/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getASCIISettingsPanel1()) 
		connEtoC10(newEvent);
	if (newEvent.getSource() == getRasterSettingsPanel1()) 
		connEtoC11(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the ASCIISettingsPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getASCIISettingsPanel1()) 
		connEtoM4(newEvent);
	if (newEvent.getSource() == getRasterSettingsPanel1()) 
		connEtoM1(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ExportSettings aExportSettings;
		aExportSettings = new ExportSettings();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.lang.Object");
		exception.printStackTrace(System.out);
	}
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("simDataType"))) 
		connPtoP7SetTarget();
	// user code begin {2}
	// user code end
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:59:37 AM)
 * @param newClosedOK boolean
 */
private void setClosedOK(boolean newClosedOK) {
	closedOK = newClosedOK;
}


/**
 * Sets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @param displayPreferences The new value for the property.
 * @see #getDisplayPreferences
 */
public void setDisplayPreferences(cbit.vcell.simdata.gui.DisplayPreferences[] displayPreferences,String[] variableNames,int viewZoom) {
	getMediaSettingsPanel1().setDisplayPreferences(displayPreferences, variableNames,viewZoom);
}


/**
 * Sets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @param formatSpecificSpecs The new value for the property.
 * @see #getFormatSpecificSpecs
 */
public void setFormatSpecificSpecs(cbit.vcell.export.server.FormatSpecificSpecs formatSpecificSpecs) {
	cbit.vcell.export.server.FormatSpecificSpecs oldValue = fieldFormatSpecificSpecs;
	fieldFormatSpecificSpecs = formatSpecificSpecs;
	firePropertyChange("formatSpecificSpecs", oldValue, formatSpecificSpecs);
}


/**
 * Sets the selectedFormat property (int) value.
 * @param selectedFormat The new value for the property.
 * @see #getSelectedFormat
 */
public void setSelectedFormat(int selectedFormat) {
	int oldValue = fieldSelectedFormat;
	fieldSelectedFormat = selectedFormat;
	firePropertyChange("selectedFormat", new Integer(oldValue), new Integer(selectedFormat));
}


/**
 * Sets the simDataType property (int) value.
 * @param simDataType The new value for the property.
 * @see #getSimDataType
 */
public void setSimDataType(int simDataType) {
	int oldValue = fieldSimDataType;
	fieldSimDataType = simDataType;
	firePropertyChange("simDataType", new Integer(oldValue), new Integer(simDataType));
}

public void setSliceCount(int sliceCount){
	getMediaSettingsPanel1().setSliceCount(sliceCount);
}
public void setTimeSpecs(TimeSpecs timeSpecs){
	getMediaSettingsPanel1().setTimeSpecs(timeSpecs);
}
public void setImageSizeCalculationInfo(CartesianMesh mesh,int normalAxis){
	getMediaSettingsPanel1().setImageSizeCalculationInfo(mesh, normalAxis);
}
public void setIsSmoldyn(boolean isSmoldyn){
	getMediaSettingsPanel1().setIsSmoldyn(isSmoldyn);
}
public void setSimulationSelector(ExportSpecs.SimulationSelector simulationSelector){
	getASCIISettingsPanel1().setSimulationSelector(simulationSelector);
}
/**
 * Comment
 */
public boolean showFormatSpecificDialog(Frame reference,boolean selectionHasVolumeVars,boolean selectionHasMembraneVars) {
	JDialog dialogToShow = null;
	setClosedOK(false);
	switch (getSelectedFormat()) {
		case FORMAT_CSV:
			dialogToShow = getJDialogASCIISettings(reference);
			break;
		case FORMAT_QUICKTIME:
		case FORMAT_ANIMATED_GIF:
			dialogToShow = getJDialogMediaSettings(reference,getSelectedFormat(),selectionHasVolumeVars,selectionHasMembraneVars,"Movie");
			break;
		case FORMAT_GIF:
		case FORMAT_JPEG:
			dialogToShow = getJDialogMediaSettings(reference,getSelectedFormat(),selectionHasVolumeVars,selectionHasMembraneVars,"Image");
			break;
		case FORMAT_NRRD:
			dialogToShow = getJDialogRasterSettings(reference);
			break;
		case FORMAT_UCD:
			int result = PopupGenerator.showComponentOKCancelDialog(new JDialog(), new JTextField("Continue UCD Export?"), "UCD Export");
			if(result == JOptionPane.OK_OPTION){
				return true;
			}
			return false;
		case FORMAT_VTK_UNSTRUCT:
		case FORMAT_VTK_IMAGE:
			int result2 = PopupGenerator.showComponentOKCancelDialog(new JDialog(), new JTextField("Continue VTK Export?"), "VTK Export");
			if(result2 == JOptionPane.OK_OPTION){
				return true;
			}
			return false;

//			dialogToShow = getJDialogRasterSettings();
//			break;
	}
	dialogToShow.pack();
	BeanUtils.centerOnComponent(dialogToShow, reference);
	dialogToShow.show();  // modal; blocks until OK'd or closed
	return isClosedOK();
}
}