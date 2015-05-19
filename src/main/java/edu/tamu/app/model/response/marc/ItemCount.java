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
 * Class ItemCount.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
@XmlRootElement(name = "itemCount")
public class ItemCount implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
	@XmlValue
    protected java.lang.String value;

    /**
     * Field _item.
     */
    @XmlAttribute(name="item")
    private java.lang.String _item;


      //----------------/
     //- Constructors -/
    //----------------/

    public ItemCount() {
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
     * Returns the value of field 'item'.
     * 
     * @return the value of field 'Item'.
     */
    public java.lang.String getItem(
    ) {
        return this._item;
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
     * Sets the value of field 'item'.
     * 
     * @param item the value of field 'item'.
     */
    public void setItem(
            final java.lang.String item) {
        this._item = item;
    }

}
