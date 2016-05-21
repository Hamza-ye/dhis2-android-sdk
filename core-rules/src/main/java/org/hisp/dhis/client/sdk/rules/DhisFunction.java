package org.hisp.dhis.client.sdk.rules;

/**
 * Created by markusbekken on 20.05.2016.
 */
abstract class DhisFunction {
    private String name;
    private Integer parameters;

    public DhisFunction(String name, Integer parameters){
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Integer getParameters() {
        return parameters;
    }

    public abstract String execute( String expression );
}