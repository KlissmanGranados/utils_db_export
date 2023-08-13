package com.kg.service;

import com.kg.dto.LoginDto;
import com.kg.dto.UserDto;

public interface LoginService {
    UserDto signIn(LoginDto loginDto);

    UserDto signUp(LoginDto loginDto, String signature);
}
