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
      // Reads the log file and adds contents to PDF line by line
      try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
          document.add(new Paragraph(currentLine));
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
      pw.println("Timestamp,Action");
      String currentLine;
      while ((currentLine = br.readLine()) != null) {
        if (currentLine.startsWith("[")) {
          int firstBracket = currentLine.indexOf("[");
          int secondBracket = currentLine.indexOf("]");
          if (firstBracket != -1 && secondBracket != -1) {
            String timestamp = currentLine.substring(firstBracket + 1, secondBracket).trim();
            String action = currentLine.substring(secondBracket + 2).trim();
            pw.println(timestamp + "," + action);
          }
        }
      }
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
