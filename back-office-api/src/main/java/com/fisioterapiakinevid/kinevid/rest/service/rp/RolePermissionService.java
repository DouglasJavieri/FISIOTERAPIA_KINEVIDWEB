package com.fisioterapiakinevid.kinevid.rest.service.rp;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionResponseDto;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
public interface RolePermissionService {

    List<PermissionResponseDto> getPermissionsByRoleId(Long roleId) throws OperationException;
    RolePermissionResponseDto assignPermissionToRole(RolePermissionRequestDto request) throws OperationException;
    void removePermissionFromRole(Long roleId, Long permissionId) throws OperationException;

}

