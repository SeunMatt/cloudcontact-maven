/**
 *
 */
package com.smatt.cc.auth;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Seun Matt
 * Date 23 Mar 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class ChallengeGen {

    Logger logger = LoggerFactory.getLogger(ChallengeGen.class);
    /**
     * This class is for authentication
     * it will accept the username and use it to
     * compute challenge B
     * it will then send the challenge to the client alongside salt 's' stored for the user
     * to the client, it will have to persist the challenge object
     * for the session
     */

    private String email, salt, B, verifier;
    private HashMap<String, String> resp = new HashMap<>();

    public ChallengeGen() { }

    public ChallengeGen(String email) {
        this.email = email;
    }


    public Map<String, Object> getChallenge(SRP6JavascriptServerSessionSHA256 server) {

//		fetch the user obj from the db

        User user = UserController.getUserByEmail(email);

        if (user != null) {
            verifier = user.getVerifier();
            salt = user.getSalt();
            logger.info("Salt = " + salt + "\nVerifier = " + verifier);

            String B = server.step1(email, salt, verifier);

            logger.info("server  challenge B = " + B);

            if (B != null && !B.isEmpty()) {

                Map<String, Object> resp = new HashMap<>();
                resp.put("salt", salt);
                resp.put("B", B);
                logger.info("Challenge: = \n" + resp.toString());
                return resp;

            } else {
                //invalid server challenge
                return null;
            }
        }

        return null;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public HashMap<String, String> getResp() {
        return resp;
    }

    public void setResp(HashMap<String, String> resp) {
        this.resp = resp;
    }


}
