/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatt.cc.auth.AuthController;
import com.smatt.cc.auth.ChallengeGen;
import com.smatt.cc.contact.ContactController;
import com.smatt.cc.db.DatabaseHelper;
import com.smatt.cc.index.IndexController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;
import spark.template.handlebars.HandlebarsTemplateEngine;

import static com.smatt.cc.config.Constants.AUTH_STATUS;
import static com.smatt.cc.config.Routes.*;
import static spark.Spark.*;

public class App {

    Logger logger = LoggerFactory.getLogger(App.class);
    private ChallengeGen gen;
    private SRP6JavascriptServerSessionSHA256 server;


    public App() {

        ObjectMapper objectMapper = new ObjectMapper();


        //setup Sparkjava
        //this tells sparkjava that our static files are in the public dir
        staticFileLocation("/public");
        port(getHerokuAssignedPort());

        //initiate our DatabaseHelper that will map our Model Classes
        new DatabaseHelper();


        //ensure user is logged in to have access to protected routes
        before("/*/", (req, res) -> {
            Session session = req.session(true);
            boolean auth = (session.attribute(AUTH_STATUS) != null) && Boolean.parseBoolean(session.attribute(AUTH_STATUS).toString());
            logger.debug("auth status = " + auth);
            if (!auth) {
                logger.warn("Secured Area! Login is REQUIRED");
                res.redirect(GET_LOGIN_PAGE);
                halt(401);
            }
        });

//		Handle homepage routes
        get(HOME, IndexController::serveHomePage, new HandlebarsTemplateEngine());

//		handle authentication routes
        get(GET_LOGIN_PAGE, AuthController::serveLoginPage, new HandlebarsTemplateEngine());
        post(DO_LOGIN, AuthController::handleLogin, objectMapper::writeValueAsString);
        post(DO_AUTH, AuthController::handleAuth, objectMapper::writeValueAsString);
        get(GET_SIGN_UP, AuthController::serveSignUpPage, new HandlebarsTemplateEngine());
        post(DO_SIGN_UP, AuthController::handleSignUp, objectMapper::writeValueAsString);
        get(LOGOUT, AuthController::handleSignOut);


//		handle CRUD routes for contacts
        get(DASHBOARD, ContactController::serveDashboard, new HandlebarsTemplateEngine());
        delete(DELETE, ContactController::handleDeleteContact, objectMapper::writeValueAsString);
        put(UPDATE, "application/json", ContactController::handleUpdateContact);
        post(NEW, "application/json", ContactController::handleNewContact);

    }


    public static int getHerokuAssignedPort() {
//         this will get the heroku assigned port in production
//         or return 8080 for use in local dev
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return 8080 on localhost
    }


    public static void main(String[] args) {
        new App();
    }


}
