package com.university.grp20.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;

import java.io.*;

public class ExportLogService {

    public void exportLogToPDF(String logFileName, String outputPdfPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPdfPath));
            document.open();
            document.add(new Paragraph("User Operation Log\n\n"));
            // Reads the log file and adds contents to PDF
            try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    document.add(new Paragraph(line));
                }
            }
            document.close();
        } catch (DocumentException | IOException e) {
            showErrorAlert("Error exporting log to PDF: " + e.getMessage());
        }
    }

    public void exportLogToCSV(String logFileName, String outputCsvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFileName));
             FileWriter fw = new FileWriter(outputCsvPath);
             PrintWriter pw = new PrintWriter(fw)) {

            // Write CSV Header
            pw.println("Timestamp,Action");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[")) {  // Example log format: [YYYY-MM-DD HH:mm:ss] Action
                    int firstBracket = line.indexOf("[");
                    int secondBracket = line.indexOf("]");

                    if (firstBracket != -1 && secondBracket != -1) {
                        String timestamp = line.substring(firstBracket + 1, secondBracket).trim();
                        String action = line.substring(secondBracket + 2).trim();
                        pw.println(timestamp + "," + action);
                    }
                }
            }

            System.out.println("Log successfully exported to CSV: " + outputCsvPath);
        } catch (IOException e) {
            showErrorAlert("Error exporting log to CSV: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Export Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
