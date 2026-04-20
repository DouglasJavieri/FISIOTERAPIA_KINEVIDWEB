package com.fisioterapiakinevid.kinevid.rest.service.ur;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.ur.UserRoleResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
public interface UserRoleService {

    List<RoleResponseDto> getRolesByUserId(Long userId) throws OperationException;
    UserRoleResponseDto assignRoleToUser(UserRoleRequestDto request) throws OperationException;
    void removeRoleFromUser(Long userId, Long roleId) throws OperationException;

    /**
     * Devuelve el nombre del rol principal del usuario.
     * Se usa en el login y refresh para poblar el campo `role` del JwtResponseDto.
     * Retorna el primer rol activo asignado al usuario, o empty si no tiene ninguno.
     */
    Optional<String> getPrimaryRoleNameByUserId(Long userId) throws OperationException;

}

