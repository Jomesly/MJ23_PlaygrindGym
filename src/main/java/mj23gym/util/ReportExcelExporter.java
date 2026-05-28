package mj23gym.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import mj23gym.dao.InventoryDAO;
import mj23gym.dao.MemberDAO;
import mj23gym.dao.PaymentDAO;
import mj23gym.dao.PosDAO;

public final class ReportExcelExporter {

    private ReportExcelExporter() {
    }

    public static void exportMembershipReport(List<PaymentDAO.PaymentSummaryRow> data, String filePath)
            throws IOException {
        String[] headers = {"Member", "Plan at Payment", "Amount Paid", "Method", "Date", "Status"};
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Membership Payments");
            CellStyle headerStyle = headerStyle(workbook);
            writeHeader(sheet, headers, headerStyle);
            int rowIndex = 1;
            for (PaymentDAO.PaymentSummaryRow row : data) {
                Row excelRow = sheet.createRow(rowIndex++);
                writeCell(excelRow, 0, row.memberName());
                writeCell(excelRow, 1, row.planTypeSnapshot());
                writeCell(excelRow, 2, row.amount());
                writeCell(excelRow, 3, row.paymentMethod());
                writeCell(excelRow, 4, row.paymentDate());
                writeCell(excelRow, 5, displayPaymentStatus(row.status()));
            }
            save(workbook, sheet, headers.length, filePath);
        }
    }

    public static void exportSalesReport(List<PosDAO.SaleDetailRow> data, String filePath)
            throws IOException {
        String[] headers = {"Date", "Item", "Qty Sold", "Unit Price", "Total", "Payment Method"};
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("POS Sales");
            CellStyle headerStyle = headerStyle(workbook);
            writeHeader(sheet, headers, headerStyle);
            int rowIndex = 1;
            for (PosDAO.SaleDetailRow row : data) {
                Row excelRow = sheet.createRow(rowIndex++);
                writeCell(excelRow, 0, row.saleDate());
                writeCell(excelRow, 1, row.itemName());
                writeCell(excelRow, 2, row.quantity());
                writeCell(excelRow, 3, row.unitPrice());
                writeCell(excelRow, 4, row.subtotal());
                writeCell(excelRow, 5, row.paymentMethod());
            }
            save(workbook, sheet, headers.length, filePath);
        }
    }

    public static void exportInventoryReport(List<InventoryDAO.InventoryRecord> data, String filePath)
            throws IOException {
        String[] headers = {"Code", "Item", "Category", "Stock", "Unit Price", "Stock Value", "Status"};
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory Stock");
            CellStyle headerStyle = headerStyle(workbook);
            writeHeader(sheet, headers, headerStyle);
            int rowIndex = 1;
            for (InventoryDAO.InventoryRecord row : data) {
                Row excelRow = sheet.createRow(rowIndex++);
                double stockValue = row.currentStock() * row.sellingPrice();
                writeCell(excelRow, 0, row.itemCode());
                writeCell(excelRow, 1, row.itemName());
                writeCell(excelRow, 2, row.category());
                writeCell(excelRow, 3, row.currentStock());
                writeCell(excelRow, 4, row.sellingPrice());
                writeCell(excelRow, 5, stockValue);
                writeCell(excelRow, 6, row.status());
            }
            save(workbook, sheet, headers.length, filePath);
        }
    }

    public static void exportAttendanceReport(List<MemberDAO.AttendanceRecord> data, String filePath)
            throws IOException {
        String[] headers = {"Date", "Member", "Member Code", "Session Type", "Check-in Time", "Notes"};
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");
            CellStyle headerStyle = headerStyle(workbook);
            writeHeader(sheet, headers, headerStyle);
            int rowIndex = 1;
            for (MemberDAO.AttendanceRecord row : data) {
                Row excelRow = sheet.createRow(rowIndex++);
                writeCell(excelRow, 0, row.attendanceDate());
                writeCell(excelRow, 1, row.memberName());
                writeCell(excelRow, 2, row.memberCode());
                writeCell(excelRow, 3, row.sessionType());
                writeCell(excelRow, 4, row.timeIn());
                writeCell(excelRow, 5, row.notes());
            }
            save(workbook, sheet, headers.length, filePath);
        }
    }

    private static CellStyle headerStyle(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private static void writeHeader(Sheet sheet, String[] headers, CellStyle style) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void writeCell(Row row, int column, Object value) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static void save(XSSFWorkbook workbook, Sheet sheet, int columnCount, String filePath)
            throws IOException {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
        Path output = Path.of(filePath);
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        try (FileOutputStream out = new FileOutputStream(output.toFile())) {
            workbook.write(out);
        }
    }

    private static String displayPaymentStatus(String raw) {
        return "Completed".equalsIgnoreCase(raw) ? "Paid" : raw;
    }
}
