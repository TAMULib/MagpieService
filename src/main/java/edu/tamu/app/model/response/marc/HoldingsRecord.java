package edu.tamu.app.model.response.marc;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class HoldingsRecord.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class HoldingsRecord implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bibRecord.
     */
    private BibRecord _bibRecord;

    /**
     * Field _mfhdCollection.
     */
    private MfhdCollection _mfhdCollection;


      //----------------/
     //- Constructors -/
    //----------------/

    public HoldingsRecord() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'bibRecord'.
     * 
     * @return the value of field 'BibRecord'.
     */
    public BibRecord getBibRecord(
    ) {
        return this._bibRecord;
    }

    /**
     * Returns the value of field 'mfhdCollection'.
     * 
     * @return the value of field 'MfhdCollection'.
     */
    public MfhdCollection getMfhdCollection(
    ) {
        return this._mfhdCollection;
    }

    /**
     * Sets the value of field 'bibRecord'.
     * 
     * @param bibRecord the value of field 'bibRecord'.
     */
    public void setBibRecord(
            final BibRecord bibRecord) {
        this._bibRecord = bibRecord;
    }

    /**
     * Sets the value of field 'mfhdCollection'.
     * 
     * @param mfhdCollection the value of field 'mfhdCollection'.
     */
    public void setMfhdCollection(
            final MfhdCollection mfhdCollection) {
        this._mfhdCollection = mfhdCollection;
    }

}
