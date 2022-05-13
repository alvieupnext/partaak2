package data.adapters;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CsvContext;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class WriteDate extends CellProcessorAdaptor {

    public WriteDate() { super(); }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

    public Object execute(Object value, CsvContext context) {
        return next.execute(format.format((Date) value), context);
    }
}
