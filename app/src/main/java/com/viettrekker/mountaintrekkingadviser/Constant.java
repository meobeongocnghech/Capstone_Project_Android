package com.viettrekker.mountaintrekkingadviser;

/**
 * Including application constants
 *
 * @author LongNC
 * @since 25/07/2018
 */
public class Constant {
    // API URL
    // Change the domain or specific IP address (run on local server)
    // in order to call service properly
    public static final String BASE_URL_API = "http://192.168.43.135:8088/api/";

    // API PATH
    public static final String PATH_SIGNIN = "auth/signin";
    public static final String PATH_SIGNUP = "auth/signup";

    // Request parameters
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FIRSTNAME = "firstname";
    public static final String PARAM_LASTNAME = "lastname";
    public static final String PARAM_GENDER = "gender";
    public static final String PARAM_BIRTHDATE = "birthdate";
}
