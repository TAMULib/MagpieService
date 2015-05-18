package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class VoyagerServiceData.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
@XmlRootElement(name="voyagerServiceData")
public class VoyagerServiceData implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ser.
     */
	@XmlAttribute(name="ser")
    private java.lang.String _ser;

    /**
     * Field _serviceData.
     */
    private ServiceData _serviceData;


      //----------------/
     //- Constructors -/
    //----------------/

    public VoyagerServiceData() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'ser'.
     * 
     * @return the value of field 'Ser'.
     */
    public java.lang.String getSer(
    ) {
        return this._ser;
    }

    /**
     * Returns the value of field 'serviceData'.
     * 
     * @return the value of field 'ServiceData'.
     */
    public ServiceData getServiceData(
    ) {
        return this._serviceData;
    }

    /**
     * Sets the value of field 'ser'.
     * 
     * @param ser the value of field 'ser'.
     */
    public void setSer(
            final java.lang.String ser) {
        this._ser = ser;
    }

    /**
     * Sets the value of field 'serviceData'.
     * 
     * @param serviceData the value of field 'serviceData'.
     */
    public void setServiceData(
            final ServiceData serviceData) {
        this._serviceData = serviceData;
    }

}
