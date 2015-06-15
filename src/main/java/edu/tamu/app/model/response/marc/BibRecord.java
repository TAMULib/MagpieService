package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class BibRecord.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class BibRecord implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bibId.
     */
	@XmlAttribute(name="bibId")
    private java.lang.String _bibId;

    /**
     * Field _dpsFlag.
     */
	@XmlAttribute(name="dpsFlag")
    private java.lang.String _dpsFlag;

    /**
     * Field _marcRecord.
     */
    private MarcRecord _marcRecord;

    /**
     * Field _bibDataList.
     */
    private java.util.Vector<BibData> _bibDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public BibRecord() {
        super();
        this._bibDataList = new java.util.Vector<BibData>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vBibData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBibData(
            final BibData vBibData)
    throws java.lang.IndexOutOfBoundsException {
        this._bibDataList.addElement(vBibData);
    }

    /**
     * 
     * 
     * @param index
     * @param vBibData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBibData(
            final int index,
            final BibData vBibData)
    throws java.lang.IndexOutOfBoundsException {
        this._bibDataList.add(index, vBibData);
    }

    /**
     * Method enumerateBibData.
     * 
     * @return an Enumeration over all BibData elements
     */
    public java.util.Enumeration<? extends BibData> enumerateBibData(
    ) {
        return this._bibDataList.elements();
    }

    /**
     * Method getBibData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the BibData at the given index
     */
    public BibData getBibData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._bibDataList.size()) {
            throw new IndexOutOfBoundsException("getBibData: Index value '" + index + "' not in range [0.." + (this._bibDataList.size() - 1) + "]");
        }

        return (BibData) _bibDataList.get(index);
    }

    /**
     * Method getBibData.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public BibData[] getBibData(
    ) {
        BibData[] array = new BibData[0];
        return (BibData[]) this._bibDataList.toArray(array);
    }

    /**
     * Method getBibDataCount.
     * 
     * @return the size of this collection
     */
    public int getBibDataCount(
    ) {
        return this._bibDataList.size();
    }

    /**
     * Returns the value of field 'bibId'.
     * 
     * @return the value of field 'BibId'.
     */
    public java.lang.String getBibId(
    ) {
        return this._bibId;
    }

    /**
     * Returns the value of field 'dpsFlag'.
     * 
     * @return the value of field 'DpsFlag'.
     */
    public java.lang.String getDpsFlag(
    ) {
        return this._dpsFlag;
    }

    /**
     * Returns the value of field 'marcRecord'.
     * 
     * @return the value of field 'MarcRecord'.
     */
    public MarcRecord getMarcRecord(
    ) {
        return this._marcRecord;
    }

    /**
     */
    public void removeAllBibData(
    ) {
        this._bibDataList.clear();
    }

    /**
     * Method removeBibData.
     * 
     * @param vBibData
     * @return true if the object was removed from the collection.
     */
    public boolean removeBibData(
            final BibData vBibData) {
        boolean removed = _bibDataList.remove(vBibData);
        return removed;
    }

    /**
     * Method removeBibDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public BibData removeBibDataAt(
            final int index) {
        java.lang.Object obj = this._bibDataList.remove(index);
        return (BibData) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vBibData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setBibData(
            final int index,
            final BibData vBibData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._bibDataList.size()) {
            throw new IndexOutOfBoundsException("setBibData: Index value '" + index + "' not in range [0.." + (this._bibDataList.size() - 1) + "]");
        }

        this._bibDataList.set(index, vBibData);
    }

    /**
     * 
     * 
     * @param vBibDataArray
     */
    public void setBibData(
            final BibData[] vBibDataArray) {
        //-- copy array
        _bibDataList.clear();

        for (int i = 0; i < vBibDataArray.length; i++) {
                this._bibDataList.add(vBibDataArray[i]);
        }
    }

    /**
     * Sets the value of field 'bibId'.
     * 
     * @param bibId the value of field 'bibId'.
     */
    public void setBibId(
            final java.lang.String bibId) {
        this._bibId = bibId;
    }

    /**
     * Sets the value of field 'dpsFlag'.
     * 
     * @param dpsFlag the value of field 'dpsFlag'.
     */
    public void setDpsFlag(
            final java.lang.String dpsFlag) {
        this._dpsFlag = dpsFlag;
    }

    /**
     * Sets the value of field 'marcRecord'.
     * 
     * @param marcRecord the value of field 'marcRecord'.
     */
    public void setMarcRecord(
            final MarcRecord marcRecord) {
        this._marcRecord = marcRecord;
    }

}
