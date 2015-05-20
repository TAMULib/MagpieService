package edu.tamu.app.model.response.marc;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class ItemCollection.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ItemCollection implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _itemCount.
     */
    private ItemCount _itemCount;

    /**
     * Field _itemRecord.
     */
    private ItemRecord _itemRecord;

    /**
     * Field _itemLocation.
     */
    private ItemLocation _itemLocation;


      //----------------/
     //- Constructors -/
    //----------------/

    public ItemCollection() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'itemCount'.
     * 
     * @return the value of field 'ItemCount'.
     */
    public ItemCount getItemCount(
    ) {
        return this._itemCount;
    }

    /**
     * Returns the value of field 'itemLocation'.
     * 
     * @return the value of field 'ItemLocation'.
     */
    public ItemLocation getItemLocation(
    ) {
        return this._itemLocation;
    }

    /**
     * Returns the value of field 'itemRecord'.
     * 
     * @return the value of field 'ItemRecord'.
     */
    public ItemRecord getItemRecord(
    ) {
        return this._itemRecord;
    }

    /**
     * Sets the value of field 'itemCount'.
     * 
     * @param itemCount the value of field 'itemCount'.
     */
    public void setItemCount(
            final ItemCount itemCount) {
        this._itemCount = itemCount;
    }

    /**
     * Sets the value of field 'itemLocation'.
     * 
     * @param itemLocation the value of field 'itemLocation'.
     */
    public void setItemLocation(
            final ItemLocation itemLocation) {
        this._itemLocation = itemLocation;
    }

    /**
     * Sets the value of field 'itemRecord'.
     * 
     * @param itemRecord the value of field 'itemRecord'.
     */
    public void setItemRecord(
            final ItemRecord itemRecord) {
        this._itemRecord = itemRecord;
    }

}
