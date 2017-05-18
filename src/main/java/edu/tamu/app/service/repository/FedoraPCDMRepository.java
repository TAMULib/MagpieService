package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    public FedoraPCDMRepository(String repoUrl, String restPath, String containerPath, String username, String password) {
        super(repoUrl, restPath, containerPath, username, password);
        objectsEndpoint = containerPath + "_" + objectsEndpoint;
    }

    @Override
    protected void prepForPush() throws IOException {
        super.confirmProjectContainerExists();
        verifyPCDMStructures();
    }

    @Override
    protected String createResource(String filePath, String resourceContainerPath, String slugName) throws IOException {
        generatePutRequest(resourceContainerPath, null, buildPCDMObject(resourceContainerPath));

        generatePutRequest(resourceContainerPath + File.separator + "files", null, buildPCDMFileContainer(resourceContainerPath + File.separator + "files", resourceContainerPath));

        String resourceUri = super.createResource(filePath, resourceContainerPath + File.separator + "files", slugName);

        updateFileMetadata(resourceUri);

        return resourceUri;
    }

    @Override
    protected String createItemContainer(String slugName) throws FileNotFoundException, IOException {
        // Create the item container
        String desiredItemUrl = getObjectsUrl() + File.separator + slugName;
        String actualItemUrl = generatePutRequest(desiredItemUrl, null, buildPCDMObject(desiredItemUrl));
        generatePutRequest(getMembersUrl() + File.separator + slugName + "Proxy", null, buildPCDMItemProxy(getMembersUrl() + File.separator + slugName + "Proxy", actualItemUrl + File.separator));
        // Create a pages container within the item container
        generatePutRequest(actualItemUrl + File.separator + pagesEndpoint + File.separator, null, buildPCDMDirectContainer(actualItemUrl + File.separator + pagesEndpoint, actualItemUrl));
        // Set up the container that will hold the page order proxies
        generatePutRequest(actualItemUrl + File.separator + "orderProxies", null, buildPCDMOrderProxy(actualItemUrl + File.separator + "orderProxies", actualItemUrl));

        return actualItemUrl;
    }

    @Override
    protected String createContainer(String containerUrl, String slugName) throws IOException {
        logger.debug("creating container: " + containerUrl + "/" + slugName);
        return generatePutRequest(containerUrl + "/" + slugName, null, buildPCDMObject(containerUrl + "/" + slugName));
    }

    @Override
    protected String pushFiles(Document document, String itemContainerPath, File[] files) throws IOException {

        ProxyPage[] proxyPages = new ProxyPage[files.length];
        // TODO Since magpie hasn't been modified to support defining pages, yet, we're treating each file as a different page to provide a proof of concept for a paged PCDM collection pushed to
        // Fedora
        int x = 0;
        for (File file : files) {
            if (file.isFile() && !file.isHidden()) {
                String pagePath = itemContainerPath + File.separator + pagesEndpoint + File.separator + "page_" + x;
                createResource(document.getDocumentPath() + File.separator + file.getName(), pagePath, file.getName());
                proxyPages[x] = new ProxyPage(itemContainerPath + File.separator + "orderProxies" + File.separator + "page_" + x + "_proxy", pagePath, itemContainerPath);
                generatePutRequest(proxyPages[x].getProxyUrl(), null, buildPCDMPageProxy(proxyPages[x].getProxyUrl(), proxyPages[x].getProxyInUrl(), proxyPages[x].getProxyForUrl()));
            }
            x++;
        }

        orderPageProxies(proxyPages);
        // set the first/last pages for the item
        executeSparqlUpdate(itemContainerPath, buildPCDMItemOrderUpdate(itemContainerPath, proxyPages[0].getProxyUrl(), proxyPages[proxyPages.length - 1].getProxyUrl()));

        return itemContainerPath;
    }

    protected void updateFileMetadata(String fileUri) throws IOException {
        executeSparqlUpdate(fileUri + File.separator + "fcr:metadata", buildPCDMFile());
    }

    private void verifyPCDMStructures() throws IOException {

        String pcdmMembersUrl = getMembersUrl();

        // make sure we have a members resource to represent properties of the items in the context of the collection
        if (!resourceExists(pcdmMembersUrl)) {
            generatePutRequest(pcdmMembersUrl, null, buildPCDMMember(pcdmMembersUrl));
        }

        // make sure we have an objects resource to store items
        String objectsUrl = getObjectsUrl();
        if (!resourceExists(objectsUrl)) {
            generatePutRequest(objectsUrl, null, buildPCDMMember(objectsUrl));
        }

    }

    private String getMembersUrl() {
        return buildContainerUrl() + File.separator + membersEndpoint;
    }

    private String getObjectsUrl() {
        return buildRepoRestUrl() + File.separator + objectsEndpoint;
    }

    private void orderPageProxies(ProxyPage[] proxyPages) throws IOException {
        // Helper class for generating order structure for an array of Proxy Pages
        class ProxyBuilder {
            private ProxyPage[] proxyPages;

            ProxyBuilder(ProxyPage[] proxyPages) {
                this.proxyPages = proxyPages;
                buildProxyOrder(this.proxyPages.length - 1);
            }

            public ProxyPage[] getOrderedProxyPages() {
                return proxyPages;
            }

            private int buildProxyOrder(int currentIndex) {
                if (currentIndex < (proxyPages.length - 1)) {
                    proxyPages[currentIndex].setNextUrl(proxyPages[currentIndex + 1].getProxyUrl());
                }
                if (currentIndex > 0) {
                    proxyPages[currentIndex].setPrevUrl(proxyPages[currentIndex - 1].getProxyUrl());
                    return buildProxyOrder(currentIndex - 1);
                } else {
                    return 0;
                }
            }
        }
        ProxyBuilder proxyBuilder = new ProxyBuilder(proxyPages);

        ProxyPage[] orderedProxyPages = proxyBuilder.getOrderedProxyPages();
        for (ProxyPage orderedProxyPage : orderedProxyPages) {
            executeSparqlUpdate(orderedProxyPage.getProxyUrl(), buildPCDMPageProxyOrderUpdate(orderedProxyPage.getProxyUrl(), orderedProxyPage.getNextUrl(), orderedProxyPage.getPrevUrl()));
        }
    }

    private String generatePutRequest(String url, Map<String, String> requestProperties, Model rdfObject) throws IOException {
        HttpURLConnection connection = buildBasicFedoraConnection(url);

        boolean hasContentType = false;
        if (requestProperties != null) {
            if (requestProperties.containsKey("CONTENT-TYPE")) {
                hasContentType = true;
            }

            requestProperties.forEach((k, v) -> {
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

        logger.debug("*** JENA GENERATED RDF+XML for <" + url + "> ***");
        logger.debug(os.toString());
        os.close();

        int responseCode = connection.getResponseCode();

        if (responseCode != 201) {
            logger.debug("Server message: " + connection.getResponseMessage());
            throw new IOException("Could not complete PUT request. Server responded with " + responseCode);
        }

        return connection.getHeaderField("Location");

    }

    private void executeSparqlUpdate(String uri, String sparqlQuery) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPatch httpPatch;
        try {
            httpPatch = new HttpPatch(new URI(uri));

            logger.debug("**** PATCHING SPARQL UPDATE ****");
            logger.debug(sparqlQuery);
            StringEntity data = new StringEntity(sparqlQuery);

            data.setContentType("application/sparql-update");

            httpPatch.setEntity(data);

            httpPatch.addHeader("Authorization", getEncodedBasicAuthorization());
            httpPatch.addHeader("CONTENT-TYPE", "application/sparql-update");
            CloseableHttpResponse response = httpClient.execute(httpPatch);

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 204) {
                throw new IOException("Could not complete PATCH request. Server responded with " + responseCode);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private Model buildPCDMObject(String containerUrl) throws FileNotFoundException {
        logger.debug("Building PCDM Object at <" + containerUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(containerUrl);
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        return model;
    }

    private Model buildPCDMDirectContainer(String directContainerUrl, String membershipResourceUrl) throws FileNotFoundException {
        logger.debug("Building Direct Container at <" + directContainerUrl + "> with member <" + membershipResourceUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(directContainerUrl);
        resource.addProperty(RDF.type, model.createProperty(LDP.DirectContainer.getIRIString()));
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()), model.createProperty("http://pcdm.org/models#hasMember"));
        resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(membershipResourceUrl));
        return model;
    }

    private Model buildPCDMMember(String membersUrl) {
        logger.debug("Building PCDM Member at <" + membersUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(membersUrl);
        resource.addProperty(RDF.type, model.createProperty(LDP.IndirectContainer.getIRIString()));
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()), model.createProperty("http://pcdm.org/models#hasMember"));
        resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(buildContainerUrl()));
        resource.addProperty(model.createProperty(LDP.insertedContentRelation.getIRIString()), model.createProperty("http://www.openarchives.org/ore/terms#proxyFor"));
        return model;
    }

    private Model buildPCDMItemProxy(String proxyUrl, String itemUrl) {
        logger.debug("Building PCDM ORE Proxy at <" + proxyUrl + "> for <" + itemUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(proxyUrl);
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty("http://www.openarchives.org/ore/terms#proxyFor"), model.createProperty(itemUrl));
        return model;
    }

    private Model buildPCDMPageProxy(String proxyUrl, String proxyInUrl, String proxyForUrl) {
        logger.debug("Building PCDM Page Proxy at <" + proxyUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(proxyUrl);
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty("http://www.openarchives.org/ore/terms#proxyFor"), model.createProperty(proxyForUrl));
        resource.addProperty(model.createProperty("http://www.openarchives.org/ore/terms#proxyIn"), model.createProperty(proxyInUrl));
        return model;
    }

    private String buildPCDMPageProxyOrderUpdate(String proxyUrl, String nextUrl, String prevUrl) {
        logger.debug("Setting order for PCDM Page Proxy at <" + proxyUrl + ">");
        String orderedPageProxy = "PREFIX iana: <http://www.iana.org/assignments/relation/>" + "INSERT {";
        if (nextUrl != null) {
            orderedPageProxy += "<> iana:next <" + nextUrl + "> .";
        }
        if (prevUrl != null) {
            orderedPageProxy += "<> iana:prev <" + prevUrl + ">";
        }
        orderedPageProxy += "} WHERE {" + "}";
        return orderedPageProxy;
    }

    private String buildPCDMItemOrderUpdate(String itemUrl, String firstUrl, String lastUrl) {
        logger.debug("Setting first/last order for PCDM Item at <" + itemUrl + ">");
        String orderedItemUpdate = "PREFIX iana: <http://www.iana.org/assignments/relation/>" + "INSERT {" + "<> iana:first <" + firstUrl + "> ." + "<> iana:last <" + lastUrl + ">" + "} WHERE {" + "}";
        return orderedItemUpdate;
    }

    private Model buildPCDMOrderProxy(String orderUrl, String membershipResourceUrl) {
        logger.debug("Building PCDM Order Proxy at <" + orderUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(orderUrl);
        resource.addProperty(RDF.type, model.createProperty(LDP.DirectContainer.getIRIString()));
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty(LDP.isMemberOfRelation.getIRIString()), model.createProperty("http://www.openarchives.org/ore/terms#proxyIn"));
        resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(membershipResourceUrl));
        return model;
    }

    private Model buildPCDMFileContainer(String fileContainerUrl, String membershipResourceUrl) {
        logger.debug("Building PCDM File container at <" + fileContainerUrl + "> for <" + membershipResourceUrl + ">");
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(fileContainerUrl);
        resource.addProperty(RDF.type, model.createProperty(LDP.DirectContainer.getIRIString()));
        resource.addProperty(RDF.type, model.createProperty("http://pcdm.org/models#Object"));
        resource.addProperty(model.createProperty(LDP.membershipResource.getIRIString()), model.createProperty(membershipResourceUrl));
        resource.addProperty(model.createProperty(LDP.hasMemberRelation.getIRIString()), model.createProperty("http://pcdm.org/models#hasFile"));
        return model;
    }

    private String buildPCDMFile() {
        String updateQuery = "PREFIX pcdm: <http://pcdm.org/models#>" + "INSERT {" + "<> a pcdm:File" + "} WHERE { }";

        return updateQuery;
    }

    // intermediary for prepping PCDM Proxy Pages to be pushed to the Fedora repo
    class ProxyPage {
        private String proxyUrl, proxyInUrl, proxyForUrl, nextUrl, prevUrl;

        public ProxyPage(String proxyUrl, String proxyForUrl, String proxyInUrl) {
            setProxyUrl(proxyUrl);
            setProxyForUrl(proxyForUrl);
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

        public String getProxyForUrl() {
            return proxyForUrl;
        }

        public void setProxyForUrl(String proxyForUrl) {
            this.proxyForUrl = proxyForUrl;
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
