package data;

import data.adapters.*;
import data.models.Patient;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {

    private static final String[] nameMapping = {"date", null, null, null, "status", "sex", "age", null, "hospitalised", "icu", "deceased", "comorbidities"};
    private static final CellProcessor[] processors = {
            new WriteDate(), null, null, null, new WriteEnum(), new WriteEnum(), new WriteEnum(), null, new WriteOption(), new WriteOption(), new WriteOption(), new WriteOption()};
    private static final String[] header = {"cdc_case_earliest_dt","cdc_report_dt","pos_spec_dt","onset_dt","current_status","sex","age_group","race_ethnicity_combined","hosp_yn","icu_yn","death_yn","medcond_yn"};

    /**
     * Writes all the given patient data of to a CSV file.
     * Fields that are not in the patient data will remain blank.
     * Boolean data will be written as "Yes"/"No", there will be no "Missing" or "NA" values.
     */
    public static void writeData(String file, ArrayList<Patient> patients) throws IOException {
        try (ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);){
            beanWriter.writeHeader(header);
            for(Patient patient: patients) {
                beanWriter.write(patient, nameMapping, processors);
            }
        }
    }
}
