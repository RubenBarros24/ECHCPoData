/**
 * @author D063124
 * @version 1.0 9/24/2014
 */

package com.sap.requestTypes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.model.PropertiesData;
import com.sap.view.ODataRequestUserInterface;

public class OlingoRequest extends Thread {

	/**
	 * this variable represents a reference to the user interface
	 */
	private ODataRequestUserInterface uiReference;
	/**
	 * this variable stores the personIdExternal
	 */
	private String personIdExt;
	/**
	 * this variable stores an object of the class PropertiesData which
	 * represents the model class
	 */
	private PropertiesData attributeData;

	/**
	 * constructor the parameters serviceUrl, user, password could be used to
	 * enter these via the ui
	 * 
	 * @param id
	 * @param ui
	 * @param serviceUrl
	 * @param user
	 * @param password
	 */
	public OlingoRequest(String personId, ODataRequestUserInterface ui) {
		uiReference = ui;
		attributeData = new PropertiesData(personId, "PerPerson");
		personIdExt = personId;

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
	 * personIdExternal which is tipped in through the UI
	 */
	private void fetchData() {
		try {
			Edm edm = readEdm(QueryParams.SERVICEURL);
			ODataFeed feed = readFeed(edm, QueryParams.SERVICEURL, "application/atom+xml", QueryParams.PERPERSONAL,
					QueryParams.PERSONNAV, QueryParams.EXTERNALIDCODE + "'" + personIdExt + "'");
			if (feed.getEntries().isEmpty()) {
				uiReference.unvalidUserIdError(personIdExt, attributeData);
				throw new WrongPersonIdException();
			}
			for (ODataEntry entry : feed.getEntries()) {
				parseData(entry);
			}
		} catch (IOException ie) {
			ie.printStackTrace();
			uiReference.serverOverstrainedError();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this method initializes the parse process
	 * 
	 * @param entry
	 */
	private void parseData(ODataEntry entry) {
		Set<Entry<String, Object>> entries = entry.getProperties().entrySet();
		for (Entry<String, Object> entry1 : entries) {
			handleEntry(entry1);
		}
	}

	/**
	 * this method handles and odata entry which is part of the odata entity
	 * PerPersonal
	 * 
	 * @param entry1
	 * @param key
	 * @throws NullPointerException
	 */
	private void handleEntry(Entry<String, Object> entry1) {
		String key = entry1.getKey();
		String value = entry1.getValue() == null ? "null" : entry1.getValue().toString();
		attributeData.setNodeValue(key, value);
		if (QueryParams.PERSONNAV.equals(entry1.getKey())) {
			handlePersonNav(entry1);
		}
	}

	/**
	 * this method handles an odata entry if the query contains the path
	 * "personNav"
	 * 
	 * @param entry1
	 */
	private void handlePersonNav(Entry<String, Object> entry1) {
		Set<Entry<String, Object>> entries1 = ((ODataEntry) entry1.getValue()).getProperties().entrySet();
		for (Entry<String, Object> entry2 : entries1) {
			handleEntry(entry2);
			if (QueryParams.DATEOFBIRTH.equals(entry2.getKey())) {
				setDateFormat(entry2);
			}
		}
	}

	private void setDateFormat(Entry<String, Object> entry2) {
		Calendar cal = (Calendar) entry2.getValue();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		attributeData.setNodeValue(QueryParams.DATEOFBIRTH, dateFormatter.format(cal.getTime()));
	}

	/**
	 * this method reads the metadata
	 * 
	 * @param serviceUrl
	 * @return
	 * @throws IOException
	 * @throws ODataException
	 */
	private Edm readEdm(String serviceUrl) throws IOException, ODataException {
		InputStream content = execute(serviceUrl + "$metadata", "application/xml", "GET");
		return EntityProvider.readMetadata(content, false);
	}

	/**
	 * this method reads a complete odata feed by using the odata operations
	 * expand and filter
	 * 
	 * @param edm
	 * @param serviceUri
	 * @param contentType
	 * @param entitySetName
	 * @param expand
	 * @param filter
	 * @return
	 * @throws IOException
	 * @throws ODataException
	 */
	public ODataFeed readFeed(Edm edm, String serviceUri, String contentType, String entitySetName, String expand,
			String filter) throws IOException, ODataException {
		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		String absolutUri = createUri(serviceUri, entitySetName, null, expand, filter);

		InputStream content = (InputStream) connect(absolutUri, contentType, "GET").getContent();
		return EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entitySetName), content,
				EntityProviderReadProperties.init().build());
	}

	/**
	 * this method creates an absolute uri by using the odata operations expand
	 * and filter
	 * 
	 * @param serviceUri
	 * @param entitySetName
	 * @param id
	 * @param expand
	 * @param filter
	 * @return
	 */
	private String createUri(String serviceUri, String entitySetName, String id, String expand, String filter) {
		final StringBuilder absolutUri = new StringBuilder(serviceUri).append(entitySetName);
		if (id != null) {
			absolutUri.append("(").append(id).append(")");
		}
		if (expand != null && filter != null) {
			absolutUri.append("/?$expand=").append(expand).append("&$filter=").append(filter);
		}

		return absolutUri.toString();
	}

	/**
	 * this method initializes the connection process
	 * 
	 * @param relativeUri
	 * @param contentType
	 * @param httpMethod
	 * @return
	 * @throws IOException
	 */
	private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();
		checkStatus(connection);

		InputStream content = connection.getInputStream();
		content = logRawContent(httpMethod + " request:\n  ", content, "\n");
		connection.disconnect();
		return content;
	}

	/**
	 * this method initializes the connection process
	 * 
	 * @param relativeUri
	 * @param contentType
	 * @param httpMethod
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();
		checkStatus(connection);

		return connection;
	}

	/**
	 * this method opens the connection and sets the request headers
	 * 
	 * @param absolutUri
	 * @param contentType
	 * @param httpMethod
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
			throws MalformedURLException, IOException {
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty("Accept", contentType);
		connection.setRequestProperty("Authorization", encodeBase64());

		return connection;
	}

	/**
	 * this method checks if the connection to the server is alright
	 * 
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString());
		}
		return httpStatusCode;
	}

	/**
	 * Logging the content of the given InputStream and recreation of and
	 * readable Inputstream which is returned
	 * 
	 * @param prefix
	 * @param content
	 * @param postfix
	 * @return
	 * @throws IOException
	 */
	private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		byte[] buffer = streamToArray(content);
		content.close();

		return new ByteArrayInputStream(buffer);

	}

	/**
	 * this method transforms a stream into an array
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while (readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		return result;
	}

	/**
	 * this methods encodes the username, companyname and password with the
	 * basic authentication Base64
	 * 
	 * @return String
	 */
	private String encodeBase64() {
		String userId = QueryParams.USER + ":" + QueryParams.PW;
		String encoding = "Basic ";
		encoding += new String(Base64.encodeBase64(userId.getBytes()));
		return encoding;
	}
}
