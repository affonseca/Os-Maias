package com.mobilelearning.maias.serviceHandling.handlers;


import com.mobilelearning.maias.serviceHandling.json.UserData;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public interface RegisterHandler {

    void onRegistrationSuccess(UserData response);

    void onRegistrationError(String error);
}
