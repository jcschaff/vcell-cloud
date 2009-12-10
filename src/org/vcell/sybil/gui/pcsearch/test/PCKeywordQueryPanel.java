package org.vcell.sybil.gui.pcsearch.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.vcell.sybil.actions.web.PathwayCommonsKeywordAction;
import org.vcell.sybil.actions.web.PathwayCommonsKeywordAction.KeywordProvider;
import org.vcell.sybil.models.tree.pckeyword.ResponseTreeManager;
import org.vcell.sybil.models.tree.pckeyword.XRefTreeSelectionListener;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.miriam.XRefToURN;

public class PCKeywordQueryPanel extends JPanel {
	@SuppressWarnings("serial")
	
	private XRef xref = null;
	
	public XRef getSelectedXRef() {
		return xref;
	}

	public static class KeywordTextField extends JTextField implements KeywordProvider {
		public String keyword() { return getText(); }
	}
	
	@SuppressWarnings("serial")
	public static class XRefLabel extends JLabel implements Accepter<XRef> {
		public void accept(XRef xRef) { setText(XRefToURN.createURN(xRef.db(), xRef.id())); }
	}
	
	protected KeywordTextField keywordTextField = new KeywordTextField();
	protected XRefLabel xRefLabel = new XRefLabel();
	protected ResponseTreeManager treeMgr = new ResponseTreeManager();
	protected PathwayCommonsKeywordAction queryAction = 
		new PathwayCommonsKeywordAction(keywordTextField, treeMgr);
	
	protected JButton queryButton = new JButton(queryAction);

	
	public PCKeywordQueryPanel(){
		initialize();
	}

	private void initialize(){
		JPanel mainPanel = this;
		JPanel topPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(xRefLabel, BorderLayout.SOUTH);
		JTree responseTree = new JTree(treeMgr.tree());
		XRefTreeSelectionListener xRefTreeSelectionListener = new XRefTreeSelectionListener(xRefLabel);
		responseTree.getSelectionModel().addTreeSelectionListener(xRefTreeSelectionListener);
		Accepter<XRef> thisXrefAccepter = new Accepter<XRef>(){
			public void accept(XRef t) {
				PCKeywordQueryPanel.this.xref = t;
			}
		};
		responseTree.getSelectionModel().addTreeSelectionListener(new XRefTreeSelectionListener(thisXrefAccepter));
		responseTree.expandRow(0);
		JScrollPane scrollPane = new JScrollPane(responseTree);
		mainPanel.add(scrollPane);
		keywordTextField.addActionListener(queryAction);
		topPanel.setLayout(new BorderLayout());
		topPanel.add(keywordTextField);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(queryButton);
		topPanel.add(buttonPanel, BorderLayout.EAST);
		setPreferredSize(new Dimension(700, 500));
	}
	
	public static void main(String[] args){
		try {
			JFrame frame = new JFrame();
			PCKeywordQueryPanel mainPanel = new PCKeywordQueryPanel();
			frame.add(mainPanel);
			frame.setPreferredSize(new Dimension(700, 500));
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
