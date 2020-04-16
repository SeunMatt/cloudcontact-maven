package com.smatt.cc.config;

public class Routes {

    public static final String HOME = "/";

    //log in routes
    public static final String GET_LOGIN_PAGE = "/login";
    public static final String DO_LOGIN = "/login";
    public static final String DO_AUTH = "/auth";
    public static final String LOGOUT = "/logout";

    //routes for performing crud on contact
    public static final String DASHBOARD = "/contacts/";
    public static final String DELETE = "/contact/:id"; //uses delete http method
    public static final String UPDATE = "/contact/:id"; //uses put http method; data is contained in req body
    public static final String NEW = "/contact/"; //uses post http method; data is contained in req body

    //routes for managing users / authentication
    public static final String GET_SIGN_UP = "/signup"; //uses get method
    public static final String DO_SIGN_UP = "/signup"; //uses post method
    public static final String NEW_USER = "/user"; //uses post method
    public static final String UPDATE_PWD = "/s/user/:id"; //uses put method


}
