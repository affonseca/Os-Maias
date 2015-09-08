package com.mobilelearning.maias.serviceHandling.handlers;

import com.mobilelearning.maias.serviceHandling.json.ClassArray;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 10-02-2015
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public interface UserClassesHandler {

    void onGettingUserClassesSuccess(ClassArray response);

    void onGettingUserClassesError(String error);

}
