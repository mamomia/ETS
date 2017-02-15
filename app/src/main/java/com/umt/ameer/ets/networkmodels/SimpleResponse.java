package com.umt.ameer.ets.networkmodels;

/**
 * Created by Mushi on 2/3/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimpleResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("IsFirstTimeIn")
    @Expose
    private String IsFirstTimeIn;


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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getIsFirstTimeIn() {
        return IsFirstTimeIn;
    }

    public void setIsFirstTimeIn(String IsFirstTimeIn) {
        this.IsFirstTimeIn = IsFirstTimeIn;
    }

}
