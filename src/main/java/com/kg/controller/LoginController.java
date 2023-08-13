package com.kg.controller;

import com.kg.dto.LoginDto;
import com.kg.dto.UserDto;
import com.kg.security.Constans;
import com.kg.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(Constans.SIGN_IN_URL)
    public UserDto login(@Valid @RequestBody LoginDto loginDto) {
        return loginService.signIn(loginDto);
    }

    @PostMapping(Constans.SIGN_UP_URL)
    public UserDto signUp(@Valid @RequestBody LoginDto loginDto, @RequestHeader String signature) {
        return loginService.signUp(loginDto, signature);
    }

}
