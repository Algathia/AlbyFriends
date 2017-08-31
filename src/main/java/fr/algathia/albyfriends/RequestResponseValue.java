package fr.algathia.albyfriends;

/**
 * @author Vialonyx
 */

public enum RequestResponseValue {

    ACCEPTED("y"),
    REFUSED("n");

    String v;
    RequestResponseValue(String value){
        this.v = value;
    }

    public String get(){
        return this.v;
    }

}
