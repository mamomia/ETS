package com.umt.ameer.ets.appdata;

/**
 * Created by Ameer on 3/12/2016.
 */
public final class Constants {
    private static String BASE_URL = "http://192.168.2.119:80/zimbra/index.php/webservice/";
    //    private static String BASE_URL = "http://192.168.8.101/ETS/index.php/webservice/";
    public static String LOGIN_URL = BASE_URL + "login";
    public static String GET_ALL_ORDERS_URL = BASE_URL + "get_all_orders";
    public static String GET_ORDERED_FORM_URL = BASE_URL + "get_ordered_form";
    public static String GET_PRODUCT_DETAIL_URL = BASE_URL + "get_product_detail";
    public static String GET_COMPANY_NAME_URL = BASE_URL + "get_company_name";
    public static String GET_PRODUCT_NAME_URL = BASE_URL + "get_product_name";
    public static String GET_STATUS_URL = BASE_URL + "change_status";
    public static String GET_ALL_PRODUCTS_URL = BASE_URL + "get_all_products";
    public static String SEND_BATTERY_NOTIFICATION_URL = BASE_URL + "get_send_notifications";
    public static String SEND_LOCATION_UPDATE_URL = BASE_URL + "get_send_location_update";
    public static String GET_UPDATE_PICTURE_URL = BASE_URL + "get_send_location_update";

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";

    //In app shared prefrences keys contants
    public static final String EMP_ID_KEY = "user_id";
    public static final String EMP_NAME_KEY = "user_name";
    public static final String EMP_EMAIL_KEY = "user_email";
    public static final String EMP_PHONE_KEY = "user_phone";
    public static final String EMP_JOIN_DATE_KEY = "user_join_date";
    public static final String EMP_STATUS_KEY = "user_status";
    public static final String EMP_STATUS_BREAK_CONTENT_KEY = "user_break_content";
    public static final String EMP_SUPERIOR_ID_KEY = "user_superior_id";
    public static final String EMP_RADIUS_KEY = "user_radius";
    public static final String EMP_RADIUS_CENTER_KEY = "user_radius_center";
    public static final String EMP_DP_KEY = "user_dp";

    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }
}