package solutions;

import data.Reader;
import data.models.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SequentialAnalyser implements CovidAnalyser {

    /** Phase one: compute some general metrics about the data set. */
    public Metrics phaseOne(Patient[] patients) {
        long labConfirmed = 0;
        long male = 0;
        long female = 0;
        long aged = 0;
        long hospitalised = 0;
        long icu = 0;
        long deceased = 0;
        long comorbidities = 0;

        for (Patient patient: patients) {
            if (patient.status == Status.LAB_CONFIRMED) labConfirmed += 1;
            if (patient.sex == Sex.MALE) male += 1;
            else if (patient.sex == Sex.FEMALE) female += 1;
            if (patient.age == AgeGroup.SIXTIES || patient.age == AgeGroup.SEVENTIES || patient.age == AgeGroup.OVER_EIGHTY) aged += 1;
            if (patient.hospitalised) hospitalised += 1;
            if (patient.icu) icu += 1;
            if (patient.deceased) deceased += 1;
            if (patient.comorbidities) comorbidities += 1;
        }

        return new Metrics(labConfirmed, male, female, aged, hospitalised, icu, deceased, comorbidities);
    }

    /** Phase two: compute a more complicated query. */
    public Long phaseTwo(Patient[] patients, Metrics metrics, long numFemales, long numICU) {
        // Find point where numFemales women have been infected.
        Date date1 = null;
        for (Patient patient: patients) {
            if (patient.sex == Sex.FEMALE) numFemales -= 1;
            if (numFemales == 0) {
                date1 = patient.date;
                break;
            }
        }
        // Find point where still numICU people in the dataset have to become infected who have comorbitidies and were admitted on the ICU.
        Date date2 = null;
        for(int i = patients.length - 1; i >= 0; i--) {
            Patient patient = patients[i];
            if (patient.icu && patient.comorbidities) numICU -= 1;
            if (numICU == 0) {
                date2 = patient.date;
                break;
            }
        }
        // If one of the two dates is non-existing, no result can be computed.
        if (date1 == null || date2 == null) {
            return null;
        // Otherwise, compute the time interval.
        } else {
            return Math.abs(TimeUnit.DAYS.convert(date2.getTime() - date1.getTime(), TimeUnit.MILLISECONDS));
        }
    }

    public static void main(String[] args) {
        long before = System.currentTimeMillis();
        Patient[] patients = Reader.generateData(100000000);
        long after = System.currentTimeMillis();
        System.out.println("Read dataset in " + (after - before) + "ms\n# Patients in dataset: " + patients.length);

        CovidAnalyser a = new SequentialAnalyser();

        // Compute some global metrics.
        before = System.currentTimeMillis();
        Metrics metrics = a.phaseOne(patients);
        after = System.currentTimeMillis();
        System.out.println("Computed metrics in " + (after - before) + "ms:\n" + metrics);

        // A more complicated query: how many days are there from
        // the point where 75% of woman have been infected
        // up to the point were still 2500 people in the dataset have to become infected who have comorbitidies and were later admitted to the ICU.
        // (Both bounds included.)
        long females = Math.round((metrics.female * 75.0) / 100.0);
        before = System.currentTimeMillis();
        Long days = a.phaseTwo(patients, metrics, females, 2500);
        after = System.currentTimeMillis();
        System.out.println("Computed result (" + days + " days) in " + (after - before) + "ms.");
    }
}
