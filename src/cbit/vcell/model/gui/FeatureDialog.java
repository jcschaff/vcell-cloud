package cbit.vcell.model.gui;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.vcell.util.Compare;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
/**
 * This class was generated by a SmartGuide.
 *
 *
 * showFeaturePropertyDialog is invoked in two modes:
 *
 * 1) parent!=null and child==null
 *    upon ok, it adds a new feature to the supplied parent.
 *
 * 2) parent==null and child!=null
 *    upon ok, edits the feature name
 *
 *
 */
public class FeatureDialog extends JDialog implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
	private JCheckBox overrideSizeNameCheckBox;
	private JTextField featureSizeNameJTextField;
	private JLabel featureSizeNameLabel;
	private javax.swing.JPanel ivjPanel1 = null;
	private Feature fieldParentFeature = null;
	private Feature fieldChildFeature = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameJTextField = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JButton ivjOKJButton = null;
	private Model fieldModel = null;
	private javax.swing.JLabel ivjMembraneNameJLabel = null;
	private javax.swing.JTextField ivjMembraneNameJTextField = null;
	private javax.swing.JCheckBox ivjOutsideJCheckBox = null;
	
	private DocumentListener documentListener = new DocumentListener(){
		public void changedUpdate(DocumentEvent e) {
			updateInterface();
		}
		public void insertUpdate(DocumentEvent e) {
			updateInterface();
		}
		public void removeUpdate(DocumentEvent e) {
			updateInterface();
		}
	};
/**
 * Constructor
 */
public FeatureDialog(JFrame parent) {
	super(parent);
	setModal(true);
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getCancelJButton()) 
		dispose();
	if (e.getSource() == getOKJButton()) 
		apply();
}
/**
 * Comment
 */
private void apply() {
	try{
		if(getParentFeature() == null){
			if(getChildFeature() != null){
				if (getNameJTextField().getText() != null && getNameJTextField().getText().length() > 0){
					getChildFeature().setName(getNameJTextField().getText());
				}
				if (getFeatureSizeNameJTextField().getText() != null && getFeatureSizeNameJTextField().getText().length() > 0){
					getChildFeature().getStructureSize().setName(getFeatureSizeNameJTextField().getText());
				}
			}
		}else if(getChildFeature() == null && getModel() != null){
			if(getOutsideJCheckBox().isVisible() && getOutsideJCheckBox().isSelected()){
				getModel().addFeature(getNameJTextField().getText(),null,getMembraneNameJTextField().getText());
			}else{
				getModel().addFeature(getNameJTextField().getText(),getParentFeature(),getMembraneNameJTextField().getText());
			}
		}
		dispose();
	}catch(Exception e){
		e.printStackTrace();
		PopupGenerator.showErrorDialog(this, "failed to apply changes: " + e.getMessage(), e);
	}
}
/**
 * Return the Button2 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}
/**
 * Gets the childFeature property (cbit.vcell.model.Feature) value.
 * @return The childFeature property value.
 * @see #setChildFeature
 */
public Feature getChildFeature() {
	return fieldChildFeature;
}
/**
 * Comment
 */
private java.lang.String getChildName() {
	if (getChildFeature()!=null){
		return getChildFeature().getName();
	}else{
		return "";
	}
}

