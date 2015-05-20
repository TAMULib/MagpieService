package edu.tamu.app.model.response.marc;

/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.1</a>, using an XML
 * Schema.
 * $Id$
 */

/**
 * Class MarcRecord.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class MarcRecord implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _leader.
     */
    private Leader _leader;

    /**
     * Field _controlfieldList.
     */
    private java.util.Vector<Controlfield> _controlfieldList;

    /**
     * Field _datafieldList.
     */
    private java.util.Vector<Datafield> _datafieldList;


      //----------------/
     //- Constructors -/
    //----------------/

    public MarcRecord() {
        super();
        this._controlfieldList = new java.util.Vector<Controlfield>();
        this._datafieldList = new java.util.Vector<Datafield>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vControlfield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlfield(
            final Controlfield vControlfield)
    throws java.lang.IndexOutOfBoundsException {
        this._controlfieldList.addElement(vControlfield);
    }

    /**
     * 
     * 
     * @param index
     * @param vControlfield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addControlfield(
            final int index,
            final Controlfield vControlfield)
    throws java.lang.IndexOutOfBoundsException {
        this._controlfieldList.add(index, vControlfield);
    }

    /**
     * 
     * 
     * @param vDatafield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDatafield(
            final Datafield vDatafield)
    throws java.lang.IndexOutOfBoundsException {
        this._datafieldList.addElement(vDatafield);
    }

    /**
     * 
     * 
     * @param index
     * @param vDatafield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addDatafield(
            final int index,
            final Datafield vDatafield)
    throws java.lang.IndexOutOfBoundsException {
        this._datafieldList.add(index, vDatafield);
    }

    /**
     * Method enumerateControlfield.
     * 
     * @return an Enumeration over all Controlfield elements
     */
    public java.util.Enumeration<? extends Controlfield> enumerateControlfield(
    ) {
        return this._controlfieldList.elements();
    }

    /**
     * Method enumerateDatafield.
     * 
     * @return an Enumeration over all Datafield elements
     */
    public java.util.Enumeration<? extends Datafield> enumerateDatafield(
    ) {
        return this._datafieldList.elements();
    }

    /**
     * Method getControlfield.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the Controlfield at the given index
     */
    public Controlfield getControlfield(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._controlfieldList.size()) {
            throw new IndexOutOfBoundsException("getControlfield: Index value '" + index + "' not in range [0.." + (this._controlfieldList.size() - 1) + "]");
        }

        return (Controlfield) _controlfieldList.get(index);
    }

    /**
     * Method getControlfield.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public Controlfield[] getControlfield(
    ) {
        Controlfield[] array = new Controlfield[0];
        return (Controlfield[]) this._controlfieldList.toArray(array);
    }

    /**
     * Method getControlfieldCount.
     * 
     * @return the size of this collection
     */
    public int getControlfieldCount(
    ) {
        return this._controlfieldList.size();
    }

    /**
     * Method getDatafield.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the Datafield at the given index
     */
    public Datafield getDatafield(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._datafieldList.size()) {
            throw new IndexOutOfBoundsException("getDatafield: Index value '" + index + "' not in range [0.." + (this._datafieldList.size() - 1) + "]");
        }

        return (Datafield) _datafieldList.get(index);
    }

    /**
     * Method getDatafield.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public Datafield[] getDatafield(
    ) {
        Datafield[] array = new Datafield[0];
        return (Datafield[]) this._datafieldList.toArray(array);
    }

    /**
     * Method getDatafieldCount.
     * 
     * @return the size of this collection
     */
    public int getDatafieldCount(
    ) {
        return this._datafieldList.size();
    }

    /**
     * Returns the value of field 'leader'.
     * 
     * @return the value of field 'Leader'.
     */
    public Leader getLeader(
    ) {
        return this._leader;
    }

    /**
     */
    public void removeAllControlfield(
    ) {
        this._controlfieldList.clear();
    }

    /**
     */
    public void removeAllDatafield(
    ) {
        this._datafieldList.clear();
    }

    /**
     * Method removeControlfield.
     * 
     * @param vControlfield
     * @return true if the object was removed from the collection.
     */
    public boolean removeControlfield(
            final Controlfield vControlfield) {
        boolean removed = _controlfieldList.remove(vControlfield);
        return removed;
    }

    /**
     * Method removeControlfieldAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public Controlfield removeControlfieldAt(
            final int index) {
        java.lang.Object obj = this._controlfieldList.remove(index);
        return (Controlfield) obj;
    }

    /**
     * Method removeDatafield.
     * 
     * @param vDatafield
     * @return true if the object was removed from the collection.
     */
    public boolean removeDatafield(
            final Datafield vDatafield) {
        boolean removed = _datafieldList.remove(vDatafield);
        return removed;
    }

    /**
     * Method removeDatafieldAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public Datafield removeDatafieldAt(
            final int index) {
        java.lang.Object obj = this._datafieldList.remove(index);
        return (Datafield) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vControlfield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setControlfield(
            final int index,
            final Controlfield vControlfield)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._controlfieldList.size()) {
            throw new IndexOutOfBoundsException("setControlfield: Index value '" + index + "' not in range [0.." + (this._controlfieldList.size() - 1) + "]");
        }

        this._controlfieldList.set(index, vControlfield);
    }

    /**
     * 
     * 
     * @param vControlfieldArray
     */
    public void setControlfield(
            final Controlfield[] vControlfieldArray) {
        //-- copy array
        _controlfieldList.clear();

        for (int i = 0; i < vControlfieldArray.length; i++) {
                this._controlfieldList.add(vControlfieldArray[i]);
        }
    }

    /**
     * 
     * 
     * @param index
     * @param vDatafield
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setDatafield(
            final int index,
            final Datafield vDatafield)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._datafieldList.size()) {
            throw new IndexOutOfBoundsException("setDatafield: Index value '" + index + "' not in range [0.." + (this._datafieldList.size() - 1) + "]");
        }

        this._datafieldList.set(index, vDatafield);
    }

    /**
     * 
     * 
     * @param vDatafieldArray
     */
    public void setDatafield(
            final Datafield[] vDatafieldArray) {
        //-- copy array
        _datafieldList.clear();

        for (int i = 0; i < vDatafieldArray.length; i++) {
                this._datafieldList.add(vDatafieldArray[i]);
        }
    }

    /**
     * Sets the value of field 'leader'.
     * 
     * @param leader the value of field 'leader'.
     */
    public void setLeader(
            final Leader leader) {
        this._leader = leader;
    }

}
