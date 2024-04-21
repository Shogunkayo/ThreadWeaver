package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.model.Peer;
import com.ThreadWeaver.prototype.model.PeerRegistrationRequest;

import com.ThreadWeaver.prototype.service.PeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/peers")
public class PeerController {
    private final PeerService peerService;

    @Autowired
    public PeerController(PeerService peerService) {
        this.peerService = peerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPeer(@RequestBody PeerRegistrationRequest request) {
        System.out.println("Registering peer");
        try {
            Peer peer = new Peer();
            peer.setPort(request.getPort());
            peer.setIpAddress(request.getIpAddress());
            peer.setOnline(true);
            peerService.registerPeer(peer, request.getChunkChecksums());
            return ResponseEntity.ok().body("Peer registered successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Error registering peer: " + e.getMessage());
        }
    }

    @DeleteMapping("/deregister/{peerId}")
    public ResponseEntity<String> deregisterPeer(@PathVariable Long peerId) {
        peerService.deregisterPeer(peerId);
        return ResponseEntity.ok().body("Peer deregistered successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Peer>> getAllPeers() {
        List<Peer> peers = peerService.getAllPeers();
        return ResponseEntity.ok().body(peers);
    }

    @GetMapping("/online")
    public ResponseEntity<List<Peer>> getOnlinePeers() {
        List<Peer> onlinePeers = peerService.getOnlinePeers();
        return ResponseEntity.ok().body(onlinePeers);
    }
}
