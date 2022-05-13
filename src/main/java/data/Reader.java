package data;

import data.adapters.ParseAgeGroup;
import data.adapters.ParseOption;
import data.adapters.ParseSex;
import data.adapters.ParseStatus;
import data.models.Patient;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Reader {

    /** Reads all the data of a given CSV file. */
    public static Patient[] readData(String file) throws IOException {
        return read(file, Integer.MAX_VALUE);
    }

    /** Reads a given number of rows of a given CSV file. */
    public static Patient[] readData(String file, int howMany) throws IOException {
        return read(file, howMany);
    }

    private static final String[] nameMapping = {"date", null, null, null, "status", "sex", "age", null, "hospitalised", "icu", "deceased", "comorbidities"};
    private static final CellProcessor[] processors = {
            new ParseDate("yyyy/MM/dd"), null, null, null, new ParseStatus(), new ParseSex(), new ParseAgeGroup(), null, new ParseOption(), new ParseOption(), new ParseOption(), new ParseOption()};

    private static Patient[] read(String file, int howMany) throws IOException {
        ArrayList<Patient> patients = (howMany == Integer.MAX_VALUE) ? new ArrayList<>() : new ArrayList<>(howMany);
        try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);) {
            String[] header = beanReader.getHeader(true);
            Patient p;
            long count = 0;
            while (count++ < howMany && (p = beanReader.read(Patient.class, nameMapping, processors)) != null) {
                patients.add(p);
            }
        }
        return patients.toArray(new Patient[0]);
    }

    /** Generates a list of patients, sorted by date (in a specific interval of hardcoded dates). */
    public static Patient[] generateData(int n) {
        Patient[] patients = new Patient[n];
        Date current = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        Date end = new GregorianCalendar(2022, Calendar.MARCH, 13).getTime();
        int period = (int) Math.abs(TimeUnit.DAYS.convert(end.getTime() - current.getTime(), TimeUnit.MILLISECONDS)); // Number of days.
        // For simplicity, every day gets more or less the same number of patients.
        int peoplePerDay = (int) Math.round((double) n / (double) period);

        Random rand = new Random(20200101);
        int todoToday = rand.nextInt(2 * peoplePerDay) + 1;
        Calendar c = Calendar.getInstance();
        for(int i = 0; i < n; i++) {
            if (todoToday == 0) {
                // Next day.
                c.setTime(current);
                c.add(Calendar.DAY_OF_MONTH, 1);
                current = c.getTime();
                todoToday = rand.nextInt(2 * peoplePerDay) + 1;
            }
            patients[i] = Patient.generatePatient(rand, current);
            todoToday -= 1;
        }
        return patients;
    }

    public static Patient[] generateDataInDates(int n, Date current, Date end) {
        Patient[] patients = new Patient[n];
        int period = (int) Math.abs(TimeUnit.DAYS.convert(end.getTime() - current.getTime(), TimeUnit.MILLISECONDS)); // Number of days.
        // For simplicity, every day gets more or less the same number of patients.
        int peoplePerDay = (int) Math.round((double) n / (double) period);

        Random rand = new Random(20200101);
        int todoToday = rand.nextInt(2 * peoplePerDay) + 1;
        Calendar c = Calendar.getInstance();
        for(int i = 0; i < n; i++) {
            if (todoToday == 0) {
                // Next day.
                c.setTime(current);
                c.add(Calendar.DAY_OF_MONTH, 1);
                current = c.getTime();
                todoToday = rand.nextInt(2 * peoplePerDay) + 1;
            }
            patients[i] = Patient.generatePatient(rand, current);
            todoToday -= 1;
        }
        return patients;
    }

}