/**
 * Return the MembraneNameJLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getMembraneNameJLabel() {
	if (ivjMembraneNameJLabel == null) {
		try {
			ivjMembraneNameJLabel = new javax.swing.JLabel();
			ivjMembraneNameJLabel.setName("MembraneNameJLabel");
			ivjMembraneNameJLabel.setText("Membrane Name");
			ivjMembraneNameJLabel.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMembraneNameJLabel;
}
/**
 * Return the MembraneNameJTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getMembraneNameJTextField() {
	if (ivjMembraneNameJTextField == null) {
		try {
			ivjMembraneNameJTextField = new javax.swing.JTextField();
			ivjMembraneNameJTextField.setName("MembraneNameJTextField");
			ivjMembraneNameJTextField.setEnabled(true);
			ivjMembraneNameJTextField.setColumns(20);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMembraneNameJTextField;
}
/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public Model getModel() {
	return fieldModel;
}
/**
 * Return the Label1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setText("Feature Name");
			ivjNameJLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getNameJTextField() {
	if (ivjNameJTextField == null) {
		try {
			ivjNameJTextField = new javax.swing.JTextField();
			ivjNameJTextField.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					updateDependentText();
				}
			});
			ivjNameJTextField.setName("NameJTextField");
			ivjNameJTextField.setEnabled(false);
			ivjNameJTextField.setColumns(20);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNameJTextField;
}
/**
 * Return the Button1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getOKJButton() {
	if (ivjOKJButton == null) {
		try {
			ivjOKJButton = new javax.swing.JButton();
			ivjOKJButton.setName("OKJButton");
			ivjOKJButton.setText("OK");
			ivjOKJButton.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOKJButton;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getOutsideJCheckBox() {
	if (ivjOutsideJCheckBox == null) {
		try {
			ivjOutsideJCheckBox = new javax.swing.JCheckBox();
			ivjOutsideJCheckBox.setName("OutsideJCheckBox");
			ivjOutsideJCheckBox.setText("Outermost");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutsideJCheckBox;
}
/**
 * Return the Panel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new javax.swing.JPanel();
			ivjPanel1.setName("Panel1");
			ivjPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
			constraintsNameJLabel.gridx = 0; constraintsNameJLabel.gridy = 0;
			constraintsNameJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsNameJLabel.anchor = GridBagConstraints.EAST;
			ivjPanel1.add(getNameJLabel(), constraintsNameJLabel);

			java.awt.GridBagConstraints constraintsNameJTextField = new java.awt.GridBagConstraints();
			constraintsNameJTextField.gridx = 1; constraintsNameJTextField.gridy = 0;
			constraintsNameJTextField.fill = java.awt.GridBagConstraints.BOTH;
			constraintsNameJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsNameJTextField.gridwidth = 2;
			ivjPanel1.add(getNameJTextField(), constraintsNameJTextField);
			
			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 3; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = GridBagConstraints.BOTH;
			constraintsJPanel1.insets = new Insets(4, 4, 4, 4);
			getPanel1().add(getOutsideJCheckBox(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsMembraneNameJLabel = new java.awt.GridBagConstraints();
			constraintsMembraneNameJLabel.gridx = 0; constraintsMembraneNameJLabel.gridy = 1;
			constraintsMembraneNameJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsMembraneNameJLabel.anchor = GridBagConstraints.EAST;
			ivjPanel1.add(getMembraneNameJLabel(), constraintsMembraneNameJLabel);

			java.awt.GridBagConstraints constraintsMembraneNameJTextField = new java.awt.GridBagConstraints();
			constraintsMembraneNameJTextField.gridx = 1; constraintsMembraneNameJTextField.gridy = 1;
			constraintsMembraneNameJTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsMembraneNameJTextField.gridwidth = 2;
			constraintsMembraneNameJTextField.fill = java.awt.GridBagConstraints.BOTH;
			ivjPanel1.add(getMembraneNameJTextField(), constraintsMembraneNameJTextField);
			
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			ivjPanel1.add(getFeatureSizeNameLabel(), gridBagConstraints);
			
			GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.gridx = 1;
			gridBagConstraints_1.gridy = 2;
			gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints_1.gridwidth = 2;
			gridBagConstraints_1.fill = java.awt.GridBagConstraints.BOTH;
			ivjPanel1.add(getFeatureSizeNameJTextField(), gridBagConstraints_1);
			
			GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.gridy = 2;
			gridBagConstraints_2.gridx = 3;
			gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
			ivjPanel1.add(getOverrideSizeNameCheckBox(), gridBagConstraints_2);			
			
			constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.fill = GridBagConstraints.BOTH;
			constraintsJPanel1.insets = new Insets(4, 4, 4, 4);
			constraintsJPanel1.ipadx = 20;
			getPanel1().add(getOKJButton(), constraintsJPanel1);
		
			constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 2; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.insets = new Insets(4, 4, 4, 4);
			getPanel1().add(getCancelJButton(), constraintsJPanel1);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
/**
 * Gets the parentFeature property (cbit.vcell.model.Feature) value.
 * @return The parentFeature property value.
 * @see #setParentFeature
 */
public Feature getParentFeature() {
	return fieldParentFeature;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION --------- in FeatureDialog ");
	 exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getNameJTextField().addPropertyChangeListener(this);
	this.addPropertyChangeListener(this);
	getCancelJButton().addActionListener(this);
	getOKJButton().addActionListener(this);
	getMembraneNameJTextField().addPropertyChangeListener(this);
	getMembraneNameJTextField().getDocument().addDocumentListener(documentListener);
	getFeatureSizeNameJTextField().addPropertyChangeListener(this);
	getFeatureSizeNameJTextField().getDocument().addDocumentListener(documentListener);
	getNameJTextField().getDocument().addDocumentListener(documentListener);
}
/**
 * Initialize class
 */
private void initialize() {
	try {
		setName("FeatureDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		add(getPanel1());
		initConnections();
		pack();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && (evt.getPropertyName().equals("parentFeature"))) 
		updateInterface();
	if (evt.getSource() == this && (evt.getPropertyName().equals("model"))) 
		updateInterface();
	if (evt.getSource() == this && (evt.getPropertyName().equals("childFeature"))) {
		getNameJTextField().setText(this.getChildName());
		if (getChildFeature()!=null){
			boolean bOverride = !getChildFeature().getStructureSize().getName().equals(Structure.getDefaultStructureSizeName(getChildFeature().getName()));
			getOverrideSizeNameCheckBox().setSelected(bOverride);
			getFeatureSizeNameJTextField().setText(this.getChildFeature().getStructureSize().getName());
			updateDependentText();
		}
		updateInterface();
	}
}
/**
 * Sets the childFeature property (cbit.vcell.model.Feature) value.
 * @param childFeature The new value for the property.
 * @see #getChildFeature
 */
public void setChildFeature(Feature childFeature) {
	//
	// See class documentation for usage:
	//
	Feature oldValue = fieldChildFeature;
	fieldChildFeature = childFeature;
	firePropertyChange("childFeature", oldValue, childFeature);
}
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}
/**
 * Sets the parentFeature property (cbit.vcell.model.Feature) value.
 * @param parentFeature The new value for the property.
 * @see #getParentFeature
 */
