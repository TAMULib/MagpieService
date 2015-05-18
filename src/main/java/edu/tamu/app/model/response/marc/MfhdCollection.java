package edu.tamu.app.model.response.marc;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class MfhdCollection.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class MfhdCollection implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _mfhdRecord.
     */
    private MfhdRecord _mfhdRecord;


      //----------------/
     //- Constructors -/
    //----------------/

    public MfhdCollection() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'mfhdRecord'.
     * 
     * @return the value of field 'MfhdRecord'.
     */
    public MfhdRecord getMfhdRecord(
    ) {
        return this._mfhdRecord;
    }

    /**
     * Sets the value of field 'mfhdRecord'.
     * 
     * @param mfhdRecord the value of field 'mfhdRecord'.
     */
    public void setMfhdRecord(
            final MfhdRecord mfhdRecord) {
        this._mfhdRecord = mfhdRecord;
    }

}
