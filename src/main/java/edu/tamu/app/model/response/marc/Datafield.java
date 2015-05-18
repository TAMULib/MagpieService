package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class Datafield.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Datafield implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ind2.
     */
	@XmlAttribute(name="ind2")
    private java.lang.String _ind2;

    /**
     * Field _ind1.
     */
	@XmlAttribute(name="ind1")
    private java.lang.String _ind1;

    /**
     * Field _tag.
     */
	@XmlAttribute(name="tag")
    private java.lang.String _tag;

    /**
     * Field _slim.
     */
	@XmlAttribute(name="slim")
    private java.lang.String _slim;

    /**
     * Field _subfield.
     */
    private java.util.Vector<Subfield> _subfieldList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Datafield() {
        super();
        this._subfieldList = new java.util.Vector<Subfield>();
    }

    /**
     * 
     * 
     * @param vControlfield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubfield(
            final Subfield vSubfield)
    throws java.lang.IndexOutOfBoundsException {
        this._subfieldList.addElement(vSubfield);
    }
    
    /**
     * 
     * 
     * @param index
     * @param vDatafield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addSubfield(
            final int index,
            final Subfield vSubfield)
    throws java.lang.IndexOutOfBoundsException {
        this._subfieldList.add(index, vSubfield);
    }
    
    
    /**
     * Method enumerateControlfield.
     * 
     * @return an Enumeration over all Controlfield elements
     */
    public java.util.Enumeration<? extends Subfield> enumerateSubfield(
    ) {
        return this._subfieldList.elements();
    }

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'ind1'.
     * 
     * @return the value of field 'Ind1'.
     */
    public java.lang.String getInd1(
    ) {
        return this._ind1;
    }

    /**
     * Returns the value of field 'ind2'.
     * 
     * @return the value of field 'Ind2'.
     */
    public java.lang.String getInd2(
    ) {
        return this._ind2;
    }

    /**
     * Returns the value of field 'slim'.
     * 
     * @return the value of field 'Slim'.
     */
    public java.lang.String getSlim(
    ) {
        return this._slim;
    }

    /**
     * Returns the value of field 'tag'.
     * 
     * @return the value of field 'Tag'.
     */
    public java.lang.String getTag(
    ) {
        return this._tag;
    }

    /**
     * Sets the value of field 'ind1'.
     * 
     * @param ind1 the value of field 'ind1'.
     */
    public void setInd1(
            final java.lang.String ind1) {
        this._ind1 = ind1;
    }

    /**
     * Sets the value of field 'ind2'.
     * 
     * @param ind2 the value of field 'ind2'.
     */
    public void setInd2(
            final java.lang.String ind2) {
        this._ind2 = ind2;
    }

    /**
     * Sets the value of field 'slim'.
     * 
     * @param slim the value of field 'slim'.
     */
    public void setSlim(
            final java.lang.String slim) {
        this._slim = slim;
    }

    /**
     * Sets the value of field 'tag'.
     * 
     * @param tag the value of field 'tag'.
     */
    public void setTag(
            final java.lang.String tag) {
        this._tag = tag;
    }
    
    /**
     * Method getControlfield.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the Controlfield at the given index
     */
    public Subfield getSubfield(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._subfieldList.size()) {
            throw new IndexOutOfBoundsException("getControlfield: Index value '" + index + "' not in range [0.." + (this._subfieldList.size() - 1) + "]");
        }

        return (Subfield) _subfieldList.get(index);
    }

    /**
     * Method getControlfield.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public Subfield[] getSubfield(
    ) {
    	Subfield[] array = new Subfield[0];
        return (Subfield[]) this._subfieldList.toArray(array);
    }

    /**
     * Method getControlfieldCount.
     * 
     * @return the size of this collection
     */
    public int getSubfieldCount(
    ) {
        return this._subfieldList.size();
    }
    
    /**
     * Method removeDatafield.
     * 
     * @param vDatafield
     * @return true if the object was removed from the collection.
     */
    public boolean removeSubfield(
            final Subfield vSubfield) {
        boolean removed = _subfieldList.remove(vSubfield);
        return removed;
    }

    /**
     * Method removeDatafieldAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public Subfield removeSubfieldAt(
            final int index) {
        java.lang.Object obj = this._subfieldList.remove(index);
        return (Subfield) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vControlfield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setSubfieldfield(
            final int index,
            final Subfield vSubfield)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._subfieldList.size()) {
            throw new IndexOutOfBoundsException("setControlfield: Index value '" + index + "' not in range [0.." + (this._subfieldList.size() - 1) + "]");
        }

        this._subfieldList.set(index, vSubfield);
    }

    /**
     * 
     * 
     * @param vControlfieldArray
     */
    public void setSubfield(
            final Subfield[] vvSubfieldArray) {
        //-- copy array
    	_subfieldList.clear();

        for (int i = 0; i < vvSubfieldArray.length; i++) {
                this._subfieldList.add(vvSubfieldArray[i]);
        }
    }
    
}
