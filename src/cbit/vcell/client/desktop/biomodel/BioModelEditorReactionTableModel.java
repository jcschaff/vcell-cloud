package cbit.vcell.client.desktop.biomodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultCellEditor;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.gui.ReactionEquation;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorReactionTableModel extends BioModelEditorRightSideTableModel<ReactionStep> {	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_EQUATION = 1;
	public final static int COLUMN_STRUCTURE = 2;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private static String[] columnNames = new String[] {"Name", "Equation", "Structure"};

	public BioModelEditorReactionTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
		addPropertyChangeListener(this);
	}

	public Class<?> getColumnClass(int column) {
		switch (column){		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_EQUATION:{
				return ReactionEquation.class;
			}
			case COLUMN_STRUCTURE:{
				return Structure.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<ReactionStep> computeData() {
		ArrayList<ReactionStep> reactionStepList = new ArrayList<ReactionStep>();
		if (getModel() != null){
			for (ReactionStep rs : getModel().getReactionSteps()){
				if (searchText == null || searchText.length() == 0 || rs.getName().startsWith(searchText)) {
					reactionStepList.add(rs);
				}
			}
		}
		return reactionStepList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {
			return null;
		}
		try{
			if (row >= 0 && row < getDataSize()) {
				ReactionStep reactionStep = getValueAt(row);
				switch (column) {
					case COLUMN_NAME: {
						return reactionStep.getName();
					} 
					case COLUMN_EQUATION: {
						return new ReactionEquation(reactionStep);
					} 
					case COLUMN_STRUCTURE: {
						return reactionStep.getStructure();
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return ADD_NEW_HERE_TEXT;
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return row < getDataSize() || column == COLUMN_NAME;
	}
	
	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			updateStructureComboBox();
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null) {
			return;
		}
		try{
			ReactionStep reactionStep = null;
			if (row < getDataSize()) {
				reactionStep = getValueAt(row);
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = (String)value;
					reactionStep.setName(inputValue);
					break;
				} 
				case COLUMN_EQUATION: {
					String inputValue = (String)value;
					reactionStep.setReactionParticipants(ReactionEquation.parseReaction(reactionStep, inputValue));
					break;
				}
				case COLUMN_STRUCTURE: {
					Structure s = (Structure)value;
					reactionStep.setStructure(s);
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_NAME: {
					if (value.equals(ADD_NEW_HERE_TEXT)) {
						return;
					}
					reactionStep = new SimpleReaction(getModel().getStructures()[0], (String)value);
					getModel().addReactionStep(reactionStep);
					break;
				}
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public boolean isSortable(int col) {
		return false;
	}
	
	@Override
	public Comparator<ReactionStep> getComparator(int col, boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String checkInputValue(String inputValue, int row, int column) {
		ReactionStep reactionStep = null;
		if (row >= 0 && row < getDataSize()) {
			reactionStep = getValueAt(row);
		}
		switch (column) {
		case COLUMN_NAME:
			if (reactionStep == null || !reactionStep.getName().equals(inputValue)) {
				if (getModel().getReactionStep(inputValue) != null) {
					return "Reaction '" + inputValue + "' already exist!";
				}
			}
			break;
		case COLUMN_EQUATION:
			try {
				ReactionEquation.parseReaction(reactionStep, inputValue);
			} catch (Exception ex) {
				return ex.getMessage();
			}
			break;
		case COLUMN_STRUCTURE:
			if (getModel().getStructure(inputValue) == null) {
				return "Structure '" + inputValue + "' does not exist!";
			}
			break;
		}
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		if (column == COLUMN_STRUCTURE) {
			Set<String> words = new HashSet<String>();
			for (Structure s : getModel().getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
	
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {		
		super.bioModelChange(evt);
		ownerTable.getColumnModel().getColumn(COLUMN_STRUCTURE).setCellEditor(getStructureComboBoxEditor());
		updateStructureComboBox();
	}
}
