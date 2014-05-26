package jp.co.zensho.android.sukiya.bean;

import java.util.ArrayList;
import java.util.List;

public class GroupProduct {
    private String productId;
    private String productName;
    private String productImage;
    private String unit;
    private Double total;
    private Double current;
    private List<ProductInfo> productList;
    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }
    /**
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }
    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }
    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }
    /**
     * @return the productImage
     */
    public String getProductImage() {
        return productImage;
    }
    /**
     * @param productImage the productImage to set
     */
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }
    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    /**
     * @return the total
     */
    public Double getTotal() {
        return total;
    }
    /**
     * @param total the total to set
     */
    public void setTotal(Double total) {
        this.total = total;
    }
    /**
     * @return the current
     */
    public Double getCurrent() {
        return current;
    }
    /**
     * @param current the current to set
     */
    public void setCurrent(Double current) {
        this.current = current;
    }
    /**
     * @return the productList
     */
    public List<ProductInfo> getProductList() {
        return productList;
    }
    /**
     * @param productList the productList to set
     */
    public void setProductList(List<ProductInfo> productList) {
        this.productList = productList;
    }
    
    public void addProduct(ProductInfo p) {
        if (this.productList == null) {
            this.productList = new ArrayList<ProductInfo>();
        }
        this.productList.add(p);
    }
}
