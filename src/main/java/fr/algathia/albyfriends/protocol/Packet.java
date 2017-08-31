package fr.algathia.albyfriends.protocol;

import java.util.UUID;

/**
 * @author Vialonyx
 */

public interface Packet {

    public void execute(String[] args);
    public String name();
    public void send(String from, String... args); // ! USING TEMPORARY STRING RATHER UUID (NOT A FORGOT, BITCH)

}
