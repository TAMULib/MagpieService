package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class Leader.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
@XmlRootElement(name = "leader")
public class Leader implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
	@XmlValue
    protected java.lang.String value;

    /**
     * Field _slim.
     */
    @XmlAttribute(name="slim")
    private java.lang.String _slim;


      //----------------/
     //- Constructors -/
    //----------------/

    public Leader() {
        super();
    }

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'content'. The field 'content'
     * has the following description: internal content storage
     * 
     * @return the value of field 'Content'.
     */
    public java.lang.String getValue(
    ) {
        return this.value;
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
     * Sets the value of field 'content'. The field 'content' has
     * the following description: internal content storage
     * 
     * @param content the value of field 'content'.
     */
    public void setValue(
            final java.lang.String value) {
        this.value = value;
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

}
