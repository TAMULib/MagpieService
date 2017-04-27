package edu.tamu.app.service.repository;

import java.io.IOException;

import org.apache.commons.rdf.api.IRI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.fcrepo.vocabulary.*;

import edu.tamu.app.model.Document;

public class FedoraPCDMRepository extends FedoraRepository {

	public FedoraPCDMRepository(String repoUrl, String restPath, String containerPath, String username,
			String password) {
		super(repoUrl, restPath, containerPath, username, password);
		buildLDPBasicContainer("test");
	}
	
	@Override
	public Document push(Document document) throws IOException {
		System.out.println("PRETENDING TO PUSH");
		return null;
	}

	@Override
	protected String createContainer(String containerUrl, String slugName) throws IOException {
		return null;
	}
	
	private void buildLDPBasicContainer(String containerName) {
		/*
		 * @prefix ldp: <http://www.w3.org/ns/ldp#>
 
<> a ldp:BasicContainer .
		 */

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(buildRepoRestUrl()+containerName+"/");
		resource.addProperty(RDF.type,LDP.BasicContainer.getIRIString());
		System.out.println("container data: "+resource.toString());
	}
	
	private void getPCDMDirectObjectTriple() {
		/*
		 * @prefix ldp: <http://www.w3.org/ns/ldp#>
@prefix pcdm: <http://pcdm.org/models#>
 
<> a ldp:DirectContainer, pcdm:Object ;
  ldp:membershipResource </fcrepo/rest/objects/confessionario-en-lengua-mexicana/> ;
  ldp:hasMemberRelation pcdm:hasMember .
		 */
//		curl --user fedoraAdmin:secret3 -i -XPUT -H"Content-Type: text/turtle" --data-binary @pcdm-object.ttl localhost:8080/fcrepo/rest/collections/

		ModelFactory.createDefaultModel();
	}
}
