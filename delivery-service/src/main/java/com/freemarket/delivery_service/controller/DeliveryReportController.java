package com.freemarket.delivery_service.controller;

import com.freemarket.delivery_service.enums.ReportStatus;
import com.freemarket.delivery_service.request.CreateReportRequest;
import com.freemarket.delivery_service.request.UpdateReportRequest;
import com.freemarket.delivery_service.response.DeliveryReportResponse;
import com.freemarket.delivery_service.service.DeliveryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api-v1/delivery/reports")
@RequiredArgsConstructor
@Tag(name = "Delivery Reports", description = "Endpoints for delivery issue reporting and ticket management")
public class DeliveryReportController {

    private final DeliveryReportService reportService;


    @PostMapping("/{idDelivery}")
    @Operation(summary = "Create report", description = "User creates a report for a delivered order they claim not to have received")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Report created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryReportResponse.class))),
        @ApiResponse(responseCode = "400", description = "Delivery is not in ENTREGADO status or report already exists",
            content = @Content(examples = @ExampleObject(value = "Only delivered orders can be reported"))),
        @ApiResponse(responseCode = "404", description = "Delivery not found",
            content = @Content(examples = @ExampleObject(value = "Delivery not found")))
    })
    public ResponseEntity<DeliveryReportResponse> createReport(
            @Parameter(description = "Delivery ID to report", example = "5", required = true)
            @PathVariable Long idDelivery,
            @RequestHeader("X-User-Id") Long idUsuario,
            @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.createReport(idDelivery, idUsuario, request));
    }

    @GetMapping("/usuario")
    @Operation(summary = "Get my reports", description = "Returns all reports created by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    public ResponseEntity<List<DeliveryReportResponse>> getMyReports(
            @RequestHeader("X-User-Id") Long idUsuario) {
        return ResponseEntity.ok(reportService.getReportsByUsuario(idUsuario));
    }


    @GetMapping("/all")
    @Operation(summary = "Get all reports", description = "Admin: returns all reports in the system")
    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    public ResponseEntity<List<DeliveryReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get reports by status", description = "Admin: filters reports by their current status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value",
            content = @Content(examples = @ExampleObject(value = "Invalid value for status. Accepted: ABIERTO, EN_REVISION, RESUELTO, CERRADO")))
    })
    public ResponseEntity<List<DeliveryReportResponse>> getReportsByStatus(
            @Parameter(description = "Status to filter by", example = "ABIERTO",
                schema = @Schema(allowableValues = {"ABIERTO", "EN_REVISION", "RESUELTO", "CERRADO"}))
            @PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @PatchMapping("/{idReport}")
    @Operation(summary = "Update report", description = "Admin: updates the status of a report and optionally adds a resolution note")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Report updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeliveryReportResponse.class))),
        @ApiResponse(responseCode = "404", description = "Report not found",
            content = @Content(examples = @ExampleObject(value = "Report not found")))
    })
    public ResponseEntity<DeliveryReportResponse> updateReport(
            @Parameter(description = "Report ID to update", example = "1", required = true)
            @PathVariable Long idReport,
            @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.updateReport(idReport, request));
    }
}