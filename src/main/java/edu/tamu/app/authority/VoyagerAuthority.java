/* 
 * VoyagerService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.authority;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.model.response.marc.VoyagerServiceData;
import edu.tamu.framework.util.HttpUtility;

/**
 * Voyager service. Performs requests with ExLibris Voyager API. All Voyager API
 * responses are xml. The xml is mapped to objects using JAXB.
 * 
 * @author
 *
 */
@Service("Voyager")
@ConfigurationProperties(prefix = "app.service.voyager")
public class VoyagerAuthority implements Authority {

	private static final Logger logger = Logger.getLogger(VoyagerAuthority.class);

	@Value("${app.service.voyager.host}")
	private String host;

	@Value("${app.service.voyager.port}")
	private String port;

	@Value("${app.service.voyager.app}")
	private String app;

	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;

	@Autowired
	private HttpUtility httpUtility;

	@Override
	public Document populate(Document document) {
		try {
			Map<String, List<String>> metadataMap = getMARCRecordMetadata(document.getName());

			document.getFields().parallelStream().forEach(field -> {
				List<String> values = metadataMap.get(field.getLabel().getName());
				if (values != null) {
					values.forEach(value -> {
						field.addValue(metadataFieldValueRepo.create(value, field));
					});
				}
			});

		} catch (Exception e) {
			logger.debug("MARC record does not exist: " + document.getName());
		}
		return document;
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getMARCRecordMetadata(String documentName) throws Exception {
		FlatMARC flatMarc = new FlatMARC(fetchMARC(documentName));

		Field[] marcFields = FlatMARC.class.getDeclaredFields();

		Map<String, List<String>> metadataMap = new HashMap<String, List<String>>();

		for (Field field : marcFields) {
			field.setAccessible(true);
			List<String> marcList = new ArrayList<String>();
			if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
				try {
					for (String string : (List<String>) field.get(flatMarc)) {
						marcList.add(string);
					}
				} catch (IllegalArgumentException e) {
					logger.error("Illegal Argument", e);
				} catch (IllegalAccessException e) {
					logger.error("Illegal Access", e);
				}
			} else {
				try {
					marcList.add(field.get(flatMarc).toString());
				} catch (IllegalArgumentException e) {
					logger.error("Illegal Argument", e);
				} catch (IllegalAccessException e) {
					logger.error("Illegal Access", e);
				}
			}

			metadataMap.put(field.getName().replace('_', '.'), marcList);
		}

		return metadataMap;
	}

	public VoyagerServiceData fetchMARC(String bibId) throws Exception {
		String urlString = "http://" + host + ":" + port + "/" + app + "/GetHoldingsService?bibId=" + bibId;
		String xmlResponse = httpUtility.makeHttpRequest(urlString, "GET");

		xmlResponse = xmlResponse.replace("ser:", "");
		xmlResponse = xmlResponse.replace("hol:", "");
		xmlResponse = xmlResponse.replace("slim:", "");
		xmlResponse = xmlResponse.replace("xmlns:", "");
		xmlResponse = xmlResponse.replace("item:", "");
		xmlResponse = xmlResponse.replace("mfhd:", "");
		xmlResponse = xmlResponse.replace("xsi:", "");

		InputStream xmlInputStream = new ByteArrayInputStream(xmlResponse.getBytes());

		JAXBContext jaxbContext = JAXBContext.newInstance(VoyagerServiceData.class);

		return (VoyagerServiceData) jaxbContext.createUnmarshaller().unmarshal(xmlInputStream);
	}

}
