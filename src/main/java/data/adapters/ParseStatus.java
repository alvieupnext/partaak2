package data.adapters;

import data.models.Status;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseStatus extends CellProcessorAdaptor {

    public ParseStatus() { super(); }

    public <T> T execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        return switch (value.toString()) {
            case "Laboratory-confirmed case" -> next.execute(Status.LAB_CONFIRMED, context);
            case "Probable Case" -> next.execute(Status.PROBABLE_CASE, context);
            default -> throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as a status.", value), context, this);
        };
    }
}
