/* 
 * VoyagerService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Voyager service. Performs requests with ExLibris Voyager API. 
 * All Voyager API responses are xml. The xml is mapped to objects using JAXB.
 * 
 * @author 
 *
 */
@Component
@Service
@ConfigurationProperties(prefix="app.service.voyager")
public class VoyagerService {
	
	@Value("${app.service.voyager.host}")
	private String host;
	
	@Value("${app.service.voyager.ports}")
	private String[] ports;
	
	@Value("${app.service.voyager.app}")
	private String app;
	
	@Autowired
	private HttpService httpService;
	
	
	public List<edu.tamu.app.model.response.marc.VoyagerServiceData> getMARC(String bibId) throws Exception {
		List<edu.tamu.app.model.response.marc.VoyagerServiceData> list = new ArrayList<edu.tamu.app.model.response.marc.VoyagerServiceData>();
		for(int i = 0; i < ports.length; i++) {			
			list.add(queryBinaryMARCRecordService(bibId, ports[i]));		        
		}		        
		return list;		
	}

	private edu.tamu.app.model.response.marc.VoyagerServiceData queryBinaryMARCRecordService(String bibId, String port) throws Exception {				
		String urlString = "http://"+host+":"+port+"/"+app+"/GetHoldingsService?bibId=" + bibId;		
		String xmlResponse = httpService.makeHttpRequest(urlString, "GET");
       
		xmlResponse = xmlResponse.replace("ser:", "");
		xmlResponse = xmlResponse.replace("hol:", "");
		xmlResponse = xmlResponse.replace("slim:", "");
		xmlResponse = xmlResponse.replace("xmlns:", "");
		xmlResponse = xmlResponse.replace("item:", "");
		xmlResponse = xmlResponse.replace("mfhd:", "");
		xmlResponse = xmlResponse.replace("xsi:", "");		
		
		System.out.println("\n" + xmlResponse + "\n");		
		
		InputStream xmlInputStream = new ByteArrayInputStream(xmlResponse.getBytes());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(edu.tamu.app.model.response.marc.VoyagerServiceData.class);
		
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		edu.tamu.app.model.response.marc.VoyagerServiceData response = (edu.tamu.app.model.response.marc.VoyagerServiceData) unmarshaller.unmarshal(xmlInputStream);
		
		return response;
	}

}
