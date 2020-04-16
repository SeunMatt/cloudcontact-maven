/**
 *
 */
package com.smatt.cc.auth;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.smatt.cc.config.Constants;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static com.smatt.cc.config.Constants.*;
import static com.smatt.cc.config.Routes.DASHBOARD;
import static com.smatt.cc.config.Routes.HOME;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 *
 */
public class AuthController {

    static boolean isAuth;
    static Authenticate authenticate;
    static SRP6CryptoParams config;
    static ChallengeGen gen;
    static SRP6JavascriptServerSessionSHA256 server;
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Constructor
     * cloud contacts
     */
    public AuthController() {
    }

    public static ModelAndView serveLoginPage(Request req, Response res) {
        return new ModelAndView(null, Constants.Templates.LOGIN);
    }

    public static Map<String, Object> handleLogin(Request req, Response res) {
        return doLogin(req, res);
    }

    public static Map<String, Object> handleAuth(Request req, Response res) {
        return doAuth(req, res);
    }

    public static ModelAndView serveSignUpPage(Request req, Response res) {
        return new ModelAndView(null, Constants.Templates.SIGN_UP);
    }

    public static Map<String, Object> handleSignUp(Request req, Response res) {
        return doSignUp(req, res);
    }

    public static Response handleSignOut(Request req, Response res) {
        Session session = req.session(false);
        if (session != null) session.invalidate();
        res.redirect(HOME);
        return res;
    }


    private static Map<String, Object> doLogin(Request req, Response res) {

        //the username is sent here and the salt and verifier for that username is
        //is sent back to the client
        //then the auth() method is invoked
        Map<String, Object> respMap = new HashMap<>();
        res.type(JSON_TYPE);


        String email = Jsoup.parse(req.queryParams("email")).text();
        logger.info("email from the client = " + email);

        if (email != null && !email.isEmpty()) {

            server = new SRP6JavascriptServerSessionSHA256(CryptoParams.N_base10, CryptoParams.g_base10);
            gen = new ChallengeGen(email);
            Map<String, Object> challengeResp = gen.getChallenge(server);
            if (challengeResp != null) {
                respMap.putAll(challengeResp);
                respMap.put("code", "200");
                respMap.put("status", "success");
                res.status(200);
                return respMap;
            }
        }

        res.status(401);
        respMap.put("status", "Invalid User Credentials");
        respMap.put("code", "401");
        logger.error("getChallenge() return null map most likely due to null B value and Invalid User Credentials");
        return respMap;
    }


    private static Map<String, Object> doAuth(Request req, Response res) {
        //this place the client has receive the salt and server challenge B
        // now it will use those to calculate it's own client challenge A and M1 which is a special message
        //it will concatenate them in the format M1:A and send it as password along the username (email) to the server
        //the ser

        Map<String, Object> response = new HashMap<>();

        String email = Jsoup.parse(req.queryParams("email")).text();
        String password = Jsoup.parse(req.queryParams("password")).text();
        logger.debug("password from client: \n" + password);

        res.type(JSON_TYPE);


        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {

            authenticate = new Authenticate(password);

            //note that the server obj has been created during call to login()
            String M2 = authenticate.getM2(server);

            if (M2 != null || !M2.isEmpty()) {

                Session session = req.session(true);
                session.maxInactiveInterval(SESSION_TIMEOUT);
                User user = UserController.getUserByEmail(email);
                session.attribute(ATTR_USER_NAME, user.getUsername());
                session.attribute(ATTR_USER_ID, user.getId().toString()); //saves the id as String
                session.attribute(AUTH_STATUS, authenticate.authenticated);
                logger.info("session attribute authenticated: " + session.attribute(AUTH_STATUS));
                session.attribute(ATTR_EMAIL, user.getEmail());
                logger.info(user.toString() + " Has Logged In Successfully");
                logger.info("session in filter: id " + session.id() + " is new: " + session.isNew() + " attr: \n" + session.attributes());

                response.put("M2", M2);
                response.put("code", "200");
                response.put("status", "success");
                response.put("target", DASHBOARD);
                logger.info("Final response sent By doAuth to client = " + response);
                res.status(200);
                return response;
            }

        }

        res.status(401);
        response.put("code", "401");
        response.put("status", "Error! Invalid Login Credentials");
        return response;
    }


    private static Map<String, Object> doSignUp(Request req, Response res) {

        res.type(JSON_TYPE); //set our response type

        Map<String, Object> response = new HashMap<>();
        String username = Jsoup.parse(req.queryParams("username")).text();
        String email = Jsoup.parse(req.queryParams("email")).text();
        String salt = Jsoup.parse(req.queryParams("salt")).text();
        String verifier = Jsoup.parse(req.queryParams("verifier")).text();

        User user = new User(username, email, salt, verifier);

        int i = UserController.createUser(user);

        if (i > 0) {
            logger.info("" + user.toString() + " created successfully");
            res.status(200);
            response.put("code", 200);
            response.put("status", "Account Creation Successful! Proceed to Login");
        } else {
            logger.error("ERROR! Unable to create User " + user.toString());
            response.put("code", "401");
            response.put("status", "ERROR! username/email exists already!");
            res.status(401);
        }

        logger.info("json to be returned = " + response);
        return response;
    }


}