public void setParentFeature(Feature parentFeature) {
	//
	// See class documentation for usage:
	//
	Feature oldValue = fieldParentFeature;
	fieldParentFeature = parentFeature;
	firePropertyChange("parentFeature", oldValue, parentFeature);
}
/**
 * Comment
 */
private void updateInterface() {
	//
	boolean bModelNull = (getModel() == null);
	boolean bAddingFeature =
		(getParentFeature() != null) && (getChildFeature() == null);
		
	boolean bEditingFeatureName =
		(getParentFeature() == null) && (getChildFeature() != null);
		
	boolean bChildNameChanged =
		getChildFeature() == null ||
		!Compare.isEqualOrNull(getChildFeature().getName(),getNameJTextField().getText());
	
	boolean bStructureSizeNameChanged =
		getChildFeature() == null ||
		!Compare.isEqualOrNull(getChildFeature().getStructureSize().getName(),getFeatureSizeNameJTextField().getText());
	//
	boolean bOKEnabled =		(	
			(bAddingFeature && !(getMembraneNameJTextField() == null || getMembraneNameJTextField().getText().length() == 0))
			|| 
			bEditingFeatureName
		) && 
		!bModelNull && 
		(bChildNameChanged || bStructureSizeNameChanged) && 
		!(getNameJTextField().getText() == null || getNameJTextField().getText().length() == 0);
	//
	getOKJButton().setEnabled(bOKEnabled);
	getNameJTextField().setEnabled(!bModelNull && (bAddingFeature || bEditingFeatureName));
	getNameJLabel().setEnabled(!bModelNull && (bAddingFeature || bEditingFeatureName));

	getMembraneNameJLabel().setVisible(bAddingFeature);
	getMembraneNameJTextField().setVisible(bAddingFeature);
	
	getFeatureSizeNameLabel().setVisible(!bAddingFeature);
	getFeatureSizeNameJTextField().setVisible(!bAddingFeature);
	getOverrideSizeNameCheckBox().setVisible(!bAddingFeature);
	
	if(bAddingFeature){
		if (!getOKJButton().getText().equals("Add Feature")){
			getOKJButton().setText("Add Feature");
		}
	}else if(bEditingFeatureName){
		if (!getOKJButton().getText().equals("OK")){
			getOKJButton().setText("OK");
		}
	}
	//OutsideCheckbox visible only if we are adding feature to top feature
	boolean bOutsideVisible =
				getParentFeature() != null && 
				getChildFeature() == null &&
				getModel() != null &&
				getModel().getTopFeature() == getParentFeature();
	if(getOutsideJCheckBox().isVisible() != bOutsideVisible){
		if(bOutsideVisible){
			getOutsideJCheckBox().setSelected(false);
		}
		getOutsideJCheckBox().setVisible(bOutsideVisible);
	}
}

	/**
	 * @return
	 */
	protected JLabel getFeatureSizeNameLabel() {
		if (featureSizeNameLabel == null) {
			featureSizeNameLabel = new JLabel();
			featureSizeNameLabel.setText("Feature Size Name [um^3]");
		}
		return featureSizeNameLabel;
	}
	/**
	 * @return
	 */
	protected JTextField getFeatureSizeNameJTextField() {
		if (featureSizeNameJTextField == null) {
			featureSizeNameJTextField = new JTextField();
			featureSizeNameJTextField.setColumns(20);
		}
		return featureSizeNameJTextField;
	}
	/**
	 * @return
	 */
	protected JCheckBox getOverrideSizeNameCheckBox() {
		if (overrideSizeNameCheckBox == null) {
			overrideSizeNameCheckBox = new JCheckBox();
			overrideSizeNameCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					updateDependentText();
				}
			});
			overrideSizeNameCheckBox.setText("override");
		}
		return overrideSizeNameCheckBox;
	}
	
	private void updateDependentText(){
		if (getOverrideSizeNameCheckBox().isVisible()) {
			if (!getOverrideSizeNameCheckBox().isSelected()){
				getFeatureSizeNameJTextField().setEditable(false);
				if (getNameJTextField().getText().length()>0 && !getFeatureSizeNameJTextField().getText().equals(Structure.getDefaultStructureSizeName(getNameJTextField().getText()))){
					getFeatureSizeNameJTextField().setText(Structure.getDefaultStructureSizeName(getNameJTextField().getText()));
				}
			}else{
				getFeatureSizeNameJTextField().setEditable(true);
			}
		}
	}
}
