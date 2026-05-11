package com.fisioterapiakinevid.kinevid.config.init;

import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.entity.rp.RolePermission;
import com.fisioterapiakinevid.kinevid.rest.model.entity.ur.UserRole;
import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.p.PermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.role.RoleRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.rp.RolePermissionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.u.UserRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.ur.UserRoleRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.svc.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private static final String ROLE_ROOT            = "ROLE_ROOT";
    private static final String ROLE_FISIOTERAPEUTA  = "ROLE_FISIOTERAPEUTA";
    private static final String ROLE_RECEPCIONISTA   = "ROLE_RECEPCIONISTA";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${kinevid.app.admin.username}")
    private String adminUsername;

    @Value("${kinevid.app.admin.email}")
    private String adminEmail;

    @Value("${kinevid.app.admin.password}")
    private String adminPassword;

    @Value("${kinevid.app.admin.role}")
    private String adminRoleName;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando DataLoader");

        try {
            List<Permission> permissions = createDefaultPermissions();

            Role adminRole = createAdminRole(permissions);
            createRootRole(permissions);
            createFisioterapeutaRole();
            createRecepcionistaRole();

            syncPermissionsToExistingFullAccessRoles(permissions);
            syncPermissionsToFisioterapeutaRole();
            syncPermissionsToRecepcionistaRole();

            User adminUser = createAdminUser();
            assignAdminRoleToUser(adminUser, adminRole);

            createDefaultServices();

            log.info("DataLoader completado exitosamente");

        } catch (Exception e) {
            log.error("Error en DataLoader", e);
            throw new RuntimeException("Error al cargar datos iniciales", e);
        }
    }

    private List<Permission> createDefaultPermissions() {
        log.info("Creando permisos por defecto");

        String[][] permissionsData = {
                {"CREATE_USER", "Crear usuario"},
                {"VIEW_USER", "Ver usuario"},
                {"UPDATE_USER", "Actualizar usuario"},
                {"DELETE_USER", "Eliminar usuario"},
                {"LIST_USER", "Listar usuarios"},
                {"CHANGE_USER_STATUS",  "Cambiar estado de usuario"},
                {"CREATE_ROLE", "Crear rol"},
                {"READ_ROLE", "Ver rol"},
                {"UPDATE_ROLE", "Actualizar rol"},
                {"DELETE_ROLE", "Eliminar rol"},
                {"LIST_ROLE", "Listar roles"},
                {"CHANGE_ROLE_STATUS", "Cambiar estado de rol"},
                {"CREATE_PERMISSION", "Crear permiso"},
                {"READ_PERMISSION", "Ver permiso"},
                {"UPDATE_PERMISSION", "Actualizar permiso"},
                {"DELETE_PERMISSION", "Eliminar permiso"},
                {"LIST_PERMISSION", "Listar permisos"},
                {"CHANGE_PERMISSION_STATUS", "Cambiar estado de permiso"},
                {"ASSIGN_PERMISSION_TO_ROLE", "Asignar permisos a rol"},
                {"REMOVE_PERMISSION_FROM_ROLE", "Remover permisos de rol"},
                {"CREATE_EMPLOYEE", "Crear empleado"},
                {"VIEW_EMPLOYEE", "Ver empleado"},
                {"UPDATE_EMPLOYEE", "Actualizar empleado"},
                {"DELETE_EMPLOYEE", "Eliminar empleado"},
                {"LIST_EMPLOYEE", "Listar empleados"},
                {"CHANGE_EMPLOYEE_STATUS", "Cambiar estado de empleado"},
                {"ASSIGN_USER_TO_EMPLOYEE", "Asignar usuario a empleado"},
                {"REMOVE_USER_FROM_EMPLOYEE", "Desvincular usuario de empleado"},
                // Pacientes
                {"CREATE_PATIENT", "Crear paciente"},
                {"VIEW_PATIENT", "Ver paciente"},
                {"UPDATE_PATIENT", "Actualizar paciente"},
                {"DELETE_PATIENT", "Eliminar paciente"},
                {"LIST_PATIENT", "Listar pacientes"},
                {"CHANGE_PATIENT_STATUS", "Cambiar estado de paciente"},
                //  Servicios del consultorio
                {"CREATE_SERVICE", "Crear servicio"},
                {"VIEW_SERVICE", "Ver servicio"},
                {"UPDATE_SERVICE", "Actualizar servicio"},
                {"DELETE_SERVICE", "Eliminar servicio"},
                {"LIST_SERVICE", "Listar servicios"},
                {"CHANGE_SERVICE_STATUS", "Cambiar estado de servicio"},
                // Episodios clínicos
                {"CREATE_EPISODE", "Abrir episodio clínico"},
                {"VIEW_EPISODE", "Ver episodio clínico"},
                {"CLOSE_EPISODE", "Cerrar episodio clínico (alta médica)"},
                {"LIST_EPISODE", "Listar episodios clínicos"},
                // Sesiones clínicas
                {"CREATE_CLINICAL_SESSION", "Crear sesión clínica"},
                {"VIEW_CLINICAL_SESSION", "Ver sesión clínica"},
                {"UPDATE_CLINICAL_SESSION", "Actualizar sesión clínica"},
                {"DELETE_CLINICAL_SESSION", "Eliminar sesión clínica"},
                {"LIST_CLINICAL_SESSION", "Listar sesiones clínicas"},
                {"MANAGE_SESSION_SERVICES", "Gestionar servicios de una sesión"},
        };

        for (String[] permData : permissionsData) {
            if (!permissionRepository.existsPermissionByName(permData[0])) {
                Permission permission = Permission.builder()
                        .name(permData[0])
                        .description(permData[1])
                        .status(PermissionStatus.ACTIVE)
                        .build();
                permissionRepository.save(permission);
                log.debug("Permiso creado: {}", permData[0]);
            }
        }

        return permissionRepository.findAll();
    }

    private Role createAdminRole(List<Permission> allPermissions) {
        log.info("Creando rol ADMIN");

        Role adminRole = roleRepository.findByName(adminRoleName)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(adminRoleName)
                            .description("Rol de administrador con acceso total")
                            .status(RoleStatus.ACTIVE)
                            .build();

                    Role savedRole = roleRepository.save(newRole);

                    for (Permission permission : allPermissions) {
                        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                                savedRole.getId(), permission.getId())) {
                            RolePermission rolePermission = RolePermission.builder()
                                    .role(savedRole)
                                    .permission(permission)
                                    .build();
                            rolePermissionRepository.save(rolePermission);
                        }
                    }

                    log.info("Rol ADMIN creado con {} permisos", allPermissions.size());
                    return savedRole;
                });

        return adminRole;
    }

    private User createAdminUser() {
        log.info("Creando usuario admin");

        User adminUser = userRepository.findByUsernameAuthentication(adminUsername)
                .orElseGet(() -> {
                    String hashedPassword = passwordEncoder.encode(adminPassword);

                    User newUser = User.builder()
                            .username(adminUsername)
                            .email(adminEmail)
                            .password(hashedPassword)
                            .status(UserStatus.ACTIVE)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    log.info("Usuario admin creado exitosamente");
                    return savedUser;
                });

        return adminUser;
    }

    private void assignAdminRoleToUser(User adminUser, Role adminRole) {
        log.info("Asignando rol ADMIN al usuario admin");

        if (!userRoleRepository.existsByUserIdAndRoleId(adminUser.getId(), adminRole.getId())) {
            UserRole userRole = UserRole.builder()
                    .user(adminUser)
                    .role(adminRole)
                    .build();
            userRoleRepository.save(userRole);
            log.info("Rol ADMIN asignado");
        }
    }

    private void createRootRole(List<Permission> allPermissions) {
        log.info("Verificando rol ROOT");

        roleRepository.findByName(ROLE_ROOT).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(ROLE_ROOT)
                    .description("Rol raÃ­z con acceso total al sistema")
                    .status(RoleStatus.ACTIVE)
                    .build();

            Role savedRole = roleRepository.save(newRole);

            for (Permission permission : allPermissions) {
                if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                        savedRole.getId(), permission.getId())) {
                    rolePermissionRepository.save(RolePermission.builder()
                            .role(savedRole)
                            .permission(permission)
                            .build());
                }
            }

            log.info("Rol ROOT creado con {} permisos", allPermissions.size());
            return savedRole;
        });
    }

    private void createFisioterapeutaRole() {
        log.info("Verificando rol FISIOTERAPEUTA");

        roleRepository.findByName(ROLE_FISIOTERAPEUTA).orElseGet(() -> {
            Role fisioRole = Role.builder()
                    .name(ROLE_FISIOTERAPEUTA)
                    .description("Rol para fisioterapeutas: acceso a pacientes, historial clinico y analisis de imagenes")
                    .status(RoleStatus.ACTIVE)
                    .build();

            Role savedRole = roleRepository.save(fisioRole);

            // Permisos base para el fisioterapeuta
            String[] fisioPerms = {
                    "LIST_PATIENT", "VIEW_PATIENT", "CREATE_PATIENT", "UPDATE_PATIENT",
                    "CHANGE_PATIENT_STATUS", "LIST_SERVICE", "VIEW_SERVICE", "CREATE_SERVICE",
                    "UPDATE_SERVICE", "DELETE_SERVICE", "CHANGE_SERVICE_STATUS",
                    // Episodios clínicos
                    "CREATE_EPISODE", "VIEW_EPISODE", "CLOSE_EPISODE", "LIST_EPISODE",
                    // Sesiones clínicas
                    "CREATE_CLINICAL_SESSION", "VIEW_CLINICAL_SESSION", "UPDATE_CLINICAL_SESSION",
                    "DELETE_CLINICAL_SESSION", "LIST_CLINICAL_SESSION", "MANAGE_SESSION_SERVICES",
            };
            for (String permName : fisioPerms) {
                permissionRepository.findByName(permName).ifPresent(permission -> {
                    if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                            savedRole.getId(), permission.getId())) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .role(savedRole)
                                .permission(permission)
                                .build());
                    }
                });
            }

            log.info("Rol FISIOTERAPEUTA creado con permisos de pacientes y servicios.");
            return savedRole;
        });
    }

    private void createRecepcionistaRole() {
        log.info("Verificando rol RECEPCIONISTA");

        roleRepository.findByName(ROLE_RECEPCIONISTA).orElseGet(() -> {
            Role recepRole = Role.builder()
                    .name(ROLE_RECEPCIONISTA)
                    .description("Rol para recepcionistas: registro de pacientes y consulta de servicios")
                    .status(RoleStatus.ACTIVE)
                    .build();

            Role savedRole = roleRepository.save(recepRole);

            // Permisos base para recepcionista
            String[] recepPerms = {
                    "LIST_PATIENT", "VIEW_PATIENT", "CREATE_PATIENT",
                    "LIST_SERVICE", "VIEW_SERVICE",
                    // Episodios clínicos (puede registrar visita y ver episodios)
                    "CREATE_EPISODE", "VIEW_EPISODE", "LIST_EPISODE",
            };
            for (String permName : recepPerms) {
                permissionRepository.findByName(permName).ifPresent(permission -> {
                    if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                            savedRole.getId(), permission.getId())) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .role(savedRole)
                                .permission(permission)
                                .build());
                    }
                });
            }

            log.info("Rol RECEPCIONISTA creado con permisos de consulta de pacientes y servicios.");
            return savedRole;
        });
    }

    private void syncPermissionsToExistingFullAccessRoles(List<Permission> allPermissions) {
        List<String> fullAccessRoles = List.of(adminRoleName, ROLE_ROOT);

        for (String roleName : fullAccessRoles) {
            roleRepository.findByName(roleName).ifPresent(role -> {
                int assigned = 0;
                for (Permission permission : allPermissions) {
                    if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                            role.getId(), permission.getId())) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .build());
                        assigned++;
                    }
                }
                if (assigned > 0) {
                    log.info("Sincronizados {} permisos nuevos al rol {}", assigned, roleName);
                }
            });
        }
    }

    /**
     * Sincroniza en cada arranque los permisos base del FISIOTERAPEUTA.
     * Permite agregar nuevos permisos al rol sin necesidad de borrar la BD.
     */
    private void syncPermissionsToFisioterapeutaRole() {
        String[] fisioPerms = {
                "LIST_PATIENT", "VIEW_PATIENT", "CREATE_PATIENT", "UPDATE_PATIENT",
                "CHANGE_PATIENT_STATUS", "LIST_SERVICE", "VIEW_SERVICE", "CREATE_SERVICE",
                "UPDATE_SERVICE", "DELETE_SERVICE", "CHANGE_SERVICE_STATUS",
                "CREATE_EPISODE", "VIEW_EPISODE", "CLOSE_EPISODE", "LIST_EPISODE",
                "CREATE_CLINICAL_SESSION", "VIEW_CLINICAL_SESSION", "UPDATE_CLINICAL_SESSION",
                "DELETE_CLINICAL_SESSION", "LIST_CLINICAL_SESSION", "MANAGE_SESSION_SERVICES",
        };
        syncPermissionsToRole(ROLE_FISIOTERAPEUTA, fisioPerms);
    }

    /**
     * Sincroniza en cada arranque los permisos base del RECEPCIONISTA.
     * Permite agregar nuevos permisos al rol sin necesidad de borrar la BD.
     */
    private void syncPermissionsToRecepcionistaRole() {
        String[] recepPerms = {
                "LIST_PATIENT", "VIEW_PATIENT", "CREATE_PATIENT",
                "LIST_SERVICE", "VIEW_SERVICE",
                "CREATE_EPISODE", "VIEW_EPISODE", "LIST_EPISODE",
                "LIST_CLINICAL_SESSION", "VIEW_CLINICAL_SESSION",
        };
        syncPermissionsToRole(ROLE_RECEPCIONISTA, recepPerms);
    }

    private void syncPermissionsToRole(String roleName, String[] permissionNames) {
        roleRepository.findByName(roleName).ifPresent(role -> {
            int assigned = 0;
            for (String permName : permissionNames) {
                permissionRepository.findByName(permName).ifPresent(permission -> {
                    if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                            role.getId(), permission.getId())) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .build());
                    }
                });
                assigned++;
            }
            log.info("Sync de permisos al rol {} completado ({} verificados)", roleName, assigned);
        });
    }

    private void createDefaultServices() {
        log.info("Creando servicios médicos iniciales");
        // ELECTROTERAPIA
        createServiceIfNotExists("Ultrasonido", "Terapia con ultrasonido terapéutico", ServiceCategory.ELECTROTHERAPY, 30, new BigDecimal("150.00"));
        createServiceIfNotExists("Magnetoterapia", "Tratamiento con campos magnéticos", ServiceCategory.ELECTROTHERAPY, 30, new BigDecimal("160.00"));
        createServiceIfNotExists("Laser", "Terapia con láser de baja potencia", ServiceCategory.ELECTROTHERAPY, 25, new BigDecimal("180.00"));
        createServiceIfNotExists("Terapia Combinada", "Combinación de técnicas electroterápicas", ServiceCategory.ELECTROTHERAPY, 40, new BigDecimal("200.00"));
        createServiceIfNotExists("Electroanalgesia", "Tratamiento del dolor mediante electroterapia", ServiceCategory.ELECTROTHERAPY, 25, new BigDecimal("140.00"));
        createServiceIfNotExists("Electroestimulación", "Estimulación muscular mediante corrientes eléctricas", ServiceCategory.ELECTROTHERAPY, 30, new BigDecimal("150.00"));
        // GIMNASIO TERAPÉUTICO
        createServiceIfNotExists("Fortalecimiento Muscular", "Ejercicios de fortalecimiento muscular dirigido", ServiceCategory.THERAPEUTIC_GYMNASIUM, 45, new BigDecimal("120.00"));
        createServiceIfNotExists("Reducción de Rangos de Movimiento", "Mejora de la amplitud articular", ServiceCategory.THERAPEUTIC_GYMNASIUM, 40, new BigDecimal("130.00"));
        createServiceIfNotExists("Reeducación Postural", "Corrección y educación de la postura", ServiceCategory.THERAPEUTIC_GYMNASIUM, 50, new BigDecimal("140.00"));
        createServiceIfNotExists("Reeducación de la Marcha", "Tratamiento de alteraciones en la marcha", ServiceCategory.THERAPEUTIC_GYMNASIUM, 45, new BigDecimal("135.00"));
        createServiceIfNotExists("Readaptación Deportiva", "Rehabilitación funcional para deportistas", ServiceCategory.THERAPEUTIC_GYMNASIUM, 60, new BigDecimal("170.00"));
        createServiceIfNotExists("Reacondicionamiento Físico", "Programa de acondicionamiento y recuperación", ServiceCategory.THERAPEUTIC_GYMNASIUM, 50, new BigDecimal("150.00"));
        createServiceIfNotExists("Estimulación Temprana", "Estimulación psicomotriz en etapas iniciales", ServiceCategory.THERAPEUTIC_GYMNASIUM, 40, new BigDecimal("110.00"));
        // TERMOTERAPIA
        createServiceIfNotExists("Calor Seco", "Aplicación de calor seco terapéutico", ServiceCategory.THERMOTHERAPY, 20, new BigDecimal("100.00"));
        createServiceIfNotExists("Calor Húmedo", "Aplicación de calor húmedo terapéutico", ServiceCategory.THERMOTHERAPY, 20, new BigDecimal("110.00"));
        createServiceIfNotExists("Crioterapia", "Aplicación de frío terapéutico", ServiceCategory.THERMOTHERAPY, 20, new BigDecimal("100.00"));
        createServiceIfNotExists("Terapia de Contraste", "Alternancia de calor y frío", ServiceCategory.THERMOTHERAPY, 30, new BigDecimal("120.00"));
        // TERAPIA MANUAL
        createServiceIfNotExists("Drenaje Linfático", "Drenaje linfático manual terapéutico", ServiceCategory.MANUAL_THERAPY, 45, new BigDecimal("160.00"));
        createServiceIfNotExists("Masoterapia", "Masaje terapéutico", ServiceCategory.MANUAL_THERAPY, 50, new BigDecimal("150.00"));
        createServiceIfNotExists("Liberación Miofascial", "Técnica de liberación de fascia muscular", ServiceCategory.MANUAL_THERAPY, 40, new BigDecimal("140.00"));
        createServiceIfNotExists("Técnicas de Energía Muscular", "PNF y técnicas de energía muscular", ServiceCategory.MANUAL_THERAPY, 35, new BigDecimal("130.00"));
        createServiceIfNotExists("Maderoterapia", "Tratamiento con herramientas de madera", ServiceCategory.MANUAL_THERAPY, 30, new BigDecimal("120.00"));
        createServiceIfNotExists("Terapia de Percusión", "Masaje percutivo terapéutico", ServiceCategory.MANUAL_THERAPY, 30, new BigDecimal("110.00"));
        // KINESIOTERAPIA
        createServiceIfNotExists("Vendaje Funcional", "Vendaje funcional y estabilización", ServiceCategory.KINESIOTHERAPY, 25, new BigDecimal("80.00"));
        createServiceIfNotExists("Kinesiotaping", "Aplicación de cinta kinesiológica", ServiceCategory.KINESIOTHERAPY, 30, new BigDecimal("90.00"));
        createServiceIfNotExists("Prescripción de Ortesis", "Evaluación y prescripción de ortesis", ServiceCategory.KINESIOTHERAPY, 40, new BigDecimal("100.00"));
        createServiceIfNotExists("Prescripción de Plantillas", "Evaluación y prescripción de plantillas personalizadas", ServiceCategory.KINESIOTHERAPY, 40, new BigDecimal("120.00"));
        // ANÁLISIS POSTURAL
        createServiceIfNotExists("Análisis de Pisada", "Análisis biomecánico de la pisada", ServiceCategory.POSTURAL_ANALYSIS, 60, new BigDecimal("200.00"));
        log.info("Servicios médicos iniciales creados exitosamente");
    }

    private void createServiceIfNotExists(String name, String description, ServiceCategory category, Integer durationMinutes, BigDecimal price) {
        if (!medicalServiceRepository.existsByName(name)) {
            MedicalService service = MedicalService.builder()
                    .name(name)
                    .description(description)
                    .category(category)
                    .durationMinutes(durationMinutes)
                    .price(price)
                    .status(ServiceStatus.ACTIVE)
                    .build();
            medicalServiceRepository.save(service);
            log.debug("Servicio creado: {} ({})", name, category.getDescription());
        }
    }
}
