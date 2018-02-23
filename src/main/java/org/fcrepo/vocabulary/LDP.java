/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.vocabulary;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.SimpleRDF;

/**
 * RDF Terms from the LDP Vocabulary
 * 
 * @author acoburn
 */
public class LDP {

    private static RDF rdf = new SimpleRDF();

    /* Namespace */
    public static String uri = "http://www.w3.org/ns/ldp#";

    /* Classes */
    public static IRI BasicContainer = rdf.createIRI(uri + "BasicContainer");
    public static IRI Container = rdf.createIRI(uri + "Container");
    public static IRI DirectContainer = rdf.createIRI(uri + "DirectContainer");
    public static IRI IndirectContainer = rdf.createIRI(uri + "IndirectContainer");
    public static IRI NonRDFSource = rdf.createIRI(uri + "NonRDFSource");
    public static IRI Resource = rdf.createIRI(uri + "Resource");
    public static IRI RDFSource = rdf.createIRI(uri + "RDFSource");

    /* Properties */
    public static IRI contains = rdf.createIRI(uri + "contains");
    public static IRI hasMemberRelation = rdf.createIRI(uri + "hasMemberRelation");
    public static IRI inbox = rdf.createIRI(uri + "inbox");
    public static IRI insertedContentRelation = rdf.createIRI(uri + "insertedContentRelation");
    public static IRI isMemberOfRelation = rdf.createIRI(uri + "isMemberOfRelation");
    public static IRI member = rdf.createIRI(uri + "member");
    public static IRI membershipResource = rdf.createIRI(uri + "membershipResource");

    /* Prefer-related Classes */
    public static IRI PreferContainment = rdf.createIRI(uri + "PreferContainment");
    public static IRI PreferMembership = rdf.createIRI(uri + "PreferMembership");
    public static IRI PreferMinimalContainer = rdf.createIRI(uri + "PreferMinimalContainer");

    /* Paging Classes */
    public static IRI PageSortCriterion = rdf.createIRI(uri + "PageSortCriterion");
    public static IRI Ascending = rdf.createIRI(uri + "Ascending");
    public static IRI Descending = rdf.createIRI(uri + "Descending");
    public static IRI Page = rdf.createIRI(uri + "Page");

    /* Paging Properties */
    public static IRI constrainedBy = rdf.createIRI(uri + "constrainedBy");
    public static IRI pageSortCriteria = rdf.createIRI(uri + "pageSortCriteria");
    public static IRI pageSortPredicate = rdf.createIRI(uri + "pageSortPredicate");
    public static IRI pageSortOrder = rdf.createIRI(uri + "pageSortOrder");
    public static IRI pageSortCollation = rdf.createIRI(uri + "pageSortCollation");
    public static IRI pageSequence = rdf.createIRI(uri + "pageSequence");

    /* Other Classes */
    public static IRI MemberSubject = rdf.createIRI(uri + "MemberSubject");

    private LDP() {
        // prevent instantiation
    }

}
