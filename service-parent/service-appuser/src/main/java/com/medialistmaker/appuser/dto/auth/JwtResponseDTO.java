package com.medialistmaker.appuser.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {

    private String token;

    private String username;

    private Date expiresAt;
}
