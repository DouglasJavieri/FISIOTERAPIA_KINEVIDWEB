
package com.fisioterapiakinevid.kinevid.rest.model.dto.rp;

import com.fisioterapiakinevid.kinevid.rest.model.entity.rp.RolePermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponseDto {

    private Long id;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;

    public RolePermissionResponseDto(RolePermission rolePermission) {
        this.id = rolePermission.getId();
        this.roleId = rolePermission.getRole().getId();
        this.roleName = rolePermission.getRole().getName();
        this.permissionId = rolePermission.getPermission().getId();
        this.permissionName = rolePermission.getPermission().getName();
    }
}
