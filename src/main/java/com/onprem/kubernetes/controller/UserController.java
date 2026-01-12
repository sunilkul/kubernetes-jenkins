package com.onprem.kubernetes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${custom.message:Default Boot Message}")
    private String message;

    @GetMapping
    public ResponseEntity<String> getUser(){
        return new ResponseEntity<>("Sunil Kulkarni : "+ message, HttpStatus.OK);
    }


}
