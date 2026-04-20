package com.fisioterapiakinevid.kinevid.rest.model.entity.p;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
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
@Table(name = "permission")
public class Permission extends AuditableEntity implements Serializable {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_PERMISSION_ID_GENERATOR", sequenceName = "SEQ_PERMISSION_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PERMISSION_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "name_permission", length = 60)
    private String name;

    @Basic
    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_status")
    private PermissionStatus status;
}

