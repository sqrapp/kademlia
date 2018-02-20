package com.soriole.dht.controller;

import com.soriole.dht.service.KademliaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    KademliaService kademliaService;

    @GetMapping("/")
    public String index(){
        return "API v1.0.0";
    }

    @GetMapping("/peers")
    public List peers(){
        return kademliaService.getKadNode().getRoutingTable().getAllNodes();
    }
}
