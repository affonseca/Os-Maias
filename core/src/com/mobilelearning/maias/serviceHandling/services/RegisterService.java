package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.mobilelearning.maias.serviceHandling.Errors;
import com.mobilelearning.maias.serviceHandling.RequestException;
import com.mobilelearning.maias.serviceHandling.ServiceRequester;
import com.mobilelearning.maias.serviceHandling.handlers.RegisterHandler;
import com.mobilelearning.maias.serviceHandling.json.UserData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class RegisterService implements Service {

    private RegisterHandler handler;


    public RegisterService(RegisterHandler handler) {
        this.handler = handler;
    }

    public void requestRegistration(String username, String password) {

        Map<String, String> parameters = new HashMap<>();

        parameters.put("username", username);
        parameters.put("password", password);

        try{
            ServiceRequester serviceRequester = new ServiceRequester(this, parameters);
            serviceRequester.postRequest();
        }
        catch (RequestException e){
            onRequestFailure(Errors.MAX_CONCURRENT_REQUESTS_REACHED.getValue());
        }
    }


    @Override
    public void onRequestSuccess(JsonValue response) {
         UserData out;

        try{
            out = UserData.load(response);
        }
        catch (SerializationException ex){
            onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
            return;
        }

        handler.onRegistrationSuccess(out);

    }

    @Override
    public void onRequestFailure(String error) {
        handler.onRegistrationError(error);
    }

    @Override
    public String getType() {
        return ServiceType.REGISTER.getValue();
    }
}
