package edu.tamu.app.resolver;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.ObjectIdResolver;

import edu.tamu.weaver.data.resolver.EntityByPropertyResolver;

@Component
public class ProjectByNameResolver extends EntityByPropertyResolver {

    @Override
    protected String getPropertyName() {
        return "name";
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
