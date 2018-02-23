package edu.tamu.app.model.repo.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification<E> implements Specification<E> {

    private Map<String, String[]> filters;

    public DocumentSpecification(Map<String, String[]> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> namePredicates = new ArrayList<Predicate>();
        List<Predicate> statusPredicates = new ArrayList<Predicate>();
        List<Predicate> orStatusPredicates = new ArrayList<Predicate>();
        List<Predicate> notStatusPredicates = new ArrayList<Predicate>();
        List<Predicate> annotatorPredicates = new ArrayList<Predicate>();
        List<Predicate> projectPredicates = new ArrayList<Predicate>();

        for (Map.Entry<String, String[]> entry : filters.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            switch (key) {

            case "status":
                for (String value : values) {
                    switch (value) {
                    case "!Published":
                        notStatusPredicates.add(cb.notLike(cb.lower(root.get(key).as(String.class)), "%" + value.substring(1, value.length()).toLowerCase() + "%"));
                        break;
                    case "!Accepted":
                        notStatusPredicates.add(cb.notLike(cb.lower(root.get(key).as(String.class)), "%" + value.substring(1, value.length()).toLowerCase() + "%"));
                        break;
                    case "Assigned":
                        orStatusPredicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
                        break;
                    case "Rejected":
                        orStatusPredicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
                        break;
                    default:
                        statusPredicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
                        break;
                    }
                }
                break;
            case "name":
                for (String value : values) {
                    namePredicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
                }
                break;
            case "annotator":
                for (String value : values) {
                    annotatorPredicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
                }
                break;
            case "projects":
                for (String value : values) {
                    projectPredicates.add(cb.equal(root.get("project").get("id").as(Integer.class), value.toLowerCase()));
                }
                break;
            default:
                break;
            }

        }

        Predicate predicate = null;

        if (orStatusPredicates.size() > 0) {
            if (projectPredicates.size() > 0) {
                predicate = cb.and(cb.and(namePredicates.toArray(new Predicate[namePredicates.size()])), cb.and(statusPredicates.toArray(new Predicate[statusPredicates.size()])), cb.or(orStatusPredicates.toArray(new Predicate[orStatusPredicates.size()])), cb.and(notStatusPredicates.toArray(new Predicate[notStatusPredicates.size()])), cb.and(annotatorPredicates.toArray(new Predicate[annotatorPredicates.size()])), cb.or(projectPredicates.toArray(new Predicate[projectPredicates.size()])));
            } else {
                predicate = cb.and(cb.and(namePredicates.toArray(new Predicate[namePredicates.size()])), cb.and(statusPredicates.toArray(new Predicate[statusPredicates.size()])), cb.or(orStatusPredicates.toArray(new Predicate[orStatusPredicates.size()])), cb.and(notStatusPredicates.toArray(new Predicate[notStatusPredicates.size()])), cb.and(annotatorPredicates.toArray(new Predicate[annotatorPredicates.size()])));
            }
        } else {
            if (projectPredicates.size() > 0) {
                predicate = cb.and(cb.and(namePredicates.toArray(new Predicate[namePredicates.size()])), cb.and(statusPredicates.toArray(new Predicate[statusPredicates.size()])), cb.and(notStatusPredicates.toArray(new Predicate[notStatusPredicates.size()])), cb.and(annotatorPredicates.toArray(new Predicate[annotatorPredicates.size()])), cb.or(projectPredicates.toArray(new Predicate[projectPredicates.size()])));
            } else {
                predicate = cb.and(cb.and(namePredicates.toArray(new Predicate[namePredicates.size()])), cb.and(statusPredicates.toArray(new Predicate[statusPredicates.size()])), cb.and(notStatusPredicates.toArray(new Predicate[notStatusPredicates.size()])), cb.and(annotatorPredicates.toArray(new Predicate[annotatorPredicates.size()])));
            }
        }

        return predicate;
    }

}