package edu.tamu.app.resolver;

import org.springframework.stereotype.Component;

import edu.tamu.weaver.data.resolver.EntityByPropertyResolver;

@Component
public class ProjectByNameResolver extends EntityByPropertyResolver {

    @Override
    protected String getPropertyName() {
        return "name";
    }

}
