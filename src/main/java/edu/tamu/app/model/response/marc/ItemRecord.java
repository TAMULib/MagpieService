package edu.tamu.app.model.response.marc;

import javax.xml.bind.annotation.XmlAttribute;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class ItemRecord.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ItemRecord implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _item.
     */
	@XmlAttribute(name="item")
    private java.lang.String _item;

    /**
     * Field _itemDataList.
     */
    private java.util.Vector<ItemData> _itemDataList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ItemRecord() {
        super();
        this._itemDataList = new java.util.Vector<ItemData>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vItemData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItemData(
            final ItemData vItemData)
    throws java.lang.IndexOutOfBoundsException {
        this._itemDataList.addElement(vItemData);
    }

    /**
     * 
     * 
     * @param index
     * @param vItemData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addItemData(
            final int index,
            final ItemData vItemData)
    throws java.lang.IndexOutOfBoundsException {
        this._itemDataList.add(index, vItemData);
    }

    /**
     * Method enumerateItemData.
     * 
     * @return an Enumeration over all ItemData elements
     */
    public java.util.Enumeration<? extends ItemData> enumerateItemData(
    ) {
        return this._itemDataList.elements();
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
     * Method getItemData.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the ItemData at the given index
     */
    public ItemData getItemData(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemDataList.size()) {
            throw new IndexOutOfBoundsException("getItemData: Index value '" + index + "' not in range [0.." + (this._itemDataList.size() - 1) + "]");
        }

        return (ItemData) _itemDataList.get(index);
    }

    /**
     * Method getItemData.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public ItemData[] getItemData(
    ) {
        ItemData[] array = new ItemData[0];
        return (ItemData[]) this._itemDataList.toArray(array);
    }

    /**
     * Method getItemDataCount.
     * 
     * @return the size of this collection
     */
    public int getItemDataCount(
    ) {
        return this._itemDataList.size();
    }

    /**
     */
    public void removeAllItemData(
    ) {
        this._itemDataList.clear();
    }

    /**
     * Method removeItemData.
     * 
     * @param vItemData
     * @return true if the object was removed from the collection.
     */
    public boolean removeItemData(
            final ItemData vItemData) {
        boolean removed = _itemDataList.remove(vItemData);
        return removed;
    }

    /**
     * Method removeItemDataAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public ItemData removeItemDataAt(
            final int index) {
        java.lang.Object obj = this._itemDataList.remove(index);
        return (ItemData) obj;
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
     * @param vItemData
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setItemData(
            final int index,
            final ItemData vItemData)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._itemDataList.size()) {
            throw new IndexOutOfBoundsException("setItemData: Index value '" + index + "' not in range [0.." + (this._itemDataList.size() - 1) + "]");
        }

        this._itemDataList.set(index, vItemData);
    }

    /**
     * 
     * 
     * @param vItemDataArray
     */
    public void setItemData(
            final ItemData[] vItemDataArray) {
        //-- copy array
        _itemDataList.clear();

        for (int i = 0; i < vItemDataArray.length; i++) {
                this._itemDataList.add(vItemDataArray[i]);
        }
    }

}
