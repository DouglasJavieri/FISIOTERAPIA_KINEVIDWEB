package com.fisioterapiakinevid.kinevid.rest.service.auth;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.RefreshToken;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * Servicio para gestionar Refresh Tokens
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) throws OperationException;

    Optional<RefreshToken> findByToken(String token) throws OperationException;

    boolean validateRefreshToken(String token) throws OperationException;

    void revokeToken(String token) throws OperationException;

    void revokeAllUserTokens(Long userId) throws OperationException;

    void deleteExpiredTokens() throws OperationException;

    long countValidTokensForUser(Long userId) throws OperationException;
}
