package com.kg.logic;

import com.kg.dao.UserDao;
import com.kg.dto.LoginDto;
import com.kg.dto.UserDto;
import com.kg.entity.User;
import com.kg.exception.BadRequestException;
import com.kg.security.JWTAuthtenticationConfig;
import com.kg.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class LoginManager implements LoginService {

    private static final String USER_EXIST = "User exist!";
    private static final String BAD_CREDENTIALS = "User or pass incorrect!";
    private static final String ACCESS_DENIED = "access denied :/";
    private final UserDao userDao;
    private final JWTAuthtenticationConfig jwtAuthtenticationConfig;
    @Value("${application.signature}")
    private String signature;

    public LoginManager(UserDao userDao, JWTAuthtenticationConfig jwtAuthtenticationConfig) {
        this.userDao = userDao;
        this.jwtAuthtenticationConfig = jwtAuthtenticationConfig;
    }

    @Override
    public UserDto signIn(LoginDto loginDto){

        String userName = loginDto.getUser();
        String password = loginDto.getSecret();

        userDao.findByUserAndPassword(userName, password)
        .orElseThrow(() -> new BadRequestException(BAD_CREDENTIALS));

        String token = jwtAuthtenticationConfig.getJWTToken(userName);
        return new UserDto(userName, password, token);
    }

    @Override
    public UserDto signUp(LoginDto loginDto, String signature){
        if(!this.signature.equals(signature)) {
            throw new AccessDeniedException(ACCESS_DENIED);
        }
        String userName = loginDto.getUser();
        String password = loginDto.getSecret();

        if(userDao.existsByUser(userName)) {
            throw new BadRequestException(USER_EXIST);
        }

        User user = new User(userName,password);
        userDao.save(user);

        String token = jwtAuthtenticationConfig.getJWTToken(userName);
        return new UserDto(userName, password, token);

    }

}