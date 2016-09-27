
package com.sap.view;

import java.awt.*;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ResultAreaPanel extends JPanel {

	/**
	 * this variable represents the 2 value and 2 name ares, which show the
	 * results on the ui
	 */
	private JTextArea[] resultArea = new JTextArea[4];

	/**
	 * constructor
	 * 
	 * @param uiRef
	 */
	public ResultAreaPanel(ODataRequestUserInterface uiRef) {
		this.setLayout(new GridLayout(1, 2, 10, 10));
		this.add(createResultArea(0, 1, "Personal Information"));
		this.add(createResultArea(2, 3, "Biographical Information"));
	}

	/**
	 * this method creates a resultArea containing name and value of the
	 * searched key
	 * 
	 * @param i
	 * @param j
	 * @param title
	 * @return
	 */
	private JPanel createResultArea(int i, int j, String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2, 5, 5));
		panel.setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		createTextArea(i, "", panel);
		createTextArea(j, "", panel);
		return panel;
	}

	/**
	 * this method creates a textarea which can either be an are representing
	 * the value or the name of the searched key
	 * 
	 * @param i
	 * @param message
	 * @param p
	 */
	private void createTextArea(int i, String message, JPanel p) {
		JPanel panel = new JPanel(new GridLayout(1, 1), false);
		JTextArea area = new JTextArea(message);
		area.setEditable(false);
		area.setPreferredSize(new Dimension(160, 140));
		setResultAreaField(area, i);
		panel.add(area);
		panel.setBorder(ODataRequestUserInterface.EMPTY);
		setResultAreaField(area, i);
		p.add(panel);
	}

	/**
	 * GETTER
	 */
	public JTextArea getPerPersonalName() {
		return this.resultArea[0];
	}

	public JTextArea getPerPersonalValue() {
		return this.resultArea[1];
	}

	public JTextArea getPerPersonName() {
		return this.resultArea[2];
	}

	public JTextArea getPerPersonValue() {
		return this.resultArea[3];
	}

	/**
	 * SETTER
	 */
	public void setResultAreaField(JTextArea tA, int i) {
		this.resultArea[i] = tA;
	}
}