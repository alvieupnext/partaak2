package solutions;

import data.models.Patient;
import data.models.Sex;

import java.util.Date;
import java.util.concurrent.RecursiveAction;

public class FixSum extends RecursiveAction {
    Node node;
    int fromLeftFemales;
    int fromRightICU;
    Patient[] patients;
    Date[] dates;
    long targetFemales;
    long targetICU;

    public FixSum(Node node, int fromLeftFemales, int fromRightICU, Patient[] patients, Date[] dates, long targetFemales, long targetICU) {
        this.node = node;
        this.fromLeftFemales = fromLeftFemales;
        this.fromRightICU = fromRightICU;
        this.patients = patients;
        this.dates = dates;
        this.targetFemales = targetFemales;
        this.targetICU = targetICU;
    }

    @Override
    protected void compute() {
        if (node.isALeaf()) {
            int sumFemales = node.numFemales + fromLeftFemales;
            int sumICU = node.numICU + fromRightICU;
            //if our fromLeft is smaller than our target AND the sum is bigger than our target, then our target female patient is in this range
            if (fromLeftFemales < targetFemales && targetFemales <= sumFemales) {
                int female = fromLeftFemales; //start female counter at from left
                for (int i = node.lo; i < node.hi; i++) { //traverse every patient in this range from left to right
                    Patient patient = patients[i];
                    if (patient.sex == Sex.FEMALE) {
                        female += 1; //if condition met, increment counter
                        if (female == targetFemales) { //if reached targetFemales
                            dates[0] = patient.date; //set first date of dates to the patients date
                            break; //stop the for loop
                        }
                    }
                }
            }
            //if our fromRight is smaller than our target and the sum is bigger than our target, then our target ICU patient is in this range
                if (fromRightICU < targetICU && targetICU <= sumICU) {
                    int ICU = fromRightICU;//start female counter at from right
                    for (int i = node.hi - 1; i >= node.lo; i--) { //traverse every patient in this range from right to left
                        Patient patient = patients[i];
                        if (patient.icu && patient.comorbidities) {
                            ICU += 1;//if condition met, increment counter
                            if (ICU == targetICU) { //if reached targetICU
                                dates[1] = patient.date; //set second date of dates to the patients date
                                break; //stop the for loop
                            }
                        }
                    }
                }
        } else {
            //add the ICU numbers from the right node to fromRightICU
            FixSum left = new FixSum(node.left, fromLeftFemales, fromRightICU + node.right.numICU, patients, dates, targetFemales, targetICU);
            //add the female numbers from the left node to fromLeftFemales
            FixSum right = new FixSum(node.right, fromLeftFemales + node.left.numFemales, fromRightICU, patients, dates, targetFemales, targetICU);
            left.fork();
            right.compute();
            left.join();
        }
    }
}
