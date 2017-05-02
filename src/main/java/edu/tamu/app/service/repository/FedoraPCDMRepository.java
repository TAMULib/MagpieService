package edu.tamu.app.service.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.fcrepo.vocabulary.LDP;

public class FedoraPCDMRepository extends FedoraRepository {
	private String membersEndpoint = "members";
	private String objectsEndpoint = "objects";
	private String pagesEndpoint = "pages";

	public FedoraPCDMRepository(String repoUrl, String restPath, String containerPath, String username,
			String password) {
		super(repoUrl, restPath, containerPath, username, password);
		objectsEndpoint = containerPath+"_"+objectsEndpoint;
	}
	
	@Override
	protected void prepForPush() throws IOException {
		verifyPCDMStructures();
	}
	
	private void verifyPCDMStructures() throws IOException {
		confirmProjectContainerExists();
		
		String pcdmMembersUrl = getMembersUrl();
		
		//make sure we have a members resource to represent properties of the items in the context of the collection
		if (!resourceExists(pcdmMembersUrl)) {
			generatePutRequest(pcdmMembersUrl,null,buildPCDMMember(pcdmMembersUrl));
		}

		//make sure we have an objects resource to store items
		String objectsUrl = getObjectsUrl();
		if (!resourceExists(objectsUrl)) {
			generatePutRequest(objectsUrl,null,buildPCDMMember(objectsUrl));
		}

	}
	
	private String getMembersUrl() {
		return buildContainerUrl()+"/"+membersEndpoint;
	}
		
	private String getObjectsUrl() {
		return buildRepoRestUrl()+"/"+objectsEndpoint;
	}
	
	@Override
	protected String createItemContainer(String slugName) throws FileNotFoundException, IOException {
		String desiredItemUrl = getObjectsUrl()+"/"+slugName;
		
		String actualItemUrl = generatePutRequest(desiredItemUrl,null,buildPCDMObject(desiredItemUrl));
		// Create a pages container for within the item container 
		generatePutRequest(actualItemUrl+"/"+pagesEndpoint+"/",null,buildPCDMObject(actualItemUrl+"/"+pagesEndpoint));

		return actualItemUrl;
	}
	
	@Override
	protected String createResource(String filePath, String itemContainerPath, String slugName) throws IOException {
		String resourceUrl = itemContainerPath+"/"+pagesEndpoint+"/"+slugName;
		generatePutRequest(resourceUrl,null,buildPCDMObject(resourceUrl));
		return super.createResource(filePath, itemContainerPath+"/"+pagesEndpoint+"/", slugName);
	}
	
	private String generatePutRequest(String url, Map<String,String> requestProperties,Model rdfObject) throws IOException {
		HttpURLConnection connection = buildBasicFedoraConnection(url);

		boolean hasContentType = false;
		if (requestProperties != null) {
			if (requestProperties.containsKey("CONTENT-TYPE")) {
				hasContentType = true;
			}

			requestProperties.forEach((k,v) -> {
		        connection.setRequestProperty(k, v);
			});
		}
		if (!hasContentType) {
			connection.setRequestProperty("CONTENT-TYPE", "application/rdf+xml");
		}
		
		
		connection.setRequestMethod("PUT");		

		connection.setDoOutput(true);

		OutputStream os = connection.getOutputStream();

		rdfObject.write(os);
		logger.debug("*** JENA GENERATED RDF+XML for <"+url+"> ***");
		logger.debug(os.toString());		
		System.out.println("*** JENA GENERATED RDF+XML <"+url+"> ***");
		System.out.println(os.toString());		
		os.close();

		
		int responseCode = connection.getResponseCode();
				
		if(responseCode != 201) {
			logger.debug("Server message: "+connection.getResponseMessage());
			throw new IOException("Could not complete PUT request. Server responded with " + responseCode);
		}
				
		return connection.getHeaderField("Location");
		
	}
	
	@Override
	protected String createContainer(String containerUrl, String slugName) throws IOException {
		logger.debug("creating container: "+containerUrl+"/"+slugName);
		
		/*
		Map<String,String> requestProperties = new HashMap<String,String>();
		if (slugName != null) {
			requestProperties.put("slug", slugName);
		}
*/
		return generatePutRequest(containerUrl+"/"+slugName,null,buildPCDMObject(containerUrl+"/"+slugName));
	}
	
	private Model buildPCDMObject(String containerUrl) throws FileNotFoundException {
		/*
			@prefix pcdm: <http://pcdm.org/models#>
			  
			<> a pcdm:Object .
		 */
		logger.debug("Building PCDM Object at <"+containerUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(containerUrl);
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		return model;
	}
	
	private Model buildPCDMMember(String memberUrl) {
		/*
		 *	@prefix ldp: <http://www.w3.org/ns/ldp#>
			@prefix pcdm: <http://pcdm.org/models#>
			@prefix ore: <http://www.openarchives.org/ore/terms/>
			 
			<> a ldp:IndirectContainer, pcdm:Object ;
			  ldp:membershipResource </fcrepo/rest/collections/primeros-libros/> ;
			  ldp:hasMemberRelation pcdm:hasMember ;
			  ldp:insertedContentRelation ore:proxyFor .

		 */
		logger.debug("Building PCDM Member at <"+memberUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(memberUrl);
		resource.addProperty(RDF.type,model.createProperty(LDP.IndirectContainer.getIRIString()));
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()),model.createProperty("http://pcdm.org/models#hasMember"));
		resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(buildContainerUrl()+"/"));
		resource.addProperty(model.createProperty(LDP.insertedContentRelation.getIRIString()), model.createProperty("http://www.openarchives.org/ore/terms/proxyFor"));
		return model;
	}

}
