package com.fisioterapiakinevid.kinevid.rest.model.entity.rp;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_permission")
public class RolePermission extends AuditableEntity implements Serializable {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_ROLE_PERMISSION_ID_GENERATOR", sequenceName = "SEQ_ROLE_PERMISSION_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE_PERMISSION_ID_GENERATOR")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "id_permision", nullable = false)
    private Permission permission;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "id_role", nullable = false)
    private Role role;
}

