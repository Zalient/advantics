package com.university.grp20.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportService {

    public static void dashboardToPDF(MetricsDTO metricsDTO) throws IOException {
        String filePath = askUserForFilename();
        if (filePath == null) return;
        if (!filePath.endsWith(".pdf")) {
            filePath += ".pdf";
        }
        File file = new File(filePath);

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
                float tableWidth = page.getMediaBox().getWidth() - 2*margin;
                float rowHeight = 25;
                float colWidth = tableWidth / 2;
                float nextY = tableTopY;

                drawRow(contentStream,margin,nextY,colWidth, "Metric","Value");
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Impressions", String.valueOf(metricsDTO.getImpressions()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Clicks", String.valueOf(metricsDTO.getClicks()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Uniques", String.valueOf(metricsDTO.getUniques()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Bounces", String.valueOf(metricsDTO.getBounces()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Conversions", String.valueOf(metricsDTO.getConversions()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Total Cost", String.valueOf(metricsDTO.getTotalCost()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "CTR", String.valueOf(metricsDTO.getCtr()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "CPA", String.valueOf(metricsDTO.getCpa()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "CPC", String.valueOf(metricsDTO.getCpc()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "CPM", String.valueOf(metricsDTO.getCpm()));
                nextY -= rowHeight;

                drawRow(contentStream, margin, nextY, colWidth,
                        "Bounce Rate", String.valueOf(metricsDTO.getBounceRate()));
            }
            document.save(file);
            System.out.println("Exported metrics to PDF: "+filePath);

        }
    }

    public static void dashboardToCSV(MetricsDTO metricsDTO) throws IOException {

        String filePath = askUserForFilename();
        if (filePath == null) return;
        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("Metric,Value");
            writer.newLine();
            writer.write("Impressions, " + metricsDTO.getImpressions());
            writer.newLine();
            writer.write("Clicks, " + metricsDTO.getClicks());
            writer.newLine();
            writer.write("Uniques, " + metricsDTO.getUniques());
            writer.newLine();
            writer.write("Bounces, " + metricsDTO.getBounces());
            writer.newLine();
            writer.write("Conversions, " + metricsDTO.getConversions());
            writer.newLine();
            writer.write("Total Cost, " + metricsDTO.getTotalCost());
            writer.newLine();
            writer.write("CTR, " + metricsDTO.getCtr());
            writer.newLine();
            writer.write("CPA, " + metricsDTO.getCpa());
            writer.newLine();
            writer.write("CPC, " + metricsDTO.getCpc());
            writer.newLine();
            writer.write("CPM, " + metricsDTO.getCpm());
            writer.newLine();
            writer.write("Bounce Rate, " + metricsDTO.getBounceRate());
            writer.newLine();
        }
        System.out.println("Exported metrics to CSV: "+filePath);
    }

    private static void drawRow(PDPageContentStream contentStream, float x, float y,
                                      float colWidth, String name, String value) throws IOException
    {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
        contentStream.newLineAtOffset(x+5, y-14);
        contentStream.showText(name);
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
        contentStream.newLineAtOffset(x+colWidth+5, y-14);
        contentStream.showText(value);
        contentStream.endText();

    }


    public static void chartToPDF(JFreeChart chart) throws IOException {

        String filename = askUserForFilename();
        if (filename == null || filename.trim().isEmpty()) {
            System.out.println("No filename provided");
            return;
        }
        String pdfPath = filename.endsWith(".pdf") ? filename : filename + ".pdf";

        int width = 1050;
        int height = 700;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

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
            PDImageXObject pdImageXObject = PDImageXObject.createFromFile(tempImage.getAbsolutePath(), document);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float imageWidth = pdImageXObject.getWidth();
                float imageHeight = pdImageXObject.getHeight();
                float x = (page.getMediaBox().getWidth() - imageWidth) / 2;
                float y = (page.getMediaBox().getHeight() - imageHeight) / 2;
                contentStream.drawImage(pdImageXObject, x, y,imageWidth, imageHeight);
            }
            document.save(pdfPath);
            document.close();
            tempImage.delete();
            System.out.println("Exported to PDF: " + tempImage.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void chartToCSV(JFreeChart chart) throws IOException {

        String filePath = askUserForFilename();
        if (filePath == null) {
            return;
        }
        if (!filePath.endsWith(".csv")) {
            filePath += ".csv";
        }
        File file = new File(filePath);
        if (!(chart.getPlot() instanceof CategoryPlot)) {
            throw new IllegalArgumentException("chartToCSV only supports CategoryPlot");
        }
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryDataset dataset = plot.getDataset();
        if (dataset == null) {
            throw new IllegalStateException("Dataset is null");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Date,Metric,Value\n");

            int rowCount = dataset.getRowCount();
            int columnCount = dataset.getColumnCount();
            for (int row = 0; row < rowCount; row++) {
                String seriesName = dataset.getRowKey(row).toString();
                for (int col = 0; col < columnCount; col++) {
                    String categoryName = dataset.getColumnKey(col).toString();
                    Number value1 = dataset.getValue(row, col);
                    writer.write(categoryName + "," + seriesName + "," + value1 + "\n");
                }
            }
        }
        System.out.println("Exported to CSV: " + file.getAbsolutePath());
    }

    private static String askUserForFilename(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("save file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int userSelection = chooser.showSaveDialog(null);
        if(userSelection == JFileChooser.APPROVE_OPTION){
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
