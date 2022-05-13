package solutions;

import data.models.Metrics;
import data.models.Patient;

public interface CovidAnalyser {

    /**
     * Computes some general metrics about the given dataset.
     */
    Metrics phaseOne(Patient[] patients);

    /**
     * Computes a specific query about the given dataset, given overall metrics obtained by phaseOne.
     * The result of the following query is computed: "How many days are there from the point where `numFemales` woman have been infected up to the point
     * where still `numICU` people in the dataset have to become infected who have comorbidities and were admitted to the ICU?".
     * (Both bounds are included, either date can be prior to the other.)
     */
    Long phaseTwo(Patient[] patients, Metrics metrics, long numFemales, long numICU);

}
