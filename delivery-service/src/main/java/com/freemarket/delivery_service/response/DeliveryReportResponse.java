package com.freemarket.delivery_service.response;

import com.freemarket.delivery_service.enums.ReportReason;
import com.freemarket.delivery_service.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DeliveryReportResponse {
    private Long idReport;
    private Long idDelivery;
    private Long idUsuario;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private String adminNote;
    private String imageBase64;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}