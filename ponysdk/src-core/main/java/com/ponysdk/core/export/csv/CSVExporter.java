
package com.ponysdk.core.export.csv;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.StreamResource;
import com.ponysdk.core.event.StreamHandler;
import com.ponysdk.core.export.ExportableField;
import com.ponysdk.core.export.Exporter;
import com.ponysdk.core.export.util.PropertyUtil;
import com.ponysdk.core.export.xml.XMLExporter;

public class CSVExporter<T> implements Exporter<T> {

    private static final Logger log = LoggerFactory.getLogger(XMLExporter.class);

    private static final String NAME = "CSV";

    private static final char DELIMITER = ';';

    private static final char LF = '\n';

    private final String fileName;

    public CSVExporter(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String export(final List<ExportableField> exportableFields, final List<T> records) throws Exception {
        final StringBuffer buffer = new StringBuffer();
        final Iterator<ExportableField> iter = exportableFields.iterator();

        while (iter.hasNext()) {
            final ExportableField exportableField = iter.next();
            final String header = exportableField.getCaption();
            buffer.append(header);
            if (iter.hasNext()) buffer.append(DELIMITER);
        }

        buffer.append(LF);
        for (final T row : records) {
            for (final ExportableField exportableField : exportableFields) {
                buffer.append(getDisplayValue(row, exportableField));
                buffer.append(DELIMITER);
            }
            buffer.append(LF);
        }

        final StreamResource streamResource = new StreamResource();
        streamResource.open(new StreamHandler() {

            @Override
            public void onStream(final HttpServletRequest req, final HttpServletResponse response) {
                response.reset();
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".csv");
                PrintWriter printer;
                try {
                    printer = response.getWriter();
                    printer.print(buffer.toString());
                    printer.flush();
                    printer.close();
                } catch (final Exception e) {
                    log.error("Error when exporting", e);
                }
            }
        });

        return records.size() + " row(s) exported in " + fileName;
    }

    protected String getDisplayValue(final T row, final ExportableField exportableField) throws Exception {
        return PropertyUtil.getProperty(row, exportableField.getKey());
    }
}
