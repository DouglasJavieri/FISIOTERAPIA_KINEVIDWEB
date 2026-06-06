package com.fisioterapiakinevid.kinevid.rest.service.reporting.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.AnalysisPhoto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.BiomechanicalAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootprintAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootSide;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.service.reporting.ReportService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final FootAnalysisRepository footAnalysisRepository;

    @Override
    public byte[] generateFootAnalysisReport(Long footAnalysisId) throws OperationException {
        FootAnalysis fa = footAnalysisRepository.findByIdWithRelations(footAnalysisId)
                .orElseThrow(() -> new OperationException("Análisis no encontrado"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            // --- CABECERA ---
            document.add(new Paragraph("INFORME DE ANÁLISIS DE PISADA")
                    .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));
            document.add(new Paragraph("KINEVID - Clínica de Fisioterapia")
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // --- DATOS DEL PACIENTE ---
            Table patientTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30})).useAllAvailableWidth();
            patientTable.addCell(new Cell().add(new Paragraph("Paciente:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(fa.getClinicalSession().getEpisode().getPatient().getFullName())));
            patientTable.addCell(new Cell().add(new Paragraph("CI:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(fa.getClinicalSession().getEpisode().getPatient().getCi())));
            patientTable.addCell(new Cell().add(new Paragraph("Fecha de Análisis:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(fa.getAnalysisDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))));
            patientTable.addCell(new Cell().add(new Paragraph("Sesión N°:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(String.valueOf(fa.getClinicalSession().getSessionNumber()))));
            document.add(patientTable.setMarginBottom(15));

            // --- RESUMEN CLÍNICO ---
            document.add(new Paragraph("Resumen Clínico").setBold().setUnderline());
            document.add(new Paragraph("Diagnóstico: " + fa.getDiagnosis()).setMarginBottom(5));
            document.add(new Paragraph("Antecedentes: " + (fa.getRelevantBackground() != null ? fa.getRelevantBackground() : "N/A")));
            document.add(new Paragraph("Evaluación: " + (fa.getKinesiologicalEvaluation() != null ? fa.getKinesiologicalEvaluation() : "N/A")));
            document.add(new Paragraph("Observaciones: " + (fa.getObservations() != null ? fa.getObservations() : "N/A")));

            // --- MEDICIONES ANTROPOMÉTRICAS Y ÁNGULOS ---
            Table measurementsTable = new Table(UnitValue.createPercentArray(new float[]{30, 20, 30, 20})).useAllAvailableWidth();
            measurementsTable.addCell(new Cell().add(new Paragraph("Distancia Intermaleolar:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getDistanceIntermaleolar() != null ? fa.getDistanceIntermaleolar().toString() + " cm" : "N/A")));
            measurementsTable.addCell(new Cell().add(new Paragraph("Distancia Intercondílea:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getDistanceIntercondylar() != null ? fa.getDistanceIntercondylar().toString() + " cm" : "N/A")));
            
            measurementsTable.addCell(new Cell().add(new Paragraph("Ángulo Interno Izq.:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getAngleLeftInternal() != null ? fa.getAngleLeftInternal().toString() + "°" : "N/A")));
            measurementsTable.addCell(new Cell().add(new Paragraph("Ángulo Interno Der.:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getAngleRightInternal() != null ? fa.getAngleRightInternal().toString() + "°" : "N/A")));

            measurementsTable.addCell(new Cell().add(new Paragraph("Ángulo Externo Izq.:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getAngleLeftExternal() != null ? fa.getAngleLeftExternal().toString() + "°" : "N/A")));
            measurementsTable.addCell(new Cell().add(new Paragraph("Ángulo Externo Der.:")).setBold());
            measurementsTable.addCell(new Cell().add(new Paragraph(fa.getAngleRightExternal() != null ? fa.getAngleRightExternal().toString() + "°" : "N/A")));
            
            document.add(measurementsTable.setMarginBottom(15));

            // --- ANÁLISIS BIOMECÁNICO ---
            document.add(new Paragraph("Análisis Biomecánico").setBold().setUnderline());
            Table bioTable = new Table(UnitValue.createPercentArray(new float[]{30, 35, 35})).useAllAvailableWidth();
            bioTable.addHeaderCell(new Cell().add(new Paragraph("Parámetro")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            bioTable.addHeaderCell(new Cell().add(new Paragraph("Pie Izquierdo")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            bioTable.addHeaderCell(new Cell().add(new Paragraph("Pie Derecho")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));

            BiomechanicalAnalysis left = fa.getBiomechanicalAnalysis().stream().filter(b -> b.getFootSide() == FootSide.LEFT).findFirst().orElse(null);
            BiomechanicalAnalysis right = fa.getBiomechanicalAnalysis().stream().filter(b -> b.getFootSide() == FootSide.RIGHT).findFirst().orElse(null);

            addBioRow(bioTable, "Regla Maleolo Tibial", left != null ? left.getTibialMalleolarRule().toString() : "N/A", right != null ? right.getTibialMalleolarRule().toString() : "N/A");
            addBioRow(bioTable, "Marcha", left != null ? left.getGait().toString() : "N/A", right != null ? right.getGait().toString() : "N/A");
            addBioRow(bioTable, "Desgaste Calzado", left != null ? left.getShoeWear() : "N/A", right != null ? right.getShoeWear() : "N/A");
            addBioRow(bioTable, "Palpación Tibial", left != null ? left.getTibiaPalpation() : "N/A", right != null ? right.getTibiaPalpation() : "N/A");
            document.add(bioTable.setMarginBottom(15));

            // --- HUELLA PLANTAR ---
            if (!fa.getFootprintAnalysis().isEmpty()) {
                FootprintAnalysis fp = fa.getFootprintAnalysis().iterator().next();
                document.add(new Paragraph("Valoración de Huella Plantar").setBold().setUnderline());
                document.add(new Paragraph("Tipo de Huella: " + fp.getFootprintType().toString()));
                if (fp.getNotes() != null) document.add(new Paragraph("Notas: " + fp.getNotes()));
                document.add(new Paragraph(" ").setMarginBottom(10));
            }

            // --- GALERÍA DE IMÁGENES ---
            List<AnalysisPhoto> selectedPhotos = fa.getPhotos().stream().filter(AnalysisPhoto::getIsSelected).collect(Collectors.toList());
            if (!selectedPhotos.isEmpty()) {
                document.add(new AreaBreak());
                document.add(new Paragraph("Análisis de Imagen (Fotos Seleccionadas)").setBold().setUnderline().setMarginBottom(10));
                
                // Usar una tabla para organizar las fotos en 2 columnas
                Table imageGrid = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
                
                for (AnalysisPhoto photo : selectedPhotos) {
                    try {
                        ImageData data = ImageDataFactory.create(new URL(photo.getPhotoUrl()));
                        Image img = new Image(data).scaleToFit(250, 250).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                        Cell cell = new Cell().add(img).add(new Paragraph("Foto " + photo.getPhotoOrder()).setTextAlignment(TextAlignment.CENTER));
                        imageGrid.addCell(cell);
                    } catch (Exception e) {
                        imageGrid.addCell(new Cell().add(new Paragraph("Error al cargar imagen " + photo.getPhotoOrder())));
                        log.warn("No se pudo cargar imagen para el reporte: {}", photo.getPhotoUrl());
                    }
                }
                document.add(imageGrid);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar PDF iText 7", e);
            throw new OperationException("Error técnico al generar el documento PDF");
        }
    }

    private void addBioRow(Table table, String label, String leftVal, String rightVal) {
        table.addCell(new Cell().add(new Paragraph(label)).setBold());
        table.addCell(new Cell().add(new Paragraph(leftVal != null ? leftVal : "N/A")));
        table.addCell(new Cell().add(new Paragraph(rightVal != null ? rightVal : "N/A")));
    }
}
