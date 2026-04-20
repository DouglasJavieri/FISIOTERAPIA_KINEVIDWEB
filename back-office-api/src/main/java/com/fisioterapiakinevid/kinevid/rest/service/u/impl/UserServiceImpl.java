package com.fisioterapiakinevid.kinevid.rest.service.u.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.*;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.u.UserRepository;
import com.fisioterapiakinevid.kinevid.rest.service.u.UserService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameAuthentication(String username) {
        try {
            return userRepository.findByUsernameAuthentication(username);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar usuario por username: " + username, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        try {
            Optional<UsernameResponseDto> user = this.userRepository.findByUsername(username);
            return user.isPresent();
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar existencia por username: " + username, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        try {
            String cleanEmail = email.trim().replace(" ", "+").toLowerCase();
            Optional<EmailResponseDto> emailModel = this.userRepository.findByEmail(cleanEmail);
            return emailModel.isPresent();
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar existencia por email: " + email, ex);
        }
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDTO user) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Usuario", user.getUsername(), true, 30);
            ValidationUtil.throwExceptionIfInvalidEmail("Email", user.getEmail(), true);
            ValidationUtil.throwExceptionIfInvalidText("Email", user.getEmail(), true, 50);
            ValidationUtil.throwExceptionIfInvalidText("Password", user.getPassword(), true, 100);


            Optional<UsernameResponseDto> findByUsername = userRepository.findByUsername(user.getUsername());
            if (findByUsername.isPresent()) {
                throw new OperationException(FormatUtil.yaRegistrado("Usuario", "Username", user.getUsername()));
            }

            Optional<EmailResponseDto> findByEmail = userRepository.findByEmail(user.getEmail().trim().toLowerCase());
            if (findByEmail.isPresent()) {
                throw new OperationException(FormatUtil.yaRegistrado("Usuario", "Email", user.getEmail()));
            }

            User userModel = User.builder()
                    .username(user.getUsername().toLowerCase().trim())
                    .email(user.getEmail().toLowerCase().trim())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(userModel);

            log.info("Usuario creado: {}", userModel.getUsername());
            return new UserResponseDto(userModel);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al crear usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear usuario", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) throws OperationException {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Usuario", id)));

            if (user.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Usuario", id));
            }

            return new UserResponseDto(user);

        } catch (OperationException e) {
            log.error("Error al buscar usuario con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar usuario con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) throws OperationException {
        try {
            return userRepository.findAllActive(pageable)
                    .map(UserResponseDto::new);
        } catch (Exception e) {
            log.error("Error inesperado al listar usuarios", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar usuarios");
        }
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDTO user) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Usuario", user.getUsername(), true, 30);
            ValidationUtil.throwExceptionIfInvalidEmail("Email", user.getEmail(), true);
            ValidationUtil.throwExceptionIfInvalidText("Email", user.getEmail(), true, 50);
            ValidationUtil.throwExceptionIfInvalidText("Password", user.getPassword(), false, 100);

            User userModel = userRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Usuario", id)));

            if (userModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Usuario", id));
            }

            // ValidaciÃ³n de duplicados excluyendo al propio usuario
            Optional<UsernameResponseDto> findByUsername = userRepository.findByUsernameExcludingId(user.getUsername(), id);
            if (findByUsername.isPresent()) {
                throw new OperationException(FormatUtil.yaRegistrado("Usuario", "Username", user.getUsername()));
            }

            Optional<EmailResponseDto> findByEmail = userRepository.findByEmailExcludingId(user.getEmail().trim().toLowerCase(), id);
            if (findByEmail.isPresent()) {
                throw new OperationException(FormatUtil.yaRegistrado("Usuario", "Email", user.getEmail()));
            }

            userModel.setUsername(user.getUsername().toLowerCase().trim());
            userModel.setEmail(user.getEmail().toLowerCase().trim());

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userModel.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(userModel);
            log.info("Usuario actualizado: ID={}", id);
            return new UserResponseDto(userModel);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al actualizar usuario con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar usuario con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar usuario");
        }
    }

    @Override
    @Transactional
    public UserResponseDto changeUserStatus(Long id, UserStatus status) throws OperationException {
        try {
            if (status == UserStatus.ELIMINATION) {
                throw new OperationException(
                        "No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Usuario", id)));

            if (user.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Usuario", id));
            }

            if (user.getStatus() == status) {
                throw new OperationException(
                        "El usuario ya se encuentra en el estado '" + status.getDescription() + "'.");
            }

            user.setStatus(status);
            userRepository.save(user);
            log.info("Estado del usuario ID={} cambiado a: {}", id, status);
            return new UserResponseDto(user);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del usuario con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del usuario con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del usuario");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws OperationException {
        try {
            User userModel = userRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Usuario", id)));

            if (userModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Usuario", id));
            }

            userModel.setDeleted(true);
            userModel.setStatus(UserStatus.ELIMINATION);
            userRepository.save(userModel);
            log.info("Usuario con ID={} eliminado lÃ³gicamente", id);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al eliminar usuario con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar usuario con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar usuario");
        }
    }
}
