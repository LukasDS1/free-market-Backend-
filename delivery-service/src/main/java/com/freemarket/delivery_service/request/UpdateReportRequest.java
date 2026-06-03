package com.freemarket.delivery_service.request;

import com.freemarket.delivery_service.enums.ReportStatus;
import lombok.Data;

@Data
public class UpdateReportRequest {
    private ReportStatus status;
    private String adminNote;
}