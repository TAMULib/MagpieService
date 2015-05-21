package edu.tamu.app.model.response.marc;

public class FlatMARC {
	
	private String dc_creator = "";
	private String dc_title = "";
	private String dc_date_created = "";
	private String dc_date_issued = "";
	private String dc_subject_lcsh = "";
	private String dc_subject = "";
	private String dc_description = "";
	private String dc_description_abstract = "";
	private String thesis_degree_grantor = "";
	
	public FlatMARC(VoyagerServiceData voyagerServiceData) {
		
		if(voyagerServiceData.getServiceData() != null) {
			
			Datafield[] dataField = voyagerServiceData.getServiceData().getHoldingsRecord().getBibRecord().getMarcRecord().getDatafield();
			
			for(Datafield df : dataField) {
				
				// dc.dc_creator
				if(df.getTag().equals("100")) {
					Subfield[] subFields = df.getSubfield();
					if(subFields.length > 0) {
						dc_creator = subFields[0].getValue();
					}
				}
				
				// dc.dc_title
				if(df.getTag().equals("245")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						if(subField.getCode().equals("a") || subField.getCode().equals("b")) {
							dc_title += subField.getValue();
						}
					}
				}
				
				// dc.date.created and dc.date.issued
				if(df.getTag().equals("260")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						if(subField.getCode().equals("c")) {
							dc_date_issued = dc_date_created = subField.getValue();
						}
					}
				}
				
				// dc.date.created and dc.date.issued
				if(df.getTag().equals("264")) {
					if(dc_date_issued.equals("")) {
						Subfield[] subFields = df.getSubfield();
						for(Subfield subField : subFields) {
							if(df.getInd2().equals("0") || df.getInd2().equals("1")) {
								dc_date_issued = dc_date_created = subField.getValue();
							}
						}					
					}
				}
				
				// dc.dc_description
				if(df.getTag().equals("300")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						if(subField.getCode().equals("a") || subField.getCode().equals("b")) {
							dc_description += subField.getValue();
						}
					}
				}
				
				// thesis.degree.grantor
				if(df.getTag().equals("502")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						if(subField.getCode().equals("c")) {
							thesis_degree_grantor += subField.getValue();
						}
					}
				}
				
				// thesis.degree.department
				if(df.getTag().equals("520")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						dc_description_abstract += subField.getValue();
					}
				}
				
				// dc.dc_subject.lcsh and dc.dc_subject
				if(df.getTag().equals("600") || df.getTag().equals("610") || df.getTag().equals("611") || df.getTag().equals("630") || df.getTag().equals("650")) {				
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {					
						if(df.getInd2().equals("4")) {
							dc_subject += subField.getValue();
						}
						else {
							if(subField.getCode().equals("a")) {
								if(dc_subject_lcsh.equals("")) {
									dc_subject_lcsh += subField.getValue();
								}
								else {
									dc_subject_lcsh += ", " + subField.getValue();
								}
							}
						}					
						if(subField.getCode().equals("x")) {
							dc_subject_lcsh += " -- " + subField.getValue();
						}					
						if(subField.getCode().equals("z")) {
							dc_subject_lcsh += " -- " + subField.getValue();
						}					
						if(subField.getCode().equals("z")) {
							dc_subject_lcsh += " -- " + subField.getValue();
						}					
					}
				}
				
				// dc.dc_subject
				if(df.getTag().equals("653")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						dc_subject += subField.getValue();
					}
				}
				
			}
		
		}
		
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


	public String getSubjectIcsh() {
		return dc_subject_lcsh;
	}


	public void setSubjectIcsh(String dc_subject_lcsh) {
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
	
	

}