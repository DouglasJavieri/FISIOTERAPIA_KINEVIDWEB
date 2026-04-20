package com.fisioterapiakinevid.kinevid.rest.service.ur.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.entity.ur.UserRole;
import com.fisioterapiakinevid.kinevid.rest.repository.ur.UserRoleRepository;
import com.fisioterapiakinevid.kinevid.rest.service.role.RoleService;
import com.fisioterapiakinevid.kinevid.rest.service.u.UserService;
import com.fisioterapiakinevid.kinevid.rest.service.ur.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getRolesByUserId(Long userId) throws OperationException {
        try {
            userService.getUserById(userId);
            List<Role> roles = userRoleRepository.findRolesByUserId(userId);
            return roles.stream()
                    .map(RoleResponseDto::new)
                    .collect(Collectors.toList());

        } catch (OperationException e) {
            log.error("Error al buscar roles del usuario ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar roles del usuario ID {}", userId, e);
            throw new OperationException("Error al buscar roles del usuario");
        }
    }

    @Override
    @Transactional
    public UserRoleResponseDto assignRoleToUser(UserRoleRequestDto request) throws OperationException {
        try {
            UserResponseDto userDto = userService.getUserById(request.getUserId());
            RoleResponseDto roleDto = roleService.getRoleById(request.getRoleId());

            if (userRoleRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
                throw new OperationException(
                        "El rol '" + roleDto.getName() + "' ya estÃ¡ asignado al usuario '" + userDto.getUsername() + "'.");
            }
            User userRef = new User();
            userRef.setId(request.getUserId());

            Role roleRef = new Role();
            roleRef.setId(request.getRoleId());

            UserRole userRole = UserRole.builder()
                    .user(userRef)
                    .role(roleRef)
                    .build();

            UserRole saved = userRoleRepository.save(userRole);
            log.info("Rol '{}' asignado al usuario '{}'", roleDto.getName(), userDto.getUsername());
            return UserRoleResponseDto.builder()
                    .id(saved.getId())
                    .userId(userDto.getId())
                    .username(userDto.getUsername())
                    .roleId(roleDto.getId())
                    .roleName(roleDto.getName())
                    .build();

        } catch (OperationException e) {
            log.error("Error al asignar rol a usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al asignar rol a usuario", e);
            throw new OperationException("Error al asignar rol a usuario");
        }
    }


    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) throws OperationException {
        try {
            userService.getUserById(userId);
            RoleResponseDto roleDto = roleService.getRoleById(roleId);

            UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                    .orElseThrow(() -> new OperationException(
                            "El rol '" + roleDto.getName() + "' no estÃ¡ asignado a este usuario."));

            userRole.setDeleted(true);
            userRoleRepository.save(userRole);
            log.info("Rol '{}' removido del usuario ID='{}'", roleDto.getName(), userId);

        } catch (OperationException e) {
            log.error("Error al remover rol de usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al remover rol de usuario", e);
            throw new OperationException("Error al remover rol de usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getPrimaryRoleNameByUserId(Long userId) throws OperationException {
        try {
            List<Role> roles = userRoleRepository.findRolesByUserId(userId);
            if (roles.isEmpty()) {
                log.warn("El usuario ID={} no tiene roles asignados", userId);
                return Optional.empty();
            }
            // Retorna el nombre del primer rol activo encontrado
            return Optional.of(roles.get(0).getName());

        } catch (Exception e) {
            log.error("Error al obtener rol principal del usuario ID={}", userId, e);
            throw new OperationException("Error al obtener rol del usuario");
        }
    }
}

