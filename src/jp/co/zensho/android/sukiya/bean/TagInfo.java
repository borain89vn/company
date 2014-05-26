package jp.co.zensho.android.sukiya.bean;

public class TagInfo {
    public static final int LOCATION_TAG = 0;
    public static final int NUMBER_TAG = 1;
    public static final int DATE_TAG = 2;
    public static final int ADD_PRODUCT_TAG = 3;
    public static final int DEL_PRODUCT_TAG = 4;
    
    private int type;
    private int index1;
    private int index2;
    
    public TagInfo(int type, int index) {
        this.type = type;
        this.index1 = index;
        this.index2 = -1;
    }
    
    public TagInfo(int type, int index1, int index2) {
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the index1
     */
    public int getIndex1() {
        return index1;
    }

    /**
     * @param index1 the index1 to set
     */
    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    /**
     * @return the index2
     */
    public int getIndex2() {
        return index2;
    }

    /**
     * @param index2 the index2 to set
     */
    public void setIndex2(int index2) {
        this.index2 = index2;
    }
    
    
}
