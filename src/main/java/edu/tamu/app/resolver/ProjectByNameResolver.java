package edu.tamu.app.resolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

@Component
public class ProjectByNameResolver implements ObjectIdResolver {

    private static final String COLUMN_NAME = "name";

    @PersistenceContext
    private EntityManager entityManager;

    public ProjectByNameResolver() {

    }

    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey idKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> e = query.from(idKey.scope);
        query.select(e).distinct(true);
        query.where(cb.equal(e.get(COLUMN_NAME), idKey.key));
        Object entity = entityManager.createQuery(query).getSingleResult();
        return entity;
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return getClass().isAssignableFrom(resolverType.getClass());
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object c) {
        return this;
    }

}
