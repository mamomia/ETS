package com.umt.ameer.ets.networkmodels;

/**
 * Created by Mushi on 2/3/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductsResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("companies_count")
    @Expose
    private Integer companiesCount;
    @SerializedName("product_info")
    @Expose
    private List<ProductInfo> productInfo = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCompaniesCount() {
        return companiesCount;
    }

    public void setCompaniesCount(Integer companiesCount) {
        this.companiesCount = companiesCount;
    }

    public List<ProductInfo> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(List<ProductInfo> productInfo) {
        this.productInfo = productInfo;
    }

    public class Product {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("product_name")
        @Expose
        private String productName;
        @SerializedName("product_size")
        @Expose
        private String productSize;
        @SerializedName("product_price")
        @Expose
        private String productPrice;
        @SerializedName("company_id")
        @Expose
        private String companyId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductSize() {
            return productSize;
        }

        public void setProductSize(String productSize) {
            this.productSize = productSize;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

    }

    public class ProductInfo {

        @SerializedName("company_title")
        @Expose
        private String companyTitle;
        @SerializedName("products")
        @Expose
        private List<Product> products = new ArrayList<>();

        public String getCompanyTitle() {
            return companyTitle;
        }

        public void setCompanyTitle(String companyTitle) {
            this.companyTitle = companyTitle;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

}