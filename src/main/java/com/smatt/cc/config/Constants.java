package com.smatt.cc.config;

public class Constants {


    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_USER_NAME = "username";
    public static final String ATTR_EMAIL = "email";
    public static final String OK_PATTERN = "[^a-zA-Z0-9:\",{}@_.\\- ]";
    public static final int SESSION_TIMEOUT = 60 * 30; //30 mins
    public static final String JSON_TYPE = "application/json";
    public static final String AUTH_STATUS = "AUTH_STATUS";

    public static class Templates {
        public static final String INDEX = "index.hbs";
        public static final String DASHBOARD = "main.hbs";
        public static final String LOGIN = "signin.hbs";
        public static final String SIGN_UP = "signup.hbs";
    }

    public static class Database {
        public static final String HEROKU_DB_NAME = "heroku_n35m7bx6"; //this is the last part of the HEROKU_DB_URI
        public static final String LOCAL_DBNAME = "contacts_db";
        public static final String HOST = "127.0.0.1";
        public static final int PORT = 27017;
        //the db uri is from the heroku platform
        public static final String HEROKU_DB_URI = "mongodb://heroku_n35m7bx6:vf99qjg9otp744biaqjtepvurd@ds011725.mlab.com:11725/heroku_n35m7bx6";
    }

    public static class Reply {
        public static final int OK = 200;
        public static final String OK_MSG = "Hurray! Operation Successful";
        public static final int CONTACT_NOT_FOUND = 601;
        public static final String CONTACT_NOT_FOUND_MSG = "Ooops! The resource is not found on the server";
    }

}
