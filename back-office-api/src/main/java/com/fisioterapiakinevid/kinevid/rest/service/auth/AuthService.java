package com.fisioterapiakinevid.kinevid.rest.service.auth;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.ChangePasswordRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.JwtResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.LoginRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.RefreshTokenRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;

public interface AuthService {

    JwtResponseDto login(LoginRequestDto loginRequest) throws OperationException;

    JwtResponseDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequest) throws OperationException;

    void logout(String refreshToken) throws OperationException;

    void changePassword(ChangePasswordRequestDto changePasswordRequest) throws OperationException;

    User getCurrentUser() throws OperationException;
}
