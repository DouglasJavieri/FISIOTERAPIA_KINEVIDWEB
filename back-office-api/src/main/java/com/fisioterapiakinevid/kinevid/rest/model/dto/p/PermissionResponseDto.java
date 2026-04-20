package com.fisioterapiakinevid.kinevid.rest.model.dto.p;

import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import lombok.*;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 03/03/2026
 * DTO de salida para operaciones sobre un solo permiso (create, update, getById).
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {
    private Long id;
    private String name;
    private String description;
    private String status;

    public PermissionResponseDto(Permission permission) {
        this.id = permission.getId();
        this.name = permission.getName();
        this.description = permission.getDescription();
        this.status = permission.getStatus() != null ? permission.getStatus().getValue() : null;
    }
}

