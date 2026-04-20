package com.fisioterapiakinevid.kinevid.rest.service.p;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PagedPermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 03/03/2026
 */
public interface PermissionService {

    PermissionResponseDto createPermission(PermissionRequestDTO permissionDto) throws OperationException;
    PermissionResponseDto getPermissionById(Long id) throws OperationException;
    Page<PagedPermissionResponseDto> findAllPermissions(String status, Pageable pageable) throws OperationException;
    PermissionResponseDto updatePermission(Long id, PermissionRequestDTO permissionDto) throws OperationException;
    PermissionResponseDto changeStatus(Long id, PermissionStatus status) throws OperationException;
    void deletePermission(Long id) throws OperationException;
}

