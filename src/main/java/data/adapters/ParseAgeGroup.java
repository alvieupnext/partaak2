package data.adapters;

import data.models.AgeGroup;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseAgeGroup extends CellProcessorAdaptor {

    public ParseAgeGroup() { super(); }

    public <T> T execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);
        return switch (value.toString()) {
            case "0 - 9 Years"   -> next.execute(AgeGroup.UNDER_TEN,   context);
            case "10 - 19 Years" -> next.execute(AgeGroup.TEENER,      context);
            case "20 - 29 Years" -> next.execute(AgeGroup.TWENTIES,    context);
            case "30 - 39 Years" -> next.execute(AgeGroup.THIRTIES,    context);
            case "40 - 49 Years" -> next.execute(AgeGroup.FORTIES,     context);
            case "50 - 59 Years" -> next.execute(AgeGroup.FIFTIES,     context);
            case "60 - 69 Years" -> next.execute(AgeGroup.SIXTIES,     context);
            case "70 - 79 Years" -> next.execute(AgeGroup.SEVENTIES,   context);
            case "80+ Years"     -> next.execute(AgeGroup.OVER_EIGHTY, context);
            case "Missing", "NA" -> next.execute(AgeGroup.MISSING,     context);
            default -> throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as an age group.", value), context, this);
        };
    }
}
