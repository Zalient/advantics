package com.university.grp20.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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

    public static void chartToPDF(JFreeChart chart) throws IOException {

        String filename = askUserForFilename();
        if (filename == null || filename.trim().isEmpty()) {
            System.out.println("No filename provided");
            return;
        }
        String pdfPath = filename.endsWith(".pdf") ? filename : filename + ".pdf";

        int width = 900;
        int height = 600;

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

            PDRectangle newMediaBox = new PDRectangle(mediaWidth * 1.5f, mediaHeight);
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
