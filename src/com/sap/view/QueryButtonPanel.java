
package com.sap.view;

import java.awt.*;

import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.sap.requestTypes.RequestTypeEnum;

@SuppressWarnings("serial")
public class QueryButtonPanel extends JPanel {

	/**
	 * this variable represents the different request types the user can decide
	 * between
	 */
	private ButtonGroup requestTypes;
	/**
	 * this variable represents the query type httpButton Client
	 */
	private JRadioButton httpButton;
	/**
	 * this variable represents the query type odataj4jButton
	 */
	private JRadioButton odataj4jButton;
	/**
	 * this variable represents the query type olingoButton
	 */
	private JRadioButton olingoButton;

	/**
	 * constructor
	 * 
	 * @param uiRef
	 */
	public QueryButtonPanel(ODataRequestUserInterface uiRef) {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBorder(new EmptyBorder(0, 5, 0, 5));
		createButtonGroup("http Client", "OData4j", "Olingo", this);
	}

	/**
	 * this method creates a buttongroup containing the three different request
	 * types: httpButtonClient, OData4j and Olingo. Each of them is represented by one
	 * radiobutton
	 * 
	 * @param httpButtonName
	 * @param odataj4jButtonName
	 * @param olingoButtonName
	 * @param p
	 */
	private void createButtonGroup(String httpButtonName, String odataj4jButtonName, String olingoButtonName, JPanel p) {
		JPanel panel = new JPanel(new FlowLayout());
		requestTypes = new ButtonGroup();

		httpButton = new JRadioButton();
		httpButton.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		httpButton.setText(httpButtonName);
		httpButton.setPreferredSize(new Dimension(200, 40));
		requestTypes.add(httpButton);
		panel.add(httpButton);

		odataj4jButton = new JRadioButton();
		odataj4jButton.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		odataj4jButton.setText(odataj4jButtonName);
		odataj4jButton.setPreferredSize(new Dimension(200, 40));
		requestTypes.add(odataj4jButton);
		panel.add(odataj4jButton);

		olingoButton = new JRadioButton();
		olingoButton.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 24));
		olingoButton.setText(olingoButtonName);
		olingoButton.setPreferredSize(new Dimension(200, 40));
		requestTypes.add(olingoButton);
		panel.add(olingoButton);

		httpButton.setSelected(true);
		panel.setBorder(ODataRequestUserInterface.EMPTY);
		p.add(panel);
	}

	/**
	 * this method returns the selected butto which decides what query to start
	 * 
	 * @return
	 */
	public RequestTypeEnum getSelectedButton() {
		if (httpButton.isSelected() == true) {
			return RequestTypeEnum.HTTP;
		} else if (odataj4jButton.isSelected() == true) {
			return RequestTypeEnum.ODATA4J;
		} else {
			return RequestTypeEnum.OLINGO;
		}
	}

}
