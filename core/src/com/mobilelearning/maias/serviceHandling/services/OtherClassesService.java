package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.mobilelearning.maias.serviceHandling.Errors;
import com.mobilelearning.maias.serviceHandling.RequestException;
import com.mobilelearning.maias.serviceHandling.ServiceRequester;
import com.mobilelearning.maias.serviceHandling.handlers.OtherClassesHandler;
import com.mobilelearning.maias.serviceHandling.json.ClassArray;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class OtherClassesService implements Service {

    private OtherClassesHandler handler;


    public OtherClassesService(OtherClassesHandler handler) {
        this.handler = handler;
    }

    public void requestOtherClasses() {

        try{
            ServiceRequester serviceRequester = new ServiceRequester(this, null);
            serviceRequester.postRequest();
        }
        catch (RequestException e){
            onRequestFailure(Errors.MAX_CONCURRENT_REQUESTS_REACHED.getValue());
        }
    }


    @Override
    public void onRequestSuccess(JsonValue response) {
        ClassArray out;

        try{
            out = ClassArray.load(response);
        }
        catch (SerializationException ex){
            onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
            return;
        }

        handler.onGettingOtherClassesSuccess(out);

    }

    @Override
    public void onRequestFailure(String error) {
        handler.onGettingOtherClassesError(error);
    }

    @Override
    public String getType() {
        return ServiceType.OTHER_CLASSES.getValue();
    }
}
