package edu.tamu.app.model.response.marc;

public class FlatMARC {
	
	private String creator = "";
	private String title = "";
	private String dateCreated = "";
	private String dateIssued = "";
	private String subjectIcsh = "";
	private String subject = "";
	private String description = "";
	private String descriptionAbstract = "";
	private String degreeGrantor = "";
	
	
	public FlatMARC(VoyagerServiceData voyagerServiceData) {
		Datafield[] dataField = voyagerServiceData.getServiceData().getHoldingsRecord().getBibRecord().getMarcRecord().getDatafield();
		
		for(Datafield df : dataField) {
			
			// dc.creator
			if(df.getTag().equals("100")) {
				Subfield[] subFields = df.getSubfield();
				if(subFields.length > 0) {
					creator = subFields[0].getValue();
				}
			}
			
			// dc.title
			if(df.getTag().equals("245")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					if(subField.getCode().equals("a") || subField.getCode().equals("b")) {
						title += subField.getValue();
					}
				}
			}
			
			// dc.date.created and dc.date.issued
			if(df.getTag().equals("260")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					if(subField.getCode().equals("c")) {
						dateIssued = dateCreated = subField.getValue();
					}
				}
			}
			
			// dc.date.created and dc.date.issued
			if(df.getTag().equals("264")) {
				if(dateIssued.equals("")) {
					Subfield[] subFields = df.getSubfield();
					for(Subfield subField : subFields) {
						if(df.getInd2().equals("0") || df.getInd2().equals("1")) {
							dateIssued = dateCreated = subField.getValue();
						}
					}					
				}
			}
			
			// dc.description
			if(df.getTag().equals("300")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					if(subField.getCode().equals("a") || subField.getCode().equals("b")) {
						description += subField.getValue();
					}
				}
			}
			
			// thesis.degree.grantor
			if(df.getTag().equals("502")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					if(subField.getCode().equals("c")) {
						degreeGrantor += subField.getValue();
					}
				}
			}
			
			// thesis.degree.department
			if(df.getTag().equals("520")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					descriptionAbstract += subField.getValue();
				}
			}
			
			// dc.subject.lcsh and dc.subject
			if(df.getTag().equals("600") || df.getTag().equals("610") || df.getTag().equals("611") || df.getTag().equals("630") || df.getTag().equals("650")) {				
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {					
					if(df.getInd2().equals("4")) {
						subject += subField.getValue();
					}
					else {
						if(subField.getCode().equals("a")) {
							if(subjectIcsh.equals("")) {
								subjectIcsh += subField.getValue();
							}
							else {
								subjectIcsh += ", " + subField.getValue();
							}
						}
					}					
					if(subField.getCode().equals("x")) {
						subjectIcsh += " -- " + subField.getValue();
					}					
					if(subField.getCode().equals("z")) {
						subjectIcsh += " -- " + subField.getValue();
					}					
					if(subField.getCode().equals("z")) {
						subjectIcsh += " -- " + subField.getValue();
					}					
				}
			}
			
			// dc.subject
			if(df.getTag().equals("653")) {
				Subfield[] subFields = df.getSubfield();
				for(Subfield subField : subFields) {
					subject += subField.getValue();
				}
			}
			
		}
		
	}


	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDateCreated() {
		return dateCreated;
	}


	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}


	public String getDateIssued() {
		return dateIssued;
	}


	public void setDateIssued(String dateIssued) {
		this.dateIssued = dateIssued;
	}


	public String getSubjectIcsh() {
		return subjectIcsh;
	}


	public void setSubjectIcsh(String subjectIcsh) {
		this.subjectIcsh = subjectIcsh;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDescriptionAbstract() {
		return descriptionAbstract;
	}


	public void setDescriptionAbstract(String descriptionAbstract) {
		this.descriptionAbstract = descriptionAbstract;
	}


	public String getDegreeGrantor() {
		return degreeGrantor;
	}


	public void setDegreeGrantor(String degreeGrantor) {
		this.degreeGrantor = degreeGrantor;
	}
	
	

}
