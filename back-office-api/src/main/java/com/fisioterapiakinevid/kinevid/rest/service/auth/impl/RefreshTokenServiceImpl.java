package com.fisioterapiakinevid.kinevid.rest.service.auth.impl;

import com.fisioterapiakinevid.kinevid.config.security.JwtUtils;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.RefreshToken;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.repository.auth.RefreshTokenRepository;
import com.fisioterapiakinevid.kinevid.rest.service.auth.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 */
@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${kinevid.app.jwtRefreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    /**
     * Crea un nuevo Refresh Token para un usuario
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) throws OperationException {
        try {
            if (user == null || user.getId() == null) {
                throw new OperationException("Usuario invÃ¡lido para crear Refresh Token");
            }

            // Paso 1: Generar JWT del Refresh Token
            String refreshTokenJwt = jwtUtils.generateRefreshToken(user.getUsername());
            log.debug("JWT Refresh Token generado para usuario: {}", user.getUsername());

            // Paso 2: Calcular fecha de expiraciÃ³n en segundos
            LocalDateTime expiryDate = LocalDateTime.now()
                    .plusSeconds(refreshTokenExpirationMs / 1000);

            // Paso 3: Crear entidad RefreshToken con el JWT generado
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(refreshTokenJwt)  // â† Asignar el JWT generado
                    .user(user)
                    .revoked(false)
                    .expiryDate(expiryDate)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            // Paso 4: Guardar en BD
            refreshToken = refreshTokenRepository.save(refreshToken);
            log.info("Refresh Token creado exitosamente para usuario: {}", user.getUsername());
            return refreshToken;

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al crear Refresh Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear Refresh Token", e);
            throw new OperationException("Error al crear Refresh Token", e);
        }
    }

    /**
     * Busca un Refresh Token por su valor JWT
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) throws OperationException {
        try {
            if (token == null || token.isBlank()) {
                throw new OperationException("Token invÃ¡lido");
            }
            return refreshTokenRepository.findByToken(token);
        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al buscar Refresh Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar Refresh Token", e);
            throw new OperationException("Error al buscar Refresh Token", e);
        }
    }

    /**
     * Valida si un Refresh Token es vÃ¡lido (no revocado y no expirado)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String token) throws OperationException {
        try {
            if (token == null || token.isBlank()) {
                log.warn("Token nulo o vacÃ­o para validaciÃ³n");
                return false;
            }

            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

            if (refreshToken.isEmpty()) {
                log.warn("Refresh Token no encontrado en BD");
                return false;
            }

            RefreshToken rt = refreshToken.get();

            // Verificar si estÃ¡ revocado
            if (rt.getRevoked()) {
                log.warn("Refresh Token ha sido revocado para usuario: {}", rt.getUser().getUsername());
                return false;
            }

            // Verificar si ha expirado
            if (rt.isExpired()) {
                log.warn("Refresh Token expirado para usuario: {}", rt.getUser().getUsername());
                return false;
            }

            log.debug("Refresh Token vÃ¡lido para usuario: {}", rt.getUser().getUsername());
            return true;

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al validar Refresh Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al validar Refresh Token", e);
            throw new OperationException("Error al validar Refresh Token", e);
        }
    }

    /**
     * Revoca un Refresh Token especÃ­fico marcÃ¡ndolo como revocado en BD
     */
    @Override
    @Transactional
    public void revokeToken(String token) throws OperationException {
        try {
            if (token == null || token.isBlank()) {
                throw new OperationException("Token invÃ¡lido");
            }

            int affectedRows = refreshTokenRepository.revokeToken(token);

            if (affectedRows == 0) {
                log.warn("No se encontrÃ³ Refresh Token para revocar");
            } else {
                log.info("Refresh Token revocado exitosamente: {} fila(s) afectada(s)", affectedRows);
            }
        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al revocar Refresh Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al revocar Refresh Token", e);
            throw new OperationException("Error al revocar Refresh Token", e);
        }
    }

    /**
     * Revoca todos los Refresh Tokens de un usuario
     * Ãštil para logout global o cambio de contraseÃ±a
     */
    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) throws OperationException {
        try {
            if (userId == null || userId <= 0) {
                throw new OperationException("ID de usuario invÃ¡lido");
            }

            int affectedRows = refreshTokenRepository.revokeAllUserTokens(userId);
            log.info("Todos los Refresh Tokens revocados para usuario ID: {} - Fila(s) afectada(s): {}",
                    userId, affectedRows);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al revocar tokens del usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al revocar tokens del usuario", e);
            throw new OperationException("Error al revocar tokens del usuario", e);
        }
    }

    /**
     * Elimina todos los Refresh Tokens expirados de la BD
     * Ayuda a limpiar la BD de tokens no vÃ¡lidos
     */
    @Override
    @Transactional
    public void deleteExpiredTokens() throws OperationException {
        try {
            LocalDateTime now = LocalDateTime.now();
            int affectedRows = refreshTokenRepository.deleteExpiredTokens(now);
            log.info("Tokens expirados eliminados: {} fila(s) afectada(s)", affectedRows);

        } catch (Exception e) {
            log.error("Error al eliminar tokens expirados", e);
            throw new OperationException("Error al eliminar tokens expirados", e);
        }
    }

    /**
     * Cuenta cuÃ¡ntos Refresh Tokens vÃ¡lidos tiene un usuario
     * Ãštil para limitar sesiones simultÃ¡neas
     */
    @Override
    @Transactional(readOnly = true)
    public long countValidTokensForUser(Long userId) throws OperationException {
        try {
            if (userId == null || userId <= 0) {
                throw new OperationException("ID de usuario invÃ¡lido");
            }
            long count = refreshTokenRepository.countValidTokensByUserId(userId);
            log.debug("Tokens vÃ¡lidos para usuario ID {}: {}", userId, count);
            return count;
        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al contar tokens vÃ¡lidos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al contar tokens vÃ¡lidos", e);
            throw new OperationException("Error al contar tokens vÃ¡lidos", e);
        }
    }
}
