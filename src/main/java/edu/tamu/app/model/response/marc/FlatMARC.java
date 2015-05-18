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
			
			if(df.getTag().equals("100")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					creator = subField[0].getValue();
				}
			}
			
			if(df.getTag().equals("245")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					title += subField[0].getValue();
				}
				if(subField.length > 1) {
					title += subField[1].getValue();
				}
			}
			
			if(df.getTag().equals("260")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 2) {
					dateIssued = dateCreated = subField[2].getValue();
				}
			}
			
			if(df.getTag().equals("264")) {
				if(dateIssued.equals("")) {
					Subfield[] subField = df.getSubfield();
					if(subField.length > 1) {
						if(df.getInd2().equals("0") || df.getInd2().equals("1")) {
							dateIssued = subField[1].getValue();
						}
					}
				}
			}
			
			if(df.getTag().equals("300")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					description += subField[0].getValue();
				}
				if(subField.length > 1) {
					description += subField[1].getValue();
				}
			}
			
			if(df.getTag().equals("502")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 2) {
					degreeGrantor += subField[2].getValue();
				}
			}
			
			if(df.getTag().equals("520")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					descriptionAbstract += subField[0].getValue();
				}
			}
			
			if(df.getTag().equals("600") || df.getTag().equals("610") || df.getTag().equals("611") || df.getTag().equals("630") || df.getTag().equals("650")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					
					if(df.getInd2().equals("4")) {
						subject += subField[0].getValue();
					}
					else {
						if(subField[0].getCode().equals("a")) {
							if(subjectIcsh.equals("")) {
								subjectIcsh += subField[0].getValue();
							}
							else {
								subjectIcsh += ", " + subField[0].getValue();
							}
						}
					}
					
				}
				
				if(subField.length > 1) {
					if(subField[1].getCode().equals("x")) {
						subjectIcsh += " -- " + subField[0].getValue();
					}
				}
				
			}
			
			if(df.getTag().equals("653")) {
				Subfield[] subField = df.getSubfield();
				if(subField.length > 0) {
					subject += subField[0].getValue();
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
