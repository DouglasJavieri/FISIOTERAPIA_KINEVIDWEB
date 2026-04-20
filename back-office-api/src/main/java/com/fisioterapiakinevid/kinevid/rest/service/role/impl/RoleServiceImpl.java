package com.fisioterapiakinevid.kinevid.rest.service.role.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.PagedRoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.role.RoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.role.RoleRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.ur.UserRoleRepository;
import com.fisioterapiakinevid.kinevid.rest.service.role.RoleService;
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
 * @created 02/03/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String PROTECTED_ADMIN_ROLE = "ROLE_ADMIN";

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;


    @Override
    @Transactional
    public RoleResponseDto createRole(RoleRequestDTO role) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre Rol", role.getName(), true, 75);
            ValidationUtil.throwExceptionIfInvalidText("DescripciÃ³n", role.getDescription(), true, 255);

            String normalizedName = role.getName().toUpperCase().strip();

            if (roleRepository.existsRoleByName(normalizedName)) {
                throw new OperationException("Ya existe un rol con el nombre: " + normalizedName);
            }

            Role roleModel = Role.builder()
                    .name(normalizedName)
                    .description(role.getDescription().strip())
                    .status(RoleStatus.ACTIVE)
                    .build();
            roleRepository.save(roleModel);
            return new RoleResponseDto(roleModel);
        } catch (OperationException e) {
            log.error("Error al crear rol: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear rol", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear el rol");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(Long id) throws OperationException {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", id)));

            if (role.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", id));
            }
            return new RoleResponseDto(role);
        } catch (OperationException e) {
            log.error("Error al buscar rol con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al buscar rol con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el rol");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PagedRoleResponseDto> findAllRoles(String status, Pageable pageable) throws OperationException {
        try {
            RoleStatus roleStatus = null;
            if (status != null && !status.isBlank()) {
                try {
                    roleStatus = RoleStatus.valueOf(status.toUpperCase().strip());
                } catch (IllegalArgumentException ex) {
                    throw new OperationException(
                            "Estado de rol invÃ¡lido: '" + status + "'. Valores vÃ¡lidos: ACTIVE, INACTIVE");
                }
            }
            return roleRepository.findAllRoles(roleStatus, pageable);

        } catch (OperationException e) {
            log.error("Error al listar roles: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar roles", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar roles");
        }
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(Long id, RoleRequestDTO role) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre Rol", role.getName(), true, 75);
            ValidationUtil.throwExceptionIfInvalidText("DescripciÃ³n", role.getDescription(), true, 255);

            Role roleModel = roleRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", id)));

            if (roleModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", id));
            }

            String normalizedName = role.getName().toUpperCase().strip();

            if (roleRepository.existsRoleByNameExcludingId(normalizedName, id)) {
                throw new OperationException("Ya existe un rol con el nombre: " + normalizedName);
            }

            roleModel.setName(normalizedName);
            roleModel.setDescription(role.getDescription().strip());
            roleRepository.save(roleModel);

            log.info("Rol actualizado: ID={}, Nombre={}", id, normalizedName);
            return new RoleResponseDto(roleModel);

        } catch (OperationException e) {
            log.error("Error al actualizar rol con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar rol con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar el rol");
        }
    }

    @Override
    @Transactional
    public RoleResponseDto changeStatus(Long id, RoleStatus status) throws OperationException {
        try {
            if (status == RoleStatus.ELIMINATION) {
                throw new OperationException(
                        "No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", id)));

            if (role.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", id));
            }

            if (role.getStatus() == status) {
                throw new OperationException(
                        "El rol ya se encuentra en el estado '" + status.getDescription() + "'.");
            }
            role.setStatus(status);
            roleRepository.save(role);
            log.info("Estado del rol ID={} cambiado a: {}", id, status);
            return new RoleResponseDto(role);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del rol con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del rol con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del rol");
        }
    }

    @Override
    @Transactional
    public void deleteRole(Long id) throws OperationException {
        try {
            Role roleModel = roleRepository.findById(id)
                    .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Rol", id)));

            if (roleModel.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Rol", id));
            }

            if (PROTECTED_ADMIN_ROLE.equalsIgnoreCase(roleModel.getName())) {
                throw new OperationException(
                        "El rol '" + PROTECTED_ADMIN_ROLE + "' es un rol del sistema y no puede ser eliminado.");
            }
            if (userRoleRepository.hasActiveUsersByRoleId(id)) {
                throw new OperationException(
                        "No se puede eliminar el rol '" + roleModel.getName() +
                        "' porque tiene usuarios activos asignados. " +
                        "Reasigne los usuarios antes de eliminar el rol.");
            }

            roleModel.setDeleted(true);
            roleModel.setStatus(RoleStatus.ELIMINATION);
            roleRepository.save(roleModel);
            log.info("Rol con ID={} eliminado lÃ³gicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar rol con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar rol con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar el rol");
        }
    }
}


