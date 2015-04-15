package edu.tamu.app.model;

public interface MetadataLabel {

	public String getLabel();

	public void setLabel(String label);

	public String getGloss();

	public void setGloss(String gloss);

	public boolean isRepeatable();

	public void setRepeatable(boolean isRepeatable);

	public InputType getInputType();

	public void setInputType(InputType inputType);

}
