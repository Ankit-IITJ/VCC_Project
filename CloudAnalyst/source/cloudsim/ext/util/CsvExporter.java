package cloudsim.ext.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class CsvExporter {
    private static final DecimalFormat df = new DecimalFormat("#0.00");

    public static void saveToCsv(File file,
                                 String header,
                                 List<Object[]> summary,
                                 List<Object[]> ubStats,
                                 List<Object[]> dcStats,
                                 List<Object[]> costSummary,
                                 List<Object[]> costDetails) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(header + "\n\n");

            writer.write("Overall Response Time Summary\n");
            writeTable(writer, summary, new String[]{"Metric", "Average (ms)", "Minimum (ms)", "Maximum (ms)"});

            writer.write("\nResponse Time by Region\n");
            writeTable(writer, ubStats, new String[]{"Userbase", "Average (ms)", "Minimum (ms)", "Maximum (ms)"});

            writer.write("\nData Center Request Servicing Times\n");
            writeTable(writer, dcStats, new String[]{"Data Center", "Average (ms)", "Minimum (ms)", "Maximum (ms)"});

            writer.write("\nCost Summary\n");
            writeTable(writer, costSummary, new String[]{"Description", "Amount ($)"});

            writer.write("\nCost Details\n");
            writeTable(writer, costDetails, new String[]{"Data Center", "VM Cost ($)", "Data Transfer Cost ($)", "Total ($)"});
        }
    }

    private static void writeTable(FileWriter writer, List<Object[]> data, String[] headers) throws IOException {
        writer.write(String.join(",", headers) + "\n");
        for (Object[] row : data) {
            String[] formattedRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                Object value = row[i];
                if (value instanceof Number) {
                    formattedRow[i] = df.format(value);
                } else {
                    formattedRow[i] = (value != null) ? value.toString() : "";
                }
            }
            writer.write(String.join(",", formattedRow) + "\n");
        }
        writer.write("\n");
    }
}