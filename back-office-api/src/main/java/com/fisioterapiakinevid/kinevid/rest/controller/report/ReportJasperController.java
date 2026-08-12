package com.fisioterapiakinevid.kinevid.rest.controller.report;

import com.fisioterapiakinevid.kinevid.rest.core.report.ArchivoPojo;
import com.fisioterapiakinevid.kinevid.rest.core.report.FormatoReporteEnum;
import com.fisioterapiakinevid.kinevid.rest.core.report.ReportEnum;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.report.ReportErrorResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UserResponseDto;
import com.fisioterapiakinevid.kinevid.rest.service.report.ReportJasperService;
import com.fisioterapiakinevid.kinevid.rest.service.u.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;


/*
 *----------------------------------------
 *   Código de Aplicación:
 *   Código de Objeto:
 *   Descripción:
 *   Author Prog: Jorge Luis Choque Callizaya
 *----------------------------------------
 *   Fecha | Autor | Comentario
 *   12.08.2026 | Jorge Luis Choque Callizaya | Creación Inicial
 *----------------------------------------
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/reports")
public class ReportJasperController {

    @Autowired
    private ReportJasperService reportJasperService;

    @Autowired
    private UserService userService;

    @GetMapping(value= "/report-user", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('REPORT_USER')")
    public ResponseEntity reportUser() {
        try {
            List<UserResponseDto> list = userService.getListUserReport();
            ArchivoPojo archivoPojo = reportJasperService.generarReporte(ReportEnum.REPORT_USER, list, FormatoReporteEnum.PDF, null);
            return ok().body(archivoPojo);
        } catch (OperationException e) {
            log.error("Error: Se produjo un error controlado al ejecutar el servicio, Mensaje: {}", e.getMessage());
            ReportErrorResponseDto badResp = ReportErrorResponseDto.builder()
                    .code(HttpStatus.CONFLICT.toString())
                    .message("Error al Generar el Reporte, mensaje: " +e.getMessage())
                    .build();
            return badRequest().body(badResp);
        } catch (Exception e) {
            log.error("Error: Se produjo un error inesperado al ejecutar el servicio", e);
            ReportErrorResponseDto badResp = ReportErrorResponseDto.builder()
                    .code(HttpStatus.CONFLICT.toString())
                    .message("Error al Generar el Reporte, mensaje: " +e.getMessage())
                    .build();
            return badRequest().body(badResp);
        }
    }
}
