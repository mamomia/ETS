package com.umt.ameer.ets.rest;

import com.umt.ameer.ets.networkmodels.CompanyInfoResponse;
import com.umt.ameer.ets.networkmodels.OrderInfoResponse;
import com.umt.ameer.ets.networkmodels.OrdersResponse;
import com.umt.ameer.ets.networkmodels.ProductsInfoResponse;
import com.umt.ameer.ets.networkmodels.ProductsResponse;
import com.umt.ameer.ets.networkmodels.SimpleResponse;
import com.umt.ameer.ets.networkmodels.UserInfoResponse;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mushi on 8/29/2016.
 */
public interface ApiInterface {

    @GET("get_user_info/{id}")
    Call<UserInfoResponse> getUserInfo(@Query("id") String emp_id);

    @GET("login/{email}/{password}")
    Call<UserInfoResponse> loginRequest(@Query("email") String email, @Query("password") String password);

    @GET("change_status/{emp_id}/{sup_id}/{status}/{break_content}")
    Call<SimpleResponse> changeStatusRequest(@Query("emp_id") String emp_id,
                                             @Query("sup_id") String sup_id,
                                             @Query("status") String status,
                                             @Query("break_content") String break_content);

    @GET("get_send_location_update/{emp_id}/{emp_lat}/{emp_lng}")
    Call<SimpleResponse> updateLocationRequest(@Query("emp_id") String emp_id,
                                               @Query("emp_lat") String emp_lat,
                                               @Query("emp_lng") String emp_lng,
                                               @Query("sup_id") String sup_id,
                                               @Query("emp_radius_flag") String emp_radius_flag);

    @GET("get_send_notifications/{emp_id}/{sup_id}/{content}/{type}")
    Call<SimpleResponse> sendNotificationRequest(@Query("emp_id") String emp_id,
                                                 @Query("sup_id") String sup_id,
                                                 @Query("content") String content,
                                                 @Query("type") String type);

    @GET("get_all_orders/{emp_id}")
    Call<OrdersResponse> getOrdersRequest(@Query("emp_id") String emp_id);

    @GET("get_order_info/{emp_id}/{order_id}")
    Call<OrderInfoResponse> getOrderInfoRequest(@Query("emp_id") String emp_id,
                                                @Query("order_id") String order_id);

    @GET("get_all_products")
    Call<ProductsResponse> getProductsRequest();

    @GET("get_companies")
    Call<CompanyInfoResponse> getCompaniesRequest();

    @GET("get_company_products/{id}")
    Call<ProductsInfoResponse> getCompaniesProductsRequest(@Query("id") String id);

    @GET("add_new_order/{emp_id}/{sup_id}/{shop_name}/{price}/{products}")
    Call<SimpleResponse> addNewOrderRequest(@Query("emp_id") String emp_id,
                                            @Query("sup_id") String sup_id,
                                            @Query("shop_name") String shop_name,
                                            @Query("price") String price,
                                            @Query("products") JSONArray products);

}
