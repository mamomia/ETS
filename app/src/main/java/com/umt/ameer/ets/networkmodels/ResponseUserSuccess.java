package com.umt.ameer.ets.networkmodels;

import com.umt.ameer.ets.models.OrderInfo;
import com.umt.ameer.ets.models.UserInfo;

/**
 * Created by Ameer on 4/30/2016.
 */
public class ResponseUserSuccess {
    public String status;
    public String message;
    public UserInfo user_info;
    public OrderInfo order_info;
}
