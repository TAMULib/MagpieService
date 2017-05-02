package edu.tamu.app.service.repository;

import java.io.IOException;

import edu.tamu.app.model.Document;

public abstract class AbstractFedoraRepository implements Repository {

	@Override
	public Document push(Document document) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	abstract void prepForPush() throws IOException;
	abstract String createItemContainer(String slugName) throws IOException;
	abstract boolean resourceExists(String uri) throws IOException;
	abstract String createResource(String filePath, String itemContainerPath, String slugName) throws IOException;
	abstract String createContainer(String containerUrl, String slugName) throws IOException;

}
