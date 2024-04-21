package com.ThreadWeaver.prototype.service;

import com.ThreadWeaver.prototype.model.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Service
public class HeartbeatService {
    @Autowired
    private final PeerService peerService;
    private final WebClient webClient;


    // Interval for heartbeat check in milliseconds : every 1 minutes
    private static final long HEARTBEAT_CHECK_INTERVAL = 1 * 60 * 1000;

    public HeartbeatService(PeerService peerService, WebClient.Builder webClientBuilder) {
        this.peerService = peerService;
        this.webClient = webClientBuilder.build();;
    }

    @Scheduled(fixedDelay = HEARTBEAT_CHECK_INTERVAL)
    public void checkHeartBeat() {
        List<Peer> onlinePeers = peerService.getOnlinePeers();

        for (Peer peer : onlinePeers) {
            boolean isOnline = checkHeartbeat(peer);
            System.out.println("PEER: " + peer.getIpAddress() + ":" + peer.getPort() + " IS " + isOnline);
            peer.setOnline(isOnline);
            peerService.savePeer(peer);
        }
    }

    private boolean checkHeartbeat(Peer peer) {
        try {
            // Send an HTTP GET request to the health check endpoint
            System.out.println("SENDING HEARTBEAT TO PEER: " + peer.getIpAddress() + ":" + peer.getPort());
            String check_uri = "http://" + peer.getIpAddress() + ":" + peer.getPort() + "/actuator/health";
            Mono<String> response = webClient.get()
                    .uri(check_uri)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            return responseBody != null && !responseBody.isEmpty();
        } catch (Exception e) {
            // Connection failed or timed out, peer is offline
            return false;
        }
    }
}