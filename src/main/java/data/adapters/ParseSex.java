package data.adapters;

import data.models.Sex;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseSex extends CellProcessorAdaptor {

    public ParseSex() { super(); }

    public <T> T execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        return switch (value.toString()) {
            case "Male" -> next.execute(Sex.MALE, context);
            case "Female" -> next.execute(Sex.FEMALE, context);
            case "Unknown", "Other", "Missing", "NA", "nul" -> next.execute(Sex.UNKNOWN, context);
            default -> throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as a sex.", value), context, this);
        };
    }
}
