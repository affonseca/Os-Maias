package com.mobilelearning.maias.serviceHandling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.serviceHandling.services.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class ServiceRequester {
    //private static final String SERVICE_URL = "http://10.0.2.2/mobilelearning/api/"; //for testing purposes
    private static final String SERVICE_URL = "https://www.fpce.uc.pt/maias/api/"; //real one
    private static final int MAX_CONCURRENT_REQUESTS = 10;

    Service service;
    Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
    Map<String, String> parameters;
    private static AtomicInteger numCurrentServices = new AtomicInteger(0);

    protected boolean startService() {
        if(numCurrentServices.incrementAndGet() == MAX_CONCURRENT_REQUESTS ){
            numCurrentServices.decrementAndGet();
            return false;
        }

        return true;
    }

    protected void finishService() {
        numCurrentServices.getAndDecrement();
    }


    public  ServiceRequester(Service service, Map<String, String> firstParameters) throws  RequestException{

        if ( !startService())
            throw new RequestException(Errors.MAX_CONCURRENT_REQUESTS_REACHED.getValue());

        if(firstParameters == null)
            parameters = new HashMap<>();
        else
            parameters = firstParameters;

        this.service = service;
        String serviceURL = SERVICE_URL+service.getType();

        if(SavedData.getToken() != null){
            parameters.put("token", SavedData.getToken());
            parameters.put("userID", SavedData.getUserID());
        }


        httpPost.setUrl(serviceURL);
        httpPost.setContent(HttpParametersUtils.convertHttpParameters(parameters));

    }

    public void postRequest(){

        Gdx.net.sendHttpRequest (httpPost, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                processResponse(httpResponse.getResultAsString(), httpResponse.getStatus().getStatusCode());

            }

            public void failed(Throwable t){
                processResponse(t.getMessage(), -1);
            }

            @Override
            public void cancelled() {
            }
        });

    }

    public void processResponse(String response, int status) {
        finishService();

        if (status >= 200 && status < 300) {
            String status2;

            JsonReader jsonReader = new JsonReader();
            JsonValue jsonTree = jsonReader.parse(response);

            try{
                status2 = jsonTree.get("status").asString();
            }
            catch (IllegalStateException e){
                service.onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
                return;
            }

            if(status2.compareTo("SUCCESS") != 0){
                service.onRequestFailure(status2);
                return;
            }

            service.onRequestSuccess(jsonTree);
        }

        else if(status == 1001){
            service.onRequestFailure(Errors.NO_INTERNET_CONNECTION.getValue());
        }

        else if(status == -1){
            if(response.contains("Unable to resolve host"))
                service.onRequestFailure(Errors.NO_INTERNET_CONNECTION.getValue());
            else
                service.onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
        }

        else{
            service.onRequestFailure(Errors.CONNECTION_LOST.getValue());
        }

    }
}
