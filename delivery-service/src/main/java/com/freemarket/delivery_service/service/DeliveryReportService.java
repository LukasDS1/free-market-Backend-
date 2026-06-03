package com.freemarket.delivery_service.service;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.enums.ReportStatus;
import com.freemarket.delivery_service.exception.NotFoundException;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryReport;
import com.freemarket.delivery_service.repository.DeliveryReportRepository;
import com.freemarket.delivery_service.repository.DeliveryRepository;
import com.freemarket.delivery_service.request.CreateReportRequest;
import com.freemarket.delivery_service.request.UpdateReportRequest;
import com.freemarket.delivery_service.response.DeliveryReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryReportService {

    private final DeliveryReportRepository reportRepository;
    private final DeliveryRepository deliveryRepository;


    public DeliveryReportResponse createReport(Long idDelivery, Long idUsuario, CreateReportRequest request) {

        Delivery delivery = deliveryRepository.findById(idDelivery)
                .orElseThrow(() -> new NotFoundException("Delivery not found"));

        if (!delivery.getStatus().equals(DeliveryStatus.ENTREGADO)) {
            throw new IllegalArgumentException("Only delivered orders can be reported");
        }

        if (!delivery.getDeliveryDetails().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("This delivery does not belong to the user");
        }

        if (reportRepository.existsByIdDeliveryAndIdUsuario(idDelivery, idUsuario)) {
            throw new IllegalArgumentException("A report already exists for this delivery");
        }

        LocalDateTime now = LocalDateTime.now();

        DeliveryReport report = new DeliveryReport();
        report.setIdDelivery(idDelivery);
        report.setIdUsuario(idUsuario);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setImageBase64(request.getImageBase64());
        report.setStatus(ReportStatus.ABIERTO);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        return toResponse(reportRepository.save(report));
    }


    public List<DeliveryReportResponse> getReportsByUsuario(Long idUsuario) {
        return reportRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public List<DeliveryReportResponse> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public List<DeliveryReportResponse> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public DeliveryReportResponse updateReport(Long idReport, UpdateReportRequest request) {
        DeliveryReport report = reportRepository.findById(idReport)
                .orElseThrow(() -> new NotFoundException("Report not found"));

        if (request.getStatus() != null)    report.setStatus(request.getStatus());
        if (request.getAdminNote() != null) report.setAdminNote(request.getAdminNote());
        report.setUpdatedAt(LocalDateTime.now());

        return toResponse(reportRepository.save(report));
    }


    private DeliveryReportResponse toResponse(DeliveryReport report) {
        return new DeliveryReportResponse(
                report.getIdReport(),
                report.getIdDelivery(),
                report.getIdUsuario(),
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getAdminNote(),
                report.getImageBase64(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}