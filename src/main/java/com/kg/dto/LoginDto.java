package com.kg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotEmpty
    @NotNull
    private String user;
    @NotEmpty
    @NotNull
    private String password;

    public String getSecret(){
        return DigestUtils.sha256Hex(password);
    }
}
