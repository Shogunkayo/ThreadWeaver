package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Service
public class HeartbeatService {
    @Autowired
    private PeerService peerService;

    // Interval for heartbeat check in milliseconds : every 2 minutes
    private static final long HEARTBEAT_CHECK_INTERVAL = 2 * 60 * 1000;

    @Scheduled(fixedDelay = HEARTBEAT_CHECK_INTERVAL)
    public void checkHeartBeat() {
        List<Peer> onlinePeers = peerService.getOnlinePeers();

        for (Peer peer : onlinePeers) {
            boolean isOnline = checkHeartbeat(peer);
            peer.setOnline(isOnline);
            peerService.savePeer(peer);
        }
    }

    private boolean checkHeartbeat(Peer peer) {
        try (Socket socket = new Socket()) {
            // Set a timeout for the connection attempt
            socket.connect(new InetSocketAddress(peer.getIpAddress(), peer.getPort()), 2000);
            return true;
        } catch (Exception e) {
            // Connection failed or timed out, peer is offline
            return false;
        }
    }
}
