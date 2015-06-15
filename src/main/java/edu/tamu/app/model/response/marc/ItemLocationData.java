package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class ItemLocationData.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ItemLocationData implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
	@XmlAttribute(name="name")
    private java.lang.String _name;

    /**
     * Field _nil.
     */
	@XmlAttribute(name="nil")
    private java.lang.String _nil;


      //----------------/
     //- Constructors -/
    //----------------/

    public ItemLocationData() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Returns the value of field 'nil'.
     * 
     * @return the value of field 'Nil'.
     */
    public java.lang.String getNil(
    ) {
        return this._nil;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * Sets the value of field 'nil'.
     * 
     * @param nil the value of field 'nil'.
     */
    public void setNil(
            final java.lang.String nil) {
        this._nil = nil;
    }

}
