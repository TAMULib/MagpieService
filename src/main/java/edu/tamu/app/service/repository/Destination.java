package edu.tamu.app.service.repository;

import java.io.IOException;

import edu.tamu.app.model.Document;
import edu.tamu.app.service.registry.MagpieService;

public interface Destination extends MagpieService {

    public Document push(Document document) throws IOException;

}