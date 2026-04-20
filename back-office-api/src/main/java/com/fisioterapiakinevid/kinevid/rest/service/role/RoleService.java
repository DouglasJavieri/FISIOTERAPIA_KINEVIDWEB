package com.fisioterapiakinevid.kinevid.rest.service.role;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.PagedRoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 02/03/2026
 */
public interface RoleService {

    // --- CRUD completo ---
    RoleResponseDto createRole(RoleRequestDTO role) throws OperationException;
    RoleResponseDto getRoleById(Long id) throws OperationException;
    Page<PagedRoleResponseDto> findAllRoles(String status, Pageable pageable) throws OperationException;
    RoleResponseDto updateRole(Long id, RoleRequestDTO role) throws OperationException;
    RoleResponseDto changeStatus(Long id, RoleStatus status) throws OperationException;
    void deleteRole(Long id) throws OperationException;
}

