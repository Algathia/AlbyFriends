package fr.algathia.albyfriends.protocol;

import fr.algathia.albyfriends.protocol.packet.FriendRequestPacket;
import fr.algathia.albyfriends.protocol.packet.ResponseRequestPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vialonyx
 */

public class ProtocolManager {

    private List<Packet> packets;

    public ProtocolManager(){

        this.packets = new ArrayList<>();
        packets.add(new FriendRequestPacket());
        packets.add(new ResponseRequestPacket());

    }

    public void execute(String command, String[] args){
        packets.stream().filter(packet -> packet.name().equals(command)).findFirst().ifPresent(packet -> packet.execute(args));
    }

}
