package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class MfhdRecord.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class MfhdRecord implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bibId.
     */
	@XmlAttribute(name="bibId")
    private java.lang.String _bibId;

    /**
     * Field _mfhdId.
     */
	@XmlAttribute(name="mfhdId")
    private java.lang.String _mfhdId;

    /**
     * Field _mfhd.
     */
	@XmlAttribute(name="mfhd")
    private java.lang.String _mfhd;

    /**
     * Field _marcRecord.
     */
    private MarcRecord _marcRecord;

    /**
     * Field _mfhdDataList.
     */
    private java.util.Vector<MfhdData> _mfhdDataList;

    /**
     * Field _itemCollection.
     */
    private ItemCollection _itemCollection;


      //----------------/
     //- Constructors -/
    //----------------/

    public MfhdRecord() {
        super();
        this._mfhdDataList = new java.util.Vector<MfhdData>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vMfhdData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMfhdData(
            final MfhdData vMfhdData)
    throws java.lang.IndexOutOfBoundsException {
        this._mfhdDataList.addElement(vMfhdData);
    }

    /**
     * 
     * 
     * @param index
     * @param vMfhdData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addMfhdData(
            final int index,
            final MfhdData vMfhdData)
    throws java.lang.IndexOutOfBoundsException {
        this._mfhdDataList.add(index, vMfhdData);
    }

    /**
     * Method enumerateMfhdData.
     * 
     * @return an Enumeration over all MfhdData elements
     */
    public java.util.Enumeration<? extends MfhdData> enumerateMfhdData(
    ) {
        return this._mfhdDataList.elements();
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
     * Returns the value of field 'itemCollection'.
     * 
     * @return the value of field 'ItemCollection'.
     */
    public ItemCollection getItemCollection(
    ) {
        return this._itemCollection;
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
     * Returns the value of field 'mfhd'.
     * 
     * @return the value of field 'Mfhd'.
     */
    public java.lang.String getMfhd(
    ) {
        return this._mfhd;
    }

    /**
     * Method getMfhdData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the MfhdData at the given index
     */
    public MfhdData getMfhdData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mfhdDataList.size()) {
            throw new IndexOutOfBoundsException("getMfhdData: Index value '" + index + "' not in range [0.." + (this._mfhdDataList.size() - 1) + "]");
        }

        return (MfhdData) _mfhdDataList.get(index);
    }

    /**
     * Method getMfhdData.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public MfhdData[] getMfhdData(
    ) {
        MfhdData[] array = new MfhdData[0];
        return (MfhdData[]) this._mfhdDataList.toArray(array);
    }

    /**
     * Method getMfhdDataCount.
     * 
     * @return the size of this collection
     */
    public int getMfhdDataCount(
    ) {
        return this._mfhdDataList.size();
    }

    /**
     * Returns the value of field 'mfhdId'.
     * 
     * @return the value of field 'MfhdId'.
     */
    public java.lang.String getMfhdId(
    ) {
        return this._mfhdId;
    }

    /**
     */
    public void removeAllMfhdData(
    ) {
        this._mfhdDataList.clear();
    }

    /**
     * Method removeMfhdData.
     * 
     * @param vMfhdData
     * @return true if the object was removed from the collection.
     */
    public boolean removeMfhdData(
            final MfhdData vMfhdData) {
        boolean removed = _mfhdDataList.remove(vMfhdData);
        return removed;
    }

    /**
     * Method removeMfhdDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public MfhdData removeMfhdDataAt(
            final int index) {
        java.lang.Object obj = this._mfhdDataList.remove(index);
        return (MfhdData) obj;
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
     * Sets the value of field 'itemCollection'.
     * 
     * @param itemCollection the value of field 'itemCollection'.
     */
    public void setItemCollection(
            final ItemCollection itemCollection) {
        this._itemCollection = itemCollection;
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

    /**
     * Sets the value of field 'mfhd'.
     * 
     * @param mfhd the value of field 'mfhd'.
     */
    public void setMfhd(
            final java.lang.String mfhd) {
        this._mfhd = mfhd;
    }

    /**
     * 
     * 
     * @param index
     * @param vMfhdData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setMfhdData(
            final int index,
            final MfhdData vMfhdData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._mfhdDataList.size()) {
            throw new IndexOutOfBoundsException("setMfhdData: Index value '" + index + "' not in range [0.." + (this._mfhdDataList.size() - 1) + "]");
        }

        this._mfhdDataList.set(index, vMfhdData);
    }

    /**
     * 
     * 
     * @param vMfhdDataArray
     */
    public void setMfhdData(
            final MfhdData[] vMfhdDataArray) {
        //-- copy array
        _mfhdDataList.clear();

        for (int i = 0; i < vMfhdDataArray.length; i++) {
                this._mfhdDataList.add(vMfhdDataArray[i]);
        }
    }

    /**
     * Sets the value of field 'mfhdId'.
     * 
     * @param mfhdId the value of field 'mfhdId'.
     */
    public void setMfhdId(
            final java.lang.String mfhdId) {
        this._mfhdId = mfhdId;
    }

}
