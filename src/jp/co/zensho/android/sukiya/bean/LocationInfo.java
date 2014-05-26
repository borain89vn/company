package jp.co.zensho.android.sukiya.bean;

public class LocationInfo {
    private String code;
    private String name;
    private String imagePath;
    private int inputedProduct;
    private int totalProduct;

    public int getInputedProduct() {
		return inputedProduct;
	}
	public void setInputedProduct(int inputedProduct) {
		this.inputedProduct = inputedProduct;
	}
	public int getTotalProduct() {
		return totalProduct;
	}
	public void setTotalProduct(int totalProduct) {
		this.totalProduct = totalProduct;
	}
	/**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }
    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
