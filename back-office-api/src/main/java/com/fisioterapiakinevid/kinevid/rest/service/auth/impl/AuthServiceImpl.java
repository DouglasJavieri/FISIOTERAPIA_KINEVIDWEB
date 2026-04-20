package com.fisioterapiakinevid.kinevid.rest.service.auth.impl;

import com.fisioterapiakinevid.kinevid.config.security.JwtUtils;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.ChangePasswordRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.JwtResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.LoginRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.RefreshTokenRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.RefreshToken;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.repository.rp.RolePermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.u.UserRepository;
import com.fisioterapiakinevid.kinevid.rest.service.auth.AuthService;
import com.fisioterapiakinevid.kinevid.rest.service.auth.RefreshTokenService;
import com.fisioterapiakinevid.kinevid.rest.service.ur.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public JwtResponseDto login(LoginRequestDto loginRequest) throws OperationException {
        log.info("Intento de login para usuario: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsernameAuthentication(loginRequest.getUsername())
                    .orElseThrow(() -> new OperationException("Usuario no encontrado"));

            String accessToken = jwtUtils.generateAccessToken(user.getUsername());
            long accessTokenExpiresIn = jwtUtils.getAccessTokenExpirationTime();

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, null, null);

            String rolePrincipal = userRoleService.getPrimaryRoleNameByUserId(user.getId())
                    .orElse("USER");

            List<String> permissions = rolePermissionRepository.findPermissionNamesByUserId(user.getId());

            log.info("Login exitoso para usuario: {} con rol: {} y {} permisos",
                    user.getUsername(), rolePrincipal, permissions.size());

            return JwtResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpiresIn)
                    .userInfo(JwtResponseDto.UserAuthInfoDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .fullName(user.getUsername())
                            .role(rolePrincipal)
                            .permissions(permissions)
                            .build()
                    )
                    .build();

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.warn("Login fallido para usuario: {} - {}", loginRequest.getUsername(), e.getMessage());
            throw new OperationException("Credenciales invÃ¡lidas. Verifique usuario y contraseÃ±a");
        } catch (OperationException e) {
            log.warn("Error operacional en login: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en login", e);
            throw new OperationException("Error durante el login. Intente nuevamente");
        }
    }

    @Override
    @Transactional
    public JwtResponseDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequest) throws OperationException {
        log.info("Intento de renovaciÃ³n de Access Token");

        try {
            String refreshTokenValue = refreshTokenRequest.getRefreshToken();

            if (!jwtUtils.validateRefreshToken(refreshTokenValue)) {
                log.warn("Refresh Token JWT invÃ¡lido o expirado");
                throw new OperationException("Refresh Token invÃ¡lido o expirado");
            }

            if (!refreshTokenService.validateRefreshToken(refreshTokenValue)) {
                log.warn("Refresh Token no vÃ¡lido en BD");
                throw new OperationException("Refresh Token no vÃ¡lido");
            }

            String username = jwtUtils.getUsernameFromRefreshToken(refreshTokenValue);

            User user = userRepository.findByUsernameAuthentication(username)
                    .orElseThrow(() -> new OperationException("Usuario no encontrado"));

            String rolePrincipal = userRoleService.getPrimaryRoleNameByUserId(user.getId())
                    .orElse("USER");

            List<String> permissions = rolePermissionRepository.findPermissionNamesByUserId(user.getId());

            String newAccessToken = jwtUtils.generateAccessToken(user.getUsername());
            long accessTokenExpiresIn = jwtUtils.getAccessTokenExpirationTime();

            log.info("Access Token renovado para usuario: {}", username);

            return JwtResponseDto.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpiresIn)
                    .userInfo(JwtResponseDto.UserAuthInfoDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .fullName(user.getUsername())
                            .role(rolePrincipal)
                            .permissions(permissions)
                            .build()
                    )
                    .build();

        } catch (OperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al renovar Access Token", e);
            throw new OperationException("Error al renovar el token. Intente login nuevamente");
        }
    }

    @Override
    @Transactional
    public void logout(String refreshToken) throws OperationException {
        log.info("Intento de logout");

        try {
            refreshTokenService.revokeToken(refreshToken);
            log.info("Logout exitoso");

        } catch (OperationException e) {
            log.warn("Error al revocar token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en logout", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequest) throws OperationException {
        log.info("Intento de cambio de contraseÃ±a");

        try {
            User user = getCurrentUser();

            if (!changePasswordRequest.getNewPassword()
                    .equals(changePasswordRequest.getConfirmPassword())) {
                log.warn("Las nuevas contraseÃ±as no coinciden para usuario: {}", user.getUsername());
                throw new OperationException("Las nuevas contraseÃ±as no coinciden");
            }

            if (changePasswordRequest.getNewPassword()
                    .equals(changePasswordRequest.getCurrentPassword())) {
                log.warn("La nueva contraseÃ±a debe ser diferente a la actual");
                throw new OperationException("La nueva contraseÃ±a debe ser diferente a la actual");
            }

            if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
                log.warn("ContraseÃ±a actual incorrecta para usuario: {}", user.getUsername());
                throw new OperationException("La contraseÃ±a actual es incorrecta");
            }

            String hashedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
            user.setPassword(hashedNewPassword);
            userRepository.save(user);
            log.info("ContraseÃ±a cambiada para usuario: {}", user.getUsername());

            refreshTokenService.revokeAllUserTokens(user.getId());
            log.info("Todos los tokens revocados para usuario: {}", user.getUsername());

        } catch (OperationException e) {
            log.warn("Error en cambio de contraseÃ±a: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar contraseÃ±a", e);
            throw new OperationException("Error al cambiar la contraseÃ±a");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() throws OperationException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new OperationException("Usuario no autenticado");
            }

            String username = authentication.getName();
            return userRepository.findByUsernameAuthentication(username)
                    .orElseThrow(() -> new OperationException("Usuario no encontrado"));
        } catch (OperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener usuario autenticado", e);
            throw new OperationException("Error al obtener informaciÃ³n del usuario");
        }
    }
}


