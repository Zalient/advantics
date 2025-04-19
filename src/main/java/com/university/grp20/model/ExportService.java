package com.university.grp20.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import java.io.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportService {

  private static final Logger logger = LogManager.getLogger(ExportService.class);

  public static void dashboardToPDF(MetricsDTO metricsDTO, String filePath) throws IOException {

    if (filePath == null) {
      logger.info("User cancelled export");
      return;
    }

    if (metricsDTO == null) {
      logger.error("Cannot export to PDF: metricsDTO is null.");
      return;
    }

    File file = new File(filePath);
    logger.info("Save PDF to " + filePath);

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        float margin = 50;
        float yTop = page.getMediaBox().getHeight() - margin;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 18);
        contentStream.newLineAtOffset(margin, yTop);
        contentStream.showText("Metrics Dashboard");
        contentStream.endText();

        float tableTopY = yTop - 20;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 25;
        float colWidth = tableWidth / 2;
        float nextY = tableTopY;

        drawRow(contentStream, margin, nextY, colWidth, "Metric", "Value");
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Impressions",
            String.valueOf(metricsDTO.impressions()));
        nextY -= rowHeight;

        drawRow(
            contentStream, margin, nextY, colWidth, "Clicks", String.valueOf(metricsDTO.clicks()));
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Uniques",
            String.valueOf(metricsDTO.uniques()));
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Bounces",
            String.valueOf(metricsDTO.bounces()));
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Conversions",
            String.valueOf(metricsDTO.conversions()));
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Total Cost",
            String.valueOf(metricsDTO.totalCost()));
        nextY -= rowHeight;

        drawRow(contentStream, margin, nextY, colWidth, "CTR", String.valueOf(metricsDTO.ctr()));
        nextY -= rowHeight;

        drawRow(contentStream, margin, nextY, colWidth, "CPA", String.valueOf(metricsDTO.cpa()));
        nextY -= rowHeight;

        drawRow(contentStream, margin, nextY, colWidth, "CPC", String.valueOf(metricsDTO.cpc()));
        nextY -= rowHeight;

        drawRow(contentStream, margin, nextY, colWidth, "CPM", String.valueOf(metricsDTO.cpm()));
        nextY -= rowHeight;

        drawRow(
            contentStream,
            margin,
            nextY,
            colWidth,
            "Bounce Rate",
            String.valueOf(metricsDTO.bounceRate()));
      }
      document.save(file);
      System.out.println("Exported metrics to PDF: " + filePath);
    }
  }

  public static void dashboardToCSV(MetricsDTO metricsDTO, String filePath) throws IOException {

    if (filePath == null) {
      logger.info("User cancelled export");
      return;
    }

    File file = new File(filePath);
    logger.info("Save CSV to " + filePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

      writer.write("Metric,Value");
      writer.newLine();
      writer.write("Impressions, " + metricsDTO.impressions());
      writer.newLine();
      writer.write("Clicks, " + metricsDTO.clicks());
      writer.newLine();
      writer.write("Uniques, " + metricsDTO.uniques());
      writer.newLine();
      writer.write("Bounces, " + metricsDTO.bounces());
      writer.newLine();
      writer.write("Conversions, " + metricsDTO.conversions());
      writer.newLine();
      writer.write("Total Cost, " + metricsDTO.totalCost());
      writer.newLine();
      writer.write("CTR, " + metricsDTO.ctr());
      writer.newLine();
      writer.write("CPA, " + metricsDTO.cpa());
      writer.newLine();
      writer.write("CPC, " + metricsDTO.cpc());
      writer.newLine();
      writer.write("CPM, " + metricsDTO.cpm());
      writer.newLine();
      writer.write("Bounce Rate, " + metricsDTO.bounceRate());
      writer.newLine();
    }
    System.out.println("Exported metrics to CSV: " + filePath);
  }

  private static void drawRow(
      PDPageContentStream contentStream,
      float x,
      float y,
      float colWidth,
      String name,
      String value)
      throws IOException {
    contentStream.beginText();
    contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
    contentStream.newLineAtOffset(x + 5, y - 14);
    contentStream.showText(name);
    contentStream.endText();

    contentStream.beginText();
    contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
    contentStream.newLineAtOffset(x + colWidth + 5, y - 14);
    contentStream.showText(value);
    contentStream.endText();
  }

  public static void chartToPDF(JFreeChart chart, String filePath) throws IOException {

    if (filePath == null) {
      logger.info("User cancelled export");
      return;
    }

    int width = 1050;
    int height = 700;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

    chart.draw(g2d, new Rectangle(0, 0, width, height));
    g2d.dispose();

    try {
      File tempImage = File.createTempFile("tempChart", ".png");
      ChartUtils.saveChartAsPNG(tempImage, chart, width, height);

      PDDocument document = new PDDocument();
      PDPage page = new PDPage();
      PDRectangle originalMediaBox = page.getMediaBox();

      float mediaWidth = originalMediaBox.getWidth();
      float mediaHeight = originalMediaBox.getHeight();

      PDRectangle newMediaBox = new PDRectangle(mediaWidth * 1.8f, mediaHeight);
      page.setMediaBox(newMediaBox);
      document.addPage(page);
      PDImageXObject pdImageXObject =
          PDImageXObject.createFromFile(tempImage.getAbsolutePath(), document);

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        float imageWidth = pdImageXObject.getWidth();
        float imageHeight = pdImageXObject.getHeight();
        float x = (page.getMediaBox().getWidth() - imageWidth) / 2;
        float y = (page.getMediaBox().getHeight() - imageHeight) / 2;
        contentStream.drawImage(pdImageXObject, x, y, imageWidth, imageHeight);
      }
      document.save(filePath);
      logger.info("Save PDF to " + filePath);

      document.close();
      tempImage.delete();
      System.out.println("Exported to PDF: " + tempImage.getAbsolutePath());

    } catch (IOException e) {
      logger.error("Error converting chart to PDF: " + e);
    }
  }

  public static void chartToCSV(JFreeChart chart, String filePath) throws IOException {

    if (filePath == null) {
      logger.info("User cancelled export");
      return;
    }

    File file = new File(filePath);
    logger.info("Save CSV to " + filePath);

    Plot plot = chart.getPlot();
    if (plot instanceof CategoryPlot categoryPlot) {
      CategoryDataset categoryDataset = categoryPlot.getDataset();
      if (categoryDataset == null) {
        throw new IllegalStateException("Category dataset is null");
      }
      exportCategoryDataset(categoryDataset, file);
    } else if (plot instanceof XYPlot xyPlot) {
      XYDataset xyDataset = xyPlot.getDataset();
      if (xyDataset == null) {
        throw new IllegalStateException("XYdataset is null");
      }
      exportXYDataset(xyDataset, file);
    } else {
      throw new IllegalStateException("chartToCSV only supports CategoryPlot or XYPlot(Histogram)");
    }
    System.out.println("Exported to CSV: " + filePath);
  }

  private static void exportCategoryDataset(CategoryDataset dataset, File file) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write("Date,Metric,Value\n");

      int rowCount = dataset.getRowCount();
      int columnCount = dataset.getColumnCount();
      for (int row = 0; row < rowCount; row++) {
        String seriesName = dataset.getRowKey(row).toString();
        for (int col = 0; col < columnCount; col++) {
          String categoryName = dataset.getColumnKey(col).toString();
          Number value = dataset.getValue(row, col);
          writer.write(categoryName + "," + seriesName + "," + (value == null ? "0" : value));
          writer.newLine();
        }
      }
    }
  }

  private static void exportXYDataset(XYDataset dataset, File file) throws IOException {
    if (dataset instanceof HistogramDataset histogramDataset) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write("StartX,EndX,Frequency\n");

        int itemCount = histogramDataset.getItemCount(0);
        for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
          double startX = histogramDataset.getStartXValue(0, itemIndex);
          double endX = histogramDataset.getEndXValue(0, itemIndex);
          double value = histogramDataset.getYValue(0, itemIndex);

          writer.write(startX + "," + endX + "," + value);
          writer.newLine();
        }
      }
    }
  }

  public static String askForPDFFilename() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));
    Stage stage = new Stage();
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      return file.getAbsolutePath().endsWith(".pdf")
          ? file.getAbsolutePath()
          : file.getAbsolutePath() + ".pdf";
    }
    return null;
  }

  public static String askForCSVFilename() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
    Stage stage = new Stage();
    File file = fileChooser.showSaveDialog(stage);
    if (file != null) {
      return file.getAbsolutePath().endsWith(".csv")
          ? file.getAbsolutePath()
          : file.getAbsolutePath() + ".csv";
    }
    return null;
  }

    public static void exportLogToPDF(String logFileName, String outputPdfPath) {
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

    public static void exportLogToCSV(String logFileName, String outputCsvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFileName)); FileWriter fw = new FileWriter(outputCsvPath); PrintWriter pw = new PrintWriter(fw)) {
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

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Export Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
