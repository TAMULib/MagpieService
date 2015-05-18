package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class ItemLocation.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ItemLocation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _item.
     */
	@XmlAttribute(name="item")
    private java.lang.String _item;

    /**
     * Field _itemLocationDataList.
     */
    private java.util.Vector<ItemLocationData> _itemLocationDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ItemLocation() {
        super();
        this._itemLocationDataList = new java.util.Vector<ItemLocationData>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vItemLocationData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItemLocationData(
            final ItemLocationData vItemLocationData)
    throws java.lang.IndexOutOfBoundsException {
        this._itemLocationDataList.addElement(vItemLocationData);
    }

    /**
     * 
     * 
     * @param index
     * @param vItemLocationData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItemLocationData(
            final int index,
            final ItemLocationData vItemLocationData)
    throws java.lang.IndexOutOfBoundsException {
        this._itemLocationDataList.add(index, vItemLocationData);
    }

    /**
     * Method enumerateItemLocationData.
     * 
     * @return an Enumeration over all ItemLocationData elements
     */
    public java.util.Enumeration<? extends ItemLocationData> enumerateItemLocationData(
    ) {
        return this._itemLocationDataList.elements();
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
     * Method getItemLocationData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the ItemLocationData at the given index
     */
    public ItemLocationData getItemLocationData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemLocationDataList.size()) {
            throw new IndexOutOfBoundsException("getItemLocationData: Index value '" + index + "' not in range [0.." + (this._itemLocationDataList.size() - 1) + "]");
        }

        return (ItemLocationData) _itemLocationDataList.get(index);
    }

    /**
     * Method getItemLocationData.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public ItemLocationData[] getItemLocationData(
    ) {
        ItemLocationData[] array = new ItemLocationData[0];
        return (ItemLocationData[]) this._itemLocationDataList.toArray(array);
    }

    /**
     * Method getItemLocationDataCount.
     * 
     * @return the size of this collection
     */
    public int getItemLocationDataCount(
    ) {
        return this._itemLocationDataList.size();
    }

    /**
     */
    public void removeAllItemLocationData(
    ) {
        this._itemLocationDataList.clear();
    }

    /**
     * Method removeItemLocationData.
     * 
     * @param vItemLocationData
     * @return true if the object was removed from the collection.
     */
    public boolean removeItemLocationData(
            final ItemLocationData vItemLocationData) {
        boolean removed = _itemLocationDataList.remove(vItemLocationData);
        return removed;
    }

    /**
     * Method removeItemLocationDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public ItemLocationData removeItemLocationDataAt(
            final int index) {
        java.lang.Object obj = this._itemLocationDataList.remove(index);
        return (ItemLocationData) obj;
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

    /**
     * 
     * 
     * @param index
     * @param vItemLocationData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setItemLocationData(
            final int index,
            final ItemLocationData vItemLocationData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemLocationDataList.size()) {
            throw new IndexOutOfBoundsException("setItemLocationData: Index value '" + index + "' not in range [0.." + (this._itemLocationDataList.size() - 1) + "]");
        }

        this._itemLocationDataList.set(index, vItemLocationData);
    }

    /**
     * 
     * 
     * @param vItemLocationDataArray
     */
    public void setItemLocationData(
            final ItemLocationData[] vItemLocationDataArray) {
        //-- copy array
        _itemLocationDataList.clear();

        for (int i = 0; i < vItemLocationDataArray.length; i++) {
                this._itemLocationDataList.add(vItemLocationDataArray[i]);
        }
    }

}
