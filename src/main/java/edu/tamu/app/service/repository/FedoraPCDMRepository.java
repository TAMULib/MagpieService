package edu.tamu.app.service.repository;

import java.io.File;
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

import edu.tamu.app.model.Document;

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
		return buildContainerUrl()+File.separator+membersEndpoint;
	}
		
	private String getObjectsUrl() {
		return buildRepoRestUrl()+File.separator+objectsEndpoint;
	}
	
	@Override
	protected String createItemContainer(String slugName) throws FileNotFoundException, IOException {
		// Create the item container
		String desiredItemUrl = getObjectsUrl()+File.separator+slugName;
		String actualItemUrl = generatePutRequest(desiredItemUrl,null,buildPCDMObject(desiredItemUrl));
		// Create a pages container within the item container 
		generatePutRequest(actualItemUrl+File.separator+pagesEndpoint+File.separator,null,buildPCDMObject(actualItemUrl+File.separator+pagesEndpoint));
		generatePutRequest(actualItemUrl+File.separator+"orderProxies",null,buildPCDMOrderProxy(actualItemUrl+File.separator+"orderProxies"));

		return actualItemUrl;
	}
	
	@Override
	protected String createResource(String filePath, String resourceContainerPath, String slugName) throws IOException {
		generatePutRequest(resourceContainerPath,null,buildPCDMObject(resourceContainerPath));

		generatePutRequest(resourceContainerPath+File.separator+"files",null,buildPCDMFileContainer(resourceContainerPath+File.separator+"files"));

		return super.createResource(filePath, resourceContainerPath+File.separator+"files",slugName);
	}
	
	@Override
	protected String pushFiles(Document document, String itemContainerPath, File[] files) throws IOException {

		String itemPath = null;
		ProxyPage[] proxyPages = new ProxyPage[files.length];
		//TODO Since magpie hasn't been modified to support defining pages, yet, we're treating each file as a different page to provide a proof of concept for a paged PCDM collection pushed to Fedora
		
		int x = 0;
		for (File file : files) {
		    if (file.isFile()) {
		    	itemPath = createResource(document.getDocumentPath()+File.separator+file.getName(),itemContainerPath+File.separator+pagesEndpoint+File.separator+"page_"+x, file.getName());
				proxyPages[x] = new ProxyPage(itemContainerPath+File.separator+"orderProxies"+File.separator+"page_"+x,itemContainerPath);
		    }
		    x++;
		}
		
		setUpPageProxies(proxyPages);
		return itemPath;
	}
	
	private void setUpPageProxies(ProxyPage[] proxyPages) throws IOException {
		// Helper class for generating order structure of Proxy Pages
		class ProxyBuilder {
			private ProxyPage[] proxyPages;
			ProxyBuilder(ProxyPage[] proxyPages) {
				this.proxyPages = proxyPages;
				buildProxyOrder(this.proxyPages.length-1);
			}
			
			public ProxyPage[] getOrderedProxyPages() {
				return proxyPages;
			}
			
			private int buildProxyOrder(int currentIndex) {
				if (currentIndex > 0) {
					proxyPages[currentIndex].setPrevUrl(proxyPages[currentIndex-1].getProxyUrl());
				}
				if (currentIndex < (proxyPages.length-1)) {
					proxyPages[currentIndex].setNextUrl(proxyPages[currentIndex+1].getProxyUrl());
				}
				if (currentIndex > 0) {
					return buildProxyOrder(currentIndex-1);
				} else {
					return 0;
				}
			}
		}
		ProxyBuilder proxyBuilder = new ProxyBuilder(proxyPages);

		/* TODO solve spqarql-update issue
		ProxyPage[] orderedProxyPages = proxyBuilder.getOrderedProxyPages();
		for (ProxyPage orderedProxyPage : orderedProxyPages) {
			generatePutRequest(orderedProxyPage.getProxyUrl(),null,buildPCDMPageProxy(orderedProxyPage.getProxyUrl(),orderedProxyPage.getProxyInUrl(),orderedProxyPage.getPageUrl(),orderedProxyPage.getNextUrl(),orderedProxyPage.getPrevUrl()));
		}
		*/
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
		return generatePutRequest(containerUrl+"/"+slugName,null,buildPCDMObject(containerUrl+"/"+slugName));
	}
	
	private Model buildPCDMObject(String containerUrl) throws FileNotFoundException {
		logger.debug("Building PCDM Object at <"+containerUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(containerUrl);
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		return model;
	}
	
	private Model buildPCDMMember(String memberUrl) {
		logger.debug("Building PCDM Member at <"+memberUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(memberUrl);
		resource.addProperty(RDF.type,model.createProperty(LDP.IndirectContainer.getIRIString()));
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()),model.createProperty("http://pcdm.org/models#hasMember"));
		resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(buildContainerUrl()+"/"));
		resource.addProperty(model.createProperty(LDP.insertedContentRelation.getIRIString()), model.createProperty("http://www.openarchives.org/ore/terms#proxyFor"));
		return model;
	}
	
	private Model buildPCDMPageProxy(String proxyUrl,String proxyInUrl, String pageUrl,String nextUrl,String prevUrl) {
		logger.debug("Building PCDM Page Proxy at <"+proxyUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(pageUrl);
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		resource.addProperty(model.createProperty("http://www.openarchives.org/ore/terms#proxyFor"),model.createProperty(pageUrl));
		resource.addProperty(model.createProperty("http://www.openarchives.org/ore/terms#proxyIn"),model.createProperty(proxyInUrl));
		if (nextUrl != null) {
			resource.addProperty(model.createProperty("http://www.iana.org/assignments/relation/#next"),model.createProperty(nextUrl));
		}
		if (prevUrl != null) {
			resource.addProperty(model.createProperty("http://www.iana.org/assignments/relation/#prev"),model.createProperty(prevUrl));
		}

		return model;
	}
	
	private Model buildPCDMOrderProxy(String orderUrl) {
		logger.debug("Building PCDM Order Proxy at <"+orderUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(orderUrl);
		resource.addProperty(RDF.type,model.createProperty(LDP.DirectContainer.getIRIString()));
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		resource.addProperty(model.createProperty(LDP.isMemberOfRelation.getIRIString()),model.createProperty("http://www.openarchives.org/ore/terms#proxyIn"));
		resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(buildContainerUrl()+"/"));
		return model;
	}
	
	private Model buildPCDMFileContainer(String containerUrl) {
		logger.debug("Building PCDM File container at <"+containerUrl+">");
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(containerUrl);
		resource.addProperty(RDF.type,model.createProperty(LDP.DirectContainer.getIRIString()));
		resource.addProperty(RDF.type,model.createProperty("http://pcdm.org/models#Object"));
		resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(containerUrl));
		resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()),model.createProperty("http://pcdm.org/models#hasFile"));
		return model;
	}
	
	// intermediary for prepping PCDM Proxy Pages to be pushed to the Fedora repo
	class ProxyPage {
		private String proxyUrl, proxyInUrl, pageUrl, nextUrl, prevUrl;
		
		public ProxyPage(String pageUrl, String proxyInUrl) {
			setPageUrl(pageUrl);
			setProxyUrl(pageUrl+"proxy");
			setProxyInUrl(proxyInUrl);
		}
		
		public String getProxyUrl() {
			return proxyUrl;
		}

		public void setProxyUrl(String proxyUrl) {
			this.proxyUrl = proxyUrl;
		}

		public String getProxyInUrl() {
			return proxyInUrl;
		}

		public void setProxyInUrl(String proxyInUrl) {
			this.proxyInUrl = proxyInUrl;
		}

		public String getPageUrl() {
			return pageUrl;
		}

		public void setPageUrl(String pageUrl) {
			this.pageUrl = pageUrl;
		}

		public String getNextUrl() {
			return nextUrl;
		}

		public void setNextUrl(String nextUrl) {
			this.nextUrl = nextUrl;
		}

		public String getPrevUrl() {
			return prevUrl;
		}

		public void setPrevUrl(String prevUrl) {
			this.prevUrl = prevUrl;
		}

	}

}
