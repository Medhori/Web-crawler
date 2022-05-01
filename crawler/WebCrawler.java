package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static crawler.WebFind.*;

public class WebCrawler extends JFrame {
    static ArrayList<String> urls = new ArrayList<>();
    static ArrayList<String> urlsTitles = new ArrayList<>();

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setTitle("Simple Window");


        var urlTextField = new JTextField();
        urlTextField.setName("UrlTextField");
        var uRL = new JLabel("URL:");



        var titleText = new JLabel("Title:");
        var titleLabel = new JLabel();
        titleLabel.setName("TitleLabel");



        String[] column = {"URL", "Title"};
        DefaultTableModel model = new DefaultTableModel(0, column.length);
        model.setColumnIdentifiers(column);
        var titlesTable = new JTable(model);
        titlesTable.setName("TitlesTable");
        titlesTable.setEnabled(false);

        JScrollPane jScrollPane = new JScrollPane(titlesTable);
        add(jScrollPane, BorderLayout.CENTER);


        var export = new JLabel("Export:");
        var exportField = new JTextField();
        exportField.setName("ExportUrlTextField");


        var runButton = new JButton();
        runButton.setText("parse");
        runButton.setName("RunButton");
        runButton.addActionListener(x -> {
            Thread thread = new Thread(() -> {

                try {
                    clear();
                    model.setRowCount(0);

                    if (!urlTextField.getText().equals("")) {

                        titleLabel.setText(
                                parseTitleFromHtmlCode(parseHtmlCode(urlTextField.getText()))
                        );

                        String[] row2 = {urlTextField.getText(), titleLabel.getText()};
                        model.addRow(row2);
                        urls.add(urlTextField.getText());
                        urlsTitles.add(titleLabel.getText());

                        List<String> urlList = new ArrayList<>(parseLinksFromHtmlCode(urlTextField.getText(), parseHtmlCode(urlTextField.getText())));
                        System.out.println(urlList);

                        for (String value : urlList) {
                            if (!parseHtmlCode(value).equals("")) {
                                addUrlToUrlsIfIsNotAdded(value);
                            }
                        }
                        for (int i = 1; i < urls.size(); i++) {
                            String[] row1 = {urls.get(i), urlsTitles.get(i)};
                            model.addRow(row1);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            });

            thread.start();
        });


        var exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        exportButton.addActionListener(x -> {
            export(exportField.getText());
        });

        // JPanel component
        var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(uRL);
        panel.add(urlTextField);
        panel.add(runButton);
        panel.add(titleText);
        panel.add(titleLabel);

        // Save panel
        var savePanel = new JPanel();
        savePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        savePanel.add(export);
        savePanel.add(exportField);
        savePanel.add(exportButton);

        forceSize(panel, 500, 70);
        forceSize(urlTextField, 320, 30);
        forceSize(runButton, 100, 30);
        forceSize(exportField, 300, 30);
        forceSize(exportButton, 100, 30);

        add(panel, BorderLayout.NORTH);
        add(savePanel, BorderLayout.SOUTH);

        setVisible(true);

    }


    public void export(final String exportFileName) {
        try (PrintWriter fileWriter = new PrintWriter(exportFileName, StandardCharsets.UTF_8)) {
            final int lastIndex = urls.size() - 1;
            for (int i = 0; i < lastIndex; i++) {
                fileWriter.println(urls.get(i));
                fileWriter.println(urlsTitles.get(i));
            }
            fileWriter.println(urls.get(lastIndex));
            fileWriter.print(urlsTitles.get(lastIndex));
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }


    private void addUrlToUrlsIfIsNotAdded(final String url) {
        String urlTitle;
        if (!urls.contains(url)) {
            urlTitle = parseTitle(url);
            urls.add(url);
            urlsTitles.add(urlTitle);
        }
    }

    private void clear() {
        urls.clear();
        urlsTitles.clear();
    }


    public static void forceSize(JComponent component, int width, int height) {
        Dimension d = new Dimension(width, height);
        component.setMaximumSize(d);
        component.setMaximumSize(d);
        component.setPreferredSize(d);
    }


}