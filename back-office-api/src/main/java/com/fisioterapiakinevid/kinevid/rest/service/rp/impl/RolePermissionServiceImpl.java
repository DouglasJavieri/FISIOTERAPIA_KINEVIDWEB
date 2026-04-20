package com.fisioterapiakinevid.kinevid.rest.service.rp.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.rp.RolePermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.entity.rp.RolePermission;
import com.fisioterapiakinevid.kinevid.rest.repository.p.PermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.role.RoleRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.rp.RolePermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.service.rp.RolePermissionService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> getPermissionsByRoleId(Long roleId) throws OperationException {
        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", roleId)));

            if (role.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", roleId));
            }

            List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(roleId);
            return permissions.stream()
                    .map(PermissionResponseDto::new)
                    .collect(Collectors.toList());

        } catch (OperationException e) {
            log.error("Error al buscar permisos del rol ID {}: {}", roleId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar permisos del rol ID {}", roleId, e);
            throw new OperationException("Error al buscar permisos del rol");
        }
    }

    @Override
    @Transactional
    public RolePermissionResponseDto assignPermissionToRole(RolePermissionRequestDto request) throws OperationException {
        try {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", request.getRoleId())));

            if (role.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", request.getRoleId()));
            }

            Permission permission = permissionRepository.findById(request.getPermissionId())
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", request.getPermissionId())));

            if (permission.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", request.getPermissionId()));
            }

            if (rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                throw new OperationException(
                        "El permiso '" + permission.getName() + "' ya estÃ¡ asignado al rol '" + role.getName() + "'.");
            }

            RolePermission rolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();

            rolePermissionRepository.save(rolePermission);
            log.info("Permiso '{}' asignado al rol '{}'", permission.getName(), role.getName());
            return new RolePermissionResponseDto(rolePermission);
        } catch (OperationException e) {
            log.error("Error al asignar permiso a rol: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al asignar permiso a rol", e);
            throw new OperationException("Error al asignar permiso a rol");
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) throws OperationException {
        try {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", roleId)));

            if (role.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", roleId));
            }

            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", permissionId)));

            if (permission.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", permissionId));
            }

            RolePermission rolePermission = rolePermissionRepository
                    .findByRoleIdAndPermissionId(roleId, permissionId)
                    .orElseThrow(() -> new OperationException(
                            "El permiso '" + permission.getName() + "' no estÃ¡ asignado al rol '" + role.getName() + "'."));

            rolePermission.setDeleted(true);
            rolePermissionRepository.save(rolePermission);

            log.info("Permiso '{}' removido del rol '{}'", permission.getName(), role.getName());

        } catch (OperationException e) {
            log.error("Error al remover permiso de rol: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al remover permiso de rol", e);
            throw new OperationException("Error al remover permiso de rol");
        }
    }
}
