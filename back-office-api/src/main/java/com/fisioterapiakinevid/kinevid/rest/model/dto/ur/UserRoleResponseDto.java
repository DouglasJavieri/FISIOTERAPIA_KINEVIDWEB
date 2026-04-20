package com.fisioterapiakinevid.kinevid.rest.model.dto.ur;

import com.fisioterapiakinevid.kinevid.rest.model.entity.ur.UserRole;
import lombok.*;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO de salida para operaciones sobre asignaciÃ³n rol-usuario.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private Long roleId;
    private String roleName;

    public UserRoleResponseDto(UserRole userRole) {
        this.id = userRole.getId();
        this.userId = userRole.getUser().getId();
        this.username = userRole.getUser().getUsername();
        this.roleId = userRole.getRole().getId();
        this.roleName = userRole.getRole().getName();
    }
}


