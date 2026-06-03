package com.freemarket.delivery_service.request;

import com.freemarket.delivery_service.enums.ReportReason;
import lombok.Data;

@Data
public class CreateReportRequest {
    private ReportReason reason;
    private String description;
    private String imageBase64;
}