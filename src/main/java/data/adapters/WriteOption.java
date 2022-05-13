package data.adapters;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class WriteOption extends CellProcessorAdaptor {

    public WriteOption() { super(); }

    public Object execute(Object value, CsvContext context) {
        return next.execute((boolean) value ? "Yes" : "No", context);
    }
}
