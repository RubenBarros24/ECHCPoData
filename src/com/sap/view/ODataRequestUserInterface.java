
package com.sap.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.sap.requestTypes.HttpRequest;
import com.sap.requestTypes.OData4jRequest;
import com.sap.requestTypes.OlingoRequest;
import com.sap.requestTypes.QueryParams;
import com.sap.model.PropertiesData;

@SuppressWarnings("serial")
public class ODataRequestUserInterface extends JFrame implements ActionListener, FocusListener {

	/**
	 * this variable represents an empty border
	 */
	public final static Border EMPTY = BorderFactory.createEmptyBorder();
	/**
	 * this variable represents a reference to the class
	 * PersonIdFieldAndGOButtonPanel which extends JPanel. This Panel describes
	 * the northern part of the container
	 */
	private PersonIdFieldAndGOButtonPanel personIdAndGO;
	/**
	 * this variable represents a reference to the class QueryButtonPanel which
	 * extends JPanel. This Panel describes the part in the center of the
	 * container
	 */
	private QueryButtonPanel buttonPanel;
	/**
	 * this variable represents a reference to the class ResultAreaPanel which
	 * extends JPanel. This Panel describes the southern part of the container
	 */
	private ResultAreaPanel resultArea;
	/**
	 * this variable represents a progress bar which is running while the call
	 * is launched
	 */
	private JProgressBar bar;

	/**
	 * constructor
	 */
	public ODataRequestUserInterface() {
		getContentPane();
		bar = new JProgressBar();
		this.add(bar, BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("OData Request");
		createUI();
		this.pack();
		this.setVisible(true);
	}

	/**
	 * this class creates all the necessary objects to build the container
	 */
	private void createUI() {
		JPanel north = new JPanel(new GridLayout(2, 1, 10, 10));
		personIdAndGO = new PersonIdFieldAndGOButtonPanel(this);
		buttonPanel = new QueryButtonPanel(this);
		north.add(personIdAndGO);
		north.add(buttonPanel);

		resultArea = new ResultAreaPanel(this);

		this.add(north, BorderLayout.NORTH);
		this.add(resultArea, BorderLayout.SOUTH);

	}

	/**
	 * this method creates a pop-up-window if the user enters an invalid
	 * userIdExternal
	 */
	public void unvalidUserIdError(String personIdExternal, PropertiesData attributeData) {
		attributeData.setNodeValue(QueryParams.FIRSTNAME, "");
		attributeData.setNodeValue(QueryParams.LASTNAME, "");
		attributeData.setNodeValue(QueryParams.COUNTRYOFBIRTH, "");
		attributeData.setNodeValue(QueryParams.DATEOFBIRTH, "");

		JOptionPane.showMessageDialog(this, "Please enter a valid userIdExternal\nWrong UserIdExternal:\n"
				+ personIdExternal, "Wrong UserIdExternal", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * this method creates a pop-up-window if the server is overstrained
	 */
	public void serverOverstrainedError() {
		JOptionPane.showMessageDialog(this, "The server might be overstrained\nplease try again!",
				"Server overstrained", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * this method sets result in the textarea
	 * 
	 * @param attributeData
	 */
	public void setUiData(PropertiesData attributeData) {
		resultArea.getPerPersonalName().setText(QueryParams.LASTNAME + ":\n\n\n\n" + QueryParams.FIRSTNAME + ":");
		resultArea.getPerPersonalValue().setText(
				attributeData.getNodeValue(QueryParams.LASTNAME) + "\n\n\n\n"
						+ attributeData.getNodeValue(QueryParams.FIRSTNAME));
		resultArea.getPerPersonName().setText(QueryParams.COUNTRYOFBIRTH + ":\n\n\n\n" + QueryParams.DATEOFBIRTH + ":");
		resultArea.getPerPersonValue().setText(
				attributeData.getNodeValue(QueryParams.COUNTRYOFBIRTH) + "\n\n\n\n"
						+ attributeData.getNodeValue(QueryParams.DATEOFBIRTH));
	}

	/**
	 * actionPerformed method: This method reads out the personId and depending
	 * on which querytype is selected it launchs the call
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String id = personIdAndGO.getInputField().getText();

		startBar();
		switch (buttonPanel.getSelectedButton()) {
		case HTTP:
			HttpRequest httpRequest = new HttpRequest(id);
			httpRequest.start();
			break;
		case ODATA4J:
			OData4jRequest odata4jRequest = new OData4jRequest(id, this);
			odata4jRequest.start();
			break;
		case OLINGO:
			OlingoRequest olingoRequest = new OlingoRequest(id, this);
			olingoRequest.start();
			break;
		default:
		}
		personIdAndGO.getGoButton().setSelected(false);
	}

	/**
	 * focusLost method
	 */
	@Override
	public void focusLost(FocusEvent e) {

	}

	/**
	 * focusGained method
	 */
	@Override
	public void focusGained(FocusEvent e) {
		personIdAndGO.getInputField().setText("");
	}

	/**
	 * starts the Bar
	 */
	public void startBar() {
		bar.setIndeterminate(true);
	}

	/**
	 * ends the Bar
	 */
	public void endBar() {
		bar.setIndeterminate(false);
	}

	/**
	 * main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ODataRequestUserInterface test = new ODataRequestUserInterface();
	}
}
