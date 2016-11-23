package edu.tamu.app.service.authority;

import edu.tamu.app.model.Document;
import edu.tamu.app.service.registry.MagpieService;

public interface Authority extends MagpieService {

    public Document populate(Document document);

}
