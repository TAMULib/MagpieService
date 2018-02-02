package edu.tamu.app.model.response.marc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FlatMARC {

    private static final Logger logger = Logger.getLogger(FlatMARC.class);

    private String dc_creator = "";
    private String dc_title = "";
    private String dc_date_created = "";
    private String dc_date_issued = "";
    private List<String> dc_subject_lcsh = new ArrayList<String>();
    private String dc_subject = "";
    private String dc_description = "";
    private String dc_description_abstract = "";
    private String thesis_degree_grantor = "";
    private String dc_contributor_advisor = "";
    private List<String> dc_contributor_committeeMember = new ArrayList<String>();
    private String dc_identifier_uri = "";

    public FlatMARC(VoyagerServiceData voyagerServiceData) {

        if (voyagerServiceData.getServiceData() != null) {

            Datafield[] dataField = voyagerServiceData.getServiceData().getHoldingsRecord().getBibRecord().getMarcRecord().getDatafield();

            for (Datafield df : dataField) {

                // dc.creator
                if (df.getTag().equals("100")) {
                    Subfield[] subFields = df.getSubfield();
                    if (subFields.length > 0) {
                        dc_creator = scrubField(".", subFields[0].getValue());
                    }
                }

                // dc.title
                if (df.getTag().equals("245")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (subField.getCode().equals("a") || subField.getCode().equals("b")) {
                            dc_title += scrubField(".", subField.getValue());
                        }
                    }
                }

                // dc.date.created and dc.date.issued
                if (df.getTag().equals("260")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (subField.getCode().equals("c")) {
                            dc_date_issued = dc_date_created = scrubField(".", subField.getValue());
                        }
                    }
                }

                // dc.date.created and dc.date.issued
                if (df.getTag().equals("264")) {
                    if (dc_date_issued.equals("")) {
                        Subfield[] subFields = df.getSubfield();
                        for (Subfield subField : subFields) {
                            if (df.getInd2().equals("0") || df.getInd2().equals("1")) {
                                dc_date_issued = dc_date_created = scrubField(".", subField.getValue());
                            }
                        }
                    }
                }

                // dc.description
                if (df.getTag().equals("500") || df.getTag().equals("502")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (subField.getCode().equals("a") || subField.getCode().equals("b")) {
                            if (dc_description.length() > 0) {
                                logger.info("Multiple description found. Deferring to the first. Ignoring: " + scrubField(".", subField.getValue()));
                                continue;
                            }
                            dc_description += scrubField(".", subField.getValue());
                        }
                    }
                }

                // thesis.degree.grantor
                if (df.getTag().equals("502")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (subField.getCode().equals("c")) {
                            thesis_degree_grantor += scrubField(".", subField.getValue());
                            if (dc_description.length() > 0) {
                                dc_description += " -- " + thesis_degree_grantor;
                            }
                        }
                    }
                }

                // dc.description.abstract
                if (df.getTag().equals("520")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        dc_description_abstract += subField.getValue();
                    }
                }

                // dc.subject.lcsh and dc.subject
                if (df.getTag().equals("600") || df.getTag().equals("610") || df.getTag().equals("611") || df.getTag().equals("630") || df.getTag().equals("650") || df.getTag().equals("651")) {
                    Subfield[] subFields = df.getSubfield();
                    String lcsh = "";
                    for (Subfield subField : subFields) {
                        if (df.getInd2().equals("4")) {
                            dc_subject += scrubField(".", subField.getValue());
                        }
                        if (df.getInd2().equals("0")) {
                            if (lcsh.length() > 0) {
                                lcsh += " -- " + subField.getValue();
                            } else {
                                lcsh += subField.getValue();
                            }
                        } else {
                            if (subField.getCode().equals("x") || subField.getCode().equals("z")) {
                                if (lcsh.length() > 0) {
                                    lcsh += " -- " + subField.getValue();
                                } else {
                                    lcsh += subField.getValue();
                                }

                            }
                        }
                    }
                    if (!"".equals(lcsh)) {
                        dc_subject_lcsh.add(lcsh);
                    }
                }

                // dc.subject
                if (df.getTag().equals("653")) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (dc_subject.length() > 0) {
                            dc_subject += ", " + subField.getValue();
                        } else {
                            dc_subject += subField.getValue();
                        }

                    }
                }

                // The 700 field can contain committe chairs and committee
                // members
                if (df.getTag().equals("700")) {

                    Subfield[] subFields = df.getSubfield();
                    String temp;

                    for (Subfield prospectiveAdvisorDescriptionSubField : subFields) {
                        if (prospectiveAdvisorDescriptionSubField.getCode().equals("e")) {
                            String advisorDescription = prospectiveAdvisorDescriptionSubField.getValue();
                            for (Subfield prospectiveAdvisorNameSubField : subFields) {
                                // advisors (chair)
                                if (prospectiveAdvisorNameSubField.getCode().equals("a") && advisorDescription.contains("supervisor")) {
                                    temp = scrubField(".", prospectiveAdvisorNameSubField.getValue());

                                    temp = scrubField(",", temp);

                                    if (temp.length() > 0) {
                                        dc_contributor_advisor += temp;
                                    }
                                }
                                // advisors (member)
                                else if (prospectiveAdvisorNameSubField.getCode().equals("a") && advisorDescription.contains("member")) {
                                    temp = scrubField(".", prospectiveAdvisorNameSubField.getValue());

                                    temp = scrubField(",", temp);

                                    if (temp.length() > 0) {
                                        dc_contributor_committeeMember.add(temp);
                                    }
                                }

                            }

                        }
                    }
                }

                // handle uri
                if (df.getTag().equals("856") && dc_identifier_uri.length() == 0) {
                    Subfield[] subFields = df.getSubfield();
                    for (Subfield subField : subFields) {
                        if (subField.getCode().equals("u")) {
                            dc_identifier_uri = subField.getValue();
                        }
                    }
                }

            }

        }

    }

    public String getIdentifierUri() {
        return dc_identifier_uri;
    }

    public void setIdentifierUri(String handleUri) {
        dc_identifier_uri = handleUri;
    }

    public String getAdvisor() {
        return dc_contributor_advisor;
    }

    public void setAdvisor(String advisor) {
        dc_contributor_advisor = advisor;
    }

    public List<String> getCommitteMembers() {
        return dc_contributor_committeeMember;
    }

    public void setCommitteeMembers(List<String> committeeMembers) {
        this.dc_contributor_committeeMember = committeeMembers;
    }

    public String getCreator() {
        return dc_creator;
    }

    public void setCreator(String dc_creator) {
        this.dc_creator = dc_creator;
    }

    public String getTitle() {
        return dc_title;
    }

    public void setTitle(String dc_title) {
        this.dc_title = dc_title;
    }

    public String getDateCreated() {
        return dc_date_created;
    }

    public void setDateCreated(String dc_date_created) {
        this.dc_date_created = dc_date_created;
    }

    public String getDateIssued() {
        return dc_date_issued;
    }

    public void setDateIssued(String dc_date_issued) {
        this.dc_date_issued = dc_date_issued;
    }

    public List<String> getSubjectIcsh() {
        return dc_subject_lcsh;
    }

    public void setSubjectIcsh(List<String> dc_subject_lcsh) {
        this.dc_subject_lcsh = dc_subject_lcsh;
    }

    public String getSubject() {
        return dc_subject;
    }

    public void setSubject(String dc_subject) {
        this.dc_subject = dc_subject;
    }

    public String getDescription() {
        return dc_description;
    }

    public void setDescription(String dc_description) {
        this.dc_description = dc_description;
    }

    public String getDescriptionAbstract() {
        return dc_description_abstract;
    }

    public void setDescriptionAbstract(String dc_description_abstract) {
        this.dc_description_abstract = dc_description_abstract;
    }

    public String getDegreeGrantor() {
        return thesis_degree_grantor;
    }

    public void setDegreeGrantor(String thesis_degree_grantor) {
        this.thesis_degree_grantor = thesis_degree_grantor;
    }

    /**
     * Removes the scrubber string from the end of the scrubbable string. Used
     * to clean up trailing puncutation, etc. coming off the MARC values
     * 
     * @param scrubber
     *            the trailing string to scrub
     * @param scrubbable
     *            the string that needs a scrubbing
     * @return
     *            the nicely scrubbed resulting string
     */
    private String scrubField(String scrubber, String scrubbable) {
        scrubbable = scrubbable.trim();
        if (scrubbable.endsWith(scrubber)) {
            return rightTrim(scrubber.length(), scrubbable);
        }
        return sanatize(scrubbable);
    }

    private String sanatize(String sanitizable) {
        return sanitizable.replaceAll("\"", "");
    }

    private String rightTrim(int length, String trimmable) {
        return trimmable.substring(0, trimmable.length() - length);
    }

}
