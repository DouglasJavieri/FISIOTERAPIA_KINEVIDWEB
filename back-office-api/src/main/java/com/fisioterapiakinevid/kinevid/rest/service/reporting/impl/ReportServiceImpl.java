package com.fisioterapiakinevid.kinevid.rest.service.reporting.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
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
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

        ClinicalSession session = fa.getClinicalSession();
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            
            // Evento para Pie de Página (Usuario y Paginación)
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler(currentUsername));

            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(60, 40, 60, 40);

            // --- LOGO Y CABECERA ---
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{15, 70, 15})).useAllAvailableWidth();
            headerTable.setMarginTop(-30); // Elevar la cabecera completa
            
            // Logo Izquierdo
            try {
                InputStream is = getClass().getResourceAsStream("/images/logo_kinevid_2.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    ImageData logoData = ImageDataFactory.create(bytes);
                    Image logo = new Image(logoData).scaleToFit(60, 60)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT);
                    headerTable.addCell(new Cell().add(logo).setBorder(null));
                } else {
                    headerTable.addCell(new Cell().setBorder(null));
                }
            } catch (Exception e) {
                log.warn("No se pudo cargar el logo izquierdo: {}", e.getMessage());
                headerTable.addCell(new Cell().setBorder(null));
            }

            // Título Central
            Cell titleCell = new Cell().add(new Paragraph("INFORME DE ANÁLISIS DE PISADA")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("KINEVID - Clínica de Fisioterapia")
                    .setTextAlignment(TextAlignment.CENTER).setFontSize(10))
                    .setBorder(null).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            // Logo Derecho
            try {
                InputStream is = getClass().getResourceAsStream("/images/logo_kinevid.png");
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    ImageData logoData = ImageDataFactory.create(bytes);
                    Image logo = new Image(logoData).scaleToFit(60, 60)
                            .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                    headerTable.addCell(new Cell().add(logo).setBorder(null));
                } else {
                    headerTable.addCell(new Cell().setBorder(null));
                }
            } catch (Exception e) {
                headerTable.addCell(new Cell().setBorder(null));
            }

            document.add(headerTable.setMarginBottom(10));

            // --- INFORMACIÓN DE LA SESIÓN (Encima de Resumen Clínico) ---
            Table sessionInfoTable = new Table(UnitValue.createPercentArray(new float[]{25, 75})).useAllAvailableWidth();
            sessionInfoTable.addCell(new Cell().add(new Paragraph("Fisioterapeuta:")).setBold().setBorder(null));
            sessionInfoTable.addCell(new Cell().add(new Paragraph(session.getEmployee().getFullName())).setBorder(null));
            
            sessionInfoTable.addCell(new Cell().add(new Paragraph("Fecha de Sesión:")).setBold().setBorder(null));
            sessionInfoTable.addCell(new Cell().add(new Paragraph(session.getSessionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))).setBorder(null));
            
            String servicesText = session.getServices().stream()
                    .map(s -> s.getMedicalService().getName())
                    .collect(Collectors.joining(", "));
            sessionInfoTable.addCell(new Cell().add(new Paragraph("Servicios Aplicados:")).setBold().setBorder(null));
            sessionInfoTable.addCell(new Cell().add(new Paragraph(servicesText.isEmpty() ? "N/A" : servicesText)).setBorder(null));
            
            sessionInfoTable.addCell(new Cell().add(new Paragraph("Motivo Consulta:")).setBold().setBorder(null));
            sessionInfoTable.addCell(new Cell().add(new Paragraph(session.getReasonForConsultation() != null ? session.getReasonForConsultation() : "N/A")).setBorder(null));
            
            document.add(sessionInfoTable.setMarginBottom(15));

            // --- DATOS DEL PACIENTE ---
            document.add(new Paragraph("Datos del Paciente").setBold().setUnderline().setMarginBottom(5));
            Table patientTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30})).useAllAvailableWidth();
            patientTable.addCell(new Cell().add(new Paragraph("Paciente:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(session.getEpisode().getPatient().getFullName())));
            patientTable.addCell(new Cell().add(new Paragraph("CI:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(session.getEpisode().getPatient().getCi())));
            patientTable.addCell(new Cell().add(new Paragraph("Edad:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(String.valueOf(session.getEpisode().getPatient().getAge()))));
            patientTable.addCell(new Cell().add(new Paragraph("Sesión N°:")).setBold());
            patientTable.addCell(new Cell().add(new Paragraph(String.valueOf(session.getSessionNumber()))));
            document.add(patientTable.setMarginBottom(15));

            // --- RESUMEN CLÍNICO ---
            document.add(new Paragraph("Resumen Clínico").setBold().setUnderline());
            document.add(new Paragraph("Diagnóstico: " + fa.getDiagnosis()).setMarginBottom(5));
            
            Paragraph backgroundPara = new Paragraph().add(new Text("Antecedentes: ").setBold())
                    .add(new Text(fa.getRelevantBackground() != null ? fa.getRelevantBackground() : "N/A"));
            document.add(backgroundPara);
            
            Paragraph evalPara = new Paragraph().add(new Text("Evaluación: ").setBold())
                    .add(new Text(fa.getKinesiologicalEvaluation() != null ? fa.getKinesiologicalEvaluation() : "N/A"))
                    .setMarginBottom(15);
            document.add(evalPara);

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
                document.add(new Paragraph("Tipo de Huella: " + fp.getFootprintType().toString()).setMarginBottom(5));

                // MEDICIONES (Se mueven aquí por solicitud del usuario)
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

                document.add(measurementsTable.setMarginBottom(5));
                
                Paragraph obsPara = new Paragraph().add(new Text("Observaciones: ").setBold())
                        .add(new Text(fa.getObservations() != null ? fa.getObservations() : "N/A"))
                        .setMarginBottom(15);
                document.add(obsPara);
            }

            // --- GALERÍA DE IMÁGENES ---
            List<AnalysisPhoto> selectedPhotos = fa.getPhotos().stream().filter(AnalysisPhoto::getIsSelected).collect(Collectors.toList());
            if (!selectedPhotos.isEmpty()) {
                document.add(new Paragraph("Análisis de Imagen (Fotos Seleccionadas)").setBold().setUnderline().setMarginBottom(10));
                
                Table imageGrid = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
                
                for (AnalysisPhoto photo : selectedPhotos) {
                    try {
                        ImageData data = ImageDataFactory.create(new URL(photo.getPhotoUrl()));
                        Image img = new Image(data).scaleToFit(250, 250).setHorizontalAlignment(HorizontalAlignment.CENTER);
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

    // --- MANEJADOR PARA PIE DE PÁGINA ---
    private static class FooterHandler implements IEventHandler {
        private final String username;

        public FooterHandler(String username) {
            this.username = username;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);

            float y = 20;

            // Usuario al lado izquierdo
            canvas.showTextAligned(new Paragraph("Usuario: " + username).setFontSize(9),
                    40, y, TextAlignment.LEFT);

            // Paginación al lado derecho
            int pageNum = pdf.getPageNumber(page);
            int totalPages = pdf.getNumberOfPages();
            canvas.showTextAligned(new Paragraph(String.format("Página %d de %d", pageNum, totalPages)).setFontSize(9),
                    pageSize.getWidth() - 40, y, TextAlignment.RIGHT);

            canvas.close();
        }
    }
}
