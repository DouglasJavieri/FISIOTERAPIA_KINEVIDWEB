package com.fisioterapiakinevid.kinevid.rest.service.p.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PagedPermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.p.PermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.rp.RolePermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.service.p.PermissionService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 03/03/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public PermissionResponseDto createPermission(PermissionRequestDTO permissionDto) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre Permiso", permissionDto.getName(), true, 60);
            ValidationUtil.throwExceptionIfInvalidText("DescripciÃ³n", permissionDto.getDescription(), true, 255);

            String normalizedName = permissionDto.getName().toUpperCase().strip();

            if (permissionRepository.existsPermissionByName(normalizedName)) {
                throw new OperationException("Ya existe un permiso con el nombre: " + normalizedName);
            }

            Permission permissionModel = Permission.builder()
                    .name(normalizedName)
                    .description(permissionDto.getDescription().strip())
                    .status(PermissionStatus.ACTIVE)
                    .build();
            permissionRepository.save(permissionModel);

            log.info("Permiso creado: {}", normalizedName);
            return new PermissionResponseDto(permissionModel);

        } catch (OperationException e) {
            log.error("Error al crear permiso: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear permiso", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear el permiso");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDto getPermissionById(Long id) throws OperationException {
        try {
            Permission permission = permissionRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", id)));

            if (permission.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", id));
            }

            return new PermissionResponseDto(permission);

        } catch (OperationException e) {
            log.error("Error al buscar permiso con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar permiso con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el permiso");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PagedPermissionResponseDto> findAllPermissions(String status, Pageable pageable) throws OperationException {
        try {
            PermissionStatus permissionStatus = null;
            if (status != null && !status.isBlank()) {
                try {
                    permissionStatus = PermissionStatus.valueOf(status.toUpperCase().strip());
                } catch (IllegalArgumentException ex) {
                    throw new OperationException(
                            "Estado de permiso invÃ¡lido: '" + status + "'. Valores vÃ¡lidos: ACTIVE, INACTIVE");
                }
            }
            return permissionRepository.findAllPermissions(permissionStatus, pageable);

        } catch (OperationException e) {
            log.error("Error al listar permisos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar permisos", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar permisos");
        }
    }

    @Override
    @Transactional
    public PermissionResponseDto updatePermission(Long id, PermissionRequestDTO permissionDto) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre Permiso", permissionDto.getName(), true, 60);
            ValidationUtil.throwExceptionIfInvalidText("DescripciÃ³n", permissionDto.getDescription(), true, 255);

            Permission permissionModel = permissionRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", id)));

            if (permissionModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", id));
            }

            String normalizedName = permissionDto.getName().toUpperCase().strip();

            if (permissionRepository.existsPermissionByNameExcludingId(normalizedName, id)) {
                throw new OperationException("Ya existe un permiso con el nombre: " + normalizedName);
            }

            permissionModel.setName(normalizedName);
            permissionModel.setDescription(permissionDto.getDescription().strip());
            permissionRepository.save(permissionModel);

            log.info("Permiso actualizado: ID={}, Nombre={}", id, normalizedName);
            return new PermissionResponseDto(permissionModel);

        } catch (OperationException e) {
            log.error("Error al actualizar permiso con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar permiso con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar el permiso");
        }
    }

    @Override
    @Transactional  // BUG FIX: era un stub vacÃ­o y no guardaba nada
    public PermissionResponseDto changeStatus(Long id, PermissionStatus status) throws OperationException {
        try {
            if (status == PermissionStatus.ELIMINATION) {
                throw new OperationException(
                        "No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }

            Permission permission = permissionRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", id)));

            if (permission.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", id));
            }

            if (permission.getStatus() == status) {
                throw new OperationException(
                        "El permiso ya se encuentra en el estado '" + status.getDescription() + "'.");
            }

            permission.setStatus(status);
            permissionRepository.save(permission);
            log.info("Estado del permiso ID={} cambiado a: {}", id, status);
            return new PermissionResponseDto(permission);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del permiso con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del permiso con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del permiso");
        }
    }

    @Override
    @Transactional
    public void deletePermission(Long id) throws OperationException {
        try {
            Permission permissionModel = permissionRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Permiso", id)));

            if (permissionModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Permiso", id));
            }

            if (rolePermissionRepository.hasActiveRolesByPermissionId(id)) {
                throw new OperationException(
                        "No se puede eliminar el permiso '" + permissionModel.getName() +
                        "' porque estÃ¡ asignado a roles activos. " +
                        "Desasigne el permiso de todos los roles antes de eliminarlo.");
            }

            permissionModel.setDeleted(true);
            permissionModel.setStatus(PermissionStatus.ELIMINATION);
            permissionRepository.save(permissionModel);
            log.info("Permiso con ID={} eliminado lÃ³gicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar permiso con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar permiso con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar el permiso");
        }
    }

}

