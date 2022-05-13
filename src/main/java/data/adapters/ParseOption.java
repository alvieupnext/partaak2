package data.adapters;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseOption extends CellProcessorAdaptor {

    public ParseOption() { super(); }

    public <T> T execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        return switch (value.toString()) {
            case "Yes" -> next.execute(true, context);
            case "No", "Unknown", "Missing", "nul" -> next.execute(false, context);
            default -> throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as an option.", value), context, this);
        };
    }
}
