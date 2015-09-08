package com.mobilelearning.maias.serviceHandling;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public enum Errors {

    UNKNOWN_ERROR("Ocorreu um erro inesperado"),
    NO_INTERNET_CONNECTION("Sem ligação à internet"),
    CONNECTION_LOST("Ocorreu um erro na comunicação com o servidor"),
    MAX_CONCURRENT_REQUESTS_REACHED("Foi atingido o limite de tarefas paralelas. Tente novamente mais tarde.");

    private String value;

    private Errors(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
