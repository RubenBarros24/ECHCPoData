
package com.sap.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PersonIdFieldAndGOButtonPanel extends JPanel {
	/**
	 * this variable represents the descriptionlabel of the inputfield
	 */
	private JLabel description;
	/**
	 * this variable represents the inputfield. Here the user can enter the
	 * personIdExternal
	 */
	private JTextField inputField;
	/**
	 * this variable represents the GO button which the user has to press
	 * starting the query
	 */
	private JButton goButton;

	/**
	 * constructor
	 * 
	 * @param uiRef
	 */
	public PersonIdFieldAndGOButtonPanel(ODataRequestUserInterface uiRef) {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBorder(new EmptyBorder(0, 5, 0, 5));
		createDescriptionLabel("PersonIdExternal:", this);
		createInputField("enter the personalId here", this);
		inputField.addFocusListener(uiRef);
		createGOButton("GO!", this);
		goButton.addActionListener(uiRef);
	}

	/**
	 * this method creates the label which describes what to enter in the input
	 * field
	 * 
	 * @param message
	 * @param p
	 */
	private void createDescriptionLabel(String message, JPanel p) {
		JPanel panel = new JPanel(new GridLayout(1, 1), false);
		description = new JLabel(message, JLabel.CENTER);
		description.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		equalSize(description);
		panel.add(description);
		panel.setBorder(ODataRequestUserInterface.EMPTY);
		p.add(panel);
	}

	/**
	 * this method creates the input field in which the user enters the
	 * personIdExternal for which he wants to sent a call
	 * 
	 * @param message
	 * @param p
	 */
	private void createInputField(String message, JPanel p) {
		JPanel panel = new JPanel(new FlowLayout());
		inputField = new JTextField(message);
		inputField.setEditable(true);
		inputField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		equalSize(inputField);
		panel.add(inputField);
		panel.setBorder(ODataRequestUserInterface.EMPTY);
		p.add(panel);
	}

	/**
	 * this method creates the Go button which starts the call
	 * 
	 * @param message
	 * @param p
	 */
	private void createGOButton(String message, JPanel p) {
		JPanel panel = new JPanel(new FlowLayout());
		goButton = new JButton(message);
		goButton.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		goButton.setPreferredSize(new Dimension(100, 40));
		goButton.setMnemonic(KeyEvent.VK_ENTER);
		panel.add(goButton);
		panel.setBorder(ODataRequestUserInterface.EMPTY);
		p.add(panel);
	}

	/**
	 * this method sets the same size for all components which are committed
	 * 
	 * @param comp
	 */
	private void equalSize(JComponent comp) {
		Dimension dim = new Dimension(220, 40);
		comp.setPreferredSize(dim);
	}

	/**
	 * GETTER
	 */
	public JLabel getDescription() {
		return description;
	}

	public JTextField getInputField() {
		return inputField;
	}

	public JButton getGoButton() {
		return goButton;
	}

	/**
	 * SETTER
	 */

	public void setDescription(JLabel description) {
		this.description = description;
	}

	public void setInputField(JTextField inputField) {
		this.inputField = inputField;
	}

	public void setGoButton(JButton goButton) {
		this.goButton = goButton;
	}
}
