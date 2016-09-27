/**
 * @author D063124
 * @version 1.0 9/24/2014
 */

package com.sap.requestTypes;

import java.io.IOException;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.BasicAuthenticationBehavior;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.core.OLink;

import com.sap.model.PropertiesData;
import com.sap.view.ODataRequestUserInterface;

public class OData4jRequest extends Thread {

	/**
	 * this variable represents an uiReference so the fetched data can be parsed
	 * to the output
	 */
	private ODataRequestUserInterface uiReference;
	/**
	 * this variable is a PropertiesData object which storages the result of the
	 * call
	 */
	private PropertiesData attributeData;

	private String personIdExternal;

	/**
	 * constructor the parameters serviceUrl, user, password could be used to
	 * enter these via the ui
	 * 
	 * @param id
	 * 
	 * @param ui
	 * @param serviceUrl
	 * @param user
	 * @param password
	 */
	public OData4jRequest(String personId, ODataRequestUserInterface ui) {
		personIdExternal = personId;
		uiReference = ui;
		attributeData = new PropertiesData(personId, "PerPerson");
	}

	/**
	 * this method represents the overwritten run method of the class Thread. It
	 * is launched by the start() method. This method fetches the Data, the
	 * terminates the progress bar and finally writes the result on the ui
	 */
	@Override
	public void run() {
		fetchData();
		uiReference.endBar();
		uiReference.setUiData(attributeData);
	}

	/**
	 * this method starts a query and filters the information by the
	 * personIdExternal which is tipped in by the user
	 */
	private void fetchData() {
		try {
			ODataConsumer consumer = buildConsumer();
			readEntities(consumer);
		} catch (WrongPersonIdException e) {
			uiReference.unvalidUserIdError(personIdExternal, attributeData);
		} catch (IOException ie) {
			ie.printStackTrace();
			uiReference.serverOverstrainedError();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * this method reads the entities by using the odata operations expand,
	 * filter, select
	 * 
	 * @param consumer
	 * @throws Exception
	 */
	private void readEntities(ODataConsumer consumer) throws Exception {
		try {
			Enumerable<OEntity> entity = consumer
					.getEntities(QueryParams.PERPERSONAL)
					.expand(QueryParams.PERSONNAV)
					.filter(attributeData.getExtId())
					.select(QueryParams.FIRSTNAME + "," + QueryParams.LASTNAME + "," + QueryParams.PERSONNAV + "/"
							+ QueryParams.DATEOFBIRTH + "," + QueryParams.PERSONNAV + "/" + QueryParams.COUNTRYOFBIRTH)
					.top(1).execute(); // .first();
			if (entity == null) {
				System.out.println("null-<<<<<");
			}
			OEntity entity1 = entity.first();
			List<OLink> entityRel = entity1.getLinks();
			parseData(entity1);

			OEntity entity2;
			for (OLink j : entityRel) {
				String navigation = j.getTitle();
				if (QueryParams.PERSONNAV.equals(navigation)) {
					entity2 = j.getRelatedEntity();
					parseData(entity2);
				}
			}
		} catch (RuntimeException runE) {
			if ("No elements".equals(runE.getMessage()))
				throw new WrongPersonIdException();
		}
	}

	/**
	 * this method builds a connection and sets the client behaviour by using
	 * the basic authentication encoding base64
	 * 
	 * @return
	 */

	public ODataConsumer buildConsumer() {
		ODataConsumer.Builder builder = ODataConsumers.newBuilder(QueryParams.SERVICEURL);
		builder.setClientBehaviors(new BasicAuthenticationBehavior(QueryParams.USER, QueryParams.PW));
		return builder.build();
	}

	/**
	 * this method parses the result data to the model class
	 * 
	 * @param caption
	 * @param entity
	 */

	private void parseData(OEntity entity) {
		for (OProperty<?> p : entity.getProperties()) {
			analyzeProperty(p);
		}
	}

	/**
	 * this method is called in the parseData class and analyzes the given
	 * property and depending to result parses it to the right variable in the
	 * PropertiesData object
	 * 
	 * @param p
	 */

	private void analyzeProperty(OProperty<?> p) {
		attributeData.setNodeValue(p.getName(), p.getValue().toString());
		if (QueryParams.DATEOFBIRTH.equals(p.getName())) {
			attributeData.setNodeValue(p.getName(), p.getValue().toString().replaceAll("T00:00:00.000", ""));
		}
	}
}
