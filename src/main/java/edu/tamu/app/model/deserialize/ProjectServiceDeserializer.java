package edu.tamu.app.model.deserialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.ProjectService;

public class ProjectServiceDeserializer extends JsonDeserializer<ProjectService> {

    @Override
    public ProjectService deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        Class<? extends ProjectService> projectServiceClass = null;
        System.out.println(projectServiceClass.getName());
       // ctxt.
        /*
        if(checkConditionsForUserInstance()) {
            instanceClass = UserInstance.class;
        } else { 
            instanceClass = HardwareInstance.class;
        }
        */  
        if (projectServiceClass != null) {
            return mapper.readValue(jp, projectServiceClass);
        } else {
            return null;
        }
    }

}
