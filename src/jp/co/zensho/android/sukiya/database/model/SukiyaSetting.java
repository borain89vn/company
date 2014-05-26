package jp.co.zensho.android.sukiya.database.model;

public class SukiyaSetting {
    private int id;
    private String shopCode;
    private String shopName;
    private int downloadZipState;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the shopCode
     */
    public String getShopCode() {
        return shopCode;
    }
    /**
     * @param shopCode the shopCode to set
     */
    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
    /**
     * @return the shopName
     */
    public String getShopName() {
        return shopName;
    }
    /**
     * @param shopName the shopName to set
     */
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    /**
     * @return the downloadZipState
     */
    public int getDownloadZipState() {
        return downloadZipState;
    }
    /**
     * @param downloadZipState the downloadZipState to set
     */
    public void setDownloadZipState(int downloadZipState) {
        this.downloadZipState = downloadZipState;
    }
}
