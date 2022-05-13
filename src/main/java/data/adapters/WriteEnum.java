package data.adapters;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.util.CsvContext;

public class WriteEnum extends CellProcessorAdaptor {

    public WriteEnum() { super(); }

    public Object execute(Object value, CsvContext context) {
        return next.execute(value.toString(), context);
    }
}
