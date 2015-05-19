package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class ServiceData.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ServiceData implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type.
     */
	@XmlAttribute(name="type")
    private java.lang.String _type;

    /**
     * Field _hol.
     */
	@XmlAttribute(name="hol")
    private java.lang.String _hol;

    /**
     * Field _xsi.
     */
	@XmlAttribute(name="xsi")
    private java.lang.String _xsi;

    /**
     * Field _holdingsRecord.
     */
    private HoldingsRecord _holdingsRecord;


      //----------------/
     //- Constructors -/
    //----------------/

    public ServiceData() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'hol'.
     * 
     * @return the value of field 'Hol'.
     */
    public java.lang.String getHol(
    ) {
        return this._hol;
    }

    /**
     * Returns the value of field 'holdingsRecord'.
     * 
     * @return the value of field 'HoldingsRecord'.
     */
    public HoldingsRecord getHoldingsRecord(
    ) {
        return this._holdingsRecord;
    }

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public java.lang.String getType(
    ) {
        return this._type;
    }

    /**
     * Returns the value of field 'xsi'.
     * 
     * @return the value of field 'Xsi'.
     */
    public java.lang.String getXsi(
    ) {
        return this._xsi;
    }

    /**
     * Sets the value of field 'hol'.
     * 
     * @param hol the value of field 'hol'.
     */
    public void setHol(
            final java.lang.String hol) {
        this._hol = hol;
    }

    /**
     * Sets the value of field 'holdingsRecord'.
     * 
     * @param holdingsRecord the value of field 'holdingsRecord'.
     */
    public void setHoldingsRecord(
            final HoldingsRecord holdingsRecord) {
        this._holdingsRecord = holdingsRecord;
    }

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(
            final java.lang.String type) {
        this._type = type;
    }

    /**
     * Sets the value of field 'xsi'.
     * 
     * @param xsi the value of field 'xsi'.
     */
    public void setXsi(
            final java.lang.String xsi) {
        this._xsi = xsi;
    }

}
