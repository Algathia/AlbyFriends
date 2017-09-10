package fr.algathia.albyfriends;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import fr.algathia.algathiaapi.impl.player.IBungeePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Vialonyx
 */

public class FriendPlayer extends IBungeePlayer {

    List<UUID> friends;

    public FriendPlayer(UUID uuid) {
        super(uuid);
        this.friends = new ArrayList<>();

        try{
            new JsonParser().parse(getData("friends")).getAsJsonArray().forEach(element -> friends.add(UUID.fromString(element.getAsString())));
        } catch (NullPointerException e){
            saveFriends();
        }

    }

    public void saveFriends() {
        JsonArray jsonFriends = new JsonArray();
        friends.stream().map(UUID::toString).forEach(jsonFriends::add);
        this.saveData("friends", jsonFriends.toString());
    }

    public List<UUID> getFriends(){
        return this.friends;
    }

}
