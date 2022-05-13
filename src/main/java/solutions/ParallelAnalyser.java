package solutions;

import data.Reader;
import data.models.Metrics;
import data.models.Patient;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * TODO: A parallel implementation of CovidAnalyser using Java Fork/Join. Carefully read the assignment for detailed instructions and requirements.
 */
public class ParallelAnalyser implements CovidAnalyser  {
    final int p; // The parallelism level (i.e. max. # cores that can be used by Java Fork/Join).
    final int T; // The sequential threshold.
    final ForkJoinPool pool;

    /**
     * Creates a parallel analyser with p worker threads and a sequential cut-off T.
     * @param p The parallelism level.
     * @param T The sequential cut-off.
     */
    public ParallelAnalyser(int p, int T) {
        this.p = p;
        this.T = T;
        //Initialise the Java Fork/Join framework here as well.
        this.pool = new ForkJoinPool(p);
    }

    @Override
    public Metrics phaseOne(Patient[] patients) {
        //start the phaseOneTask
        return pool.invoke(new PhaseOneTask(patients, 0, patients.length, T));
    }

    @Override
    public Long phaseTwo(Patient[] patients, Metrics metrics, long numFemales, long numICU) {
        //step one: up-pass
        Node root = pool.invoke(new BuildTree(patients, 0, patients.length, T));

        //step two: the down-pass (which will get our dates)
        Date[] dates = new Date[2];
        pool.invoke(new FixSum(root, 0, 0, patients, dates, numFemales, numICU));
        Date date1 = dates[0];
        Date date2 = dates[1];
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
        Patient[] patients = Reader.generateData(110000000);
        long after = System.currentTimeMillis();
        System.out.println("Read dataset in " + (after - before) + "ms\n# Patients in dataset: " + patients.length);

        CovidAnalyser a = new ParallelAnalyser(4, 5000);

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
