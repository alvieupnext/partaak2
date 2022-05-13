package solutions;


import data.models.Patient;
import data.models.Sex;

import java.util.concurrent.RecursiveTask;

public class BuildTree extends RecursiveTask<Node> {
    Patient[] patients; int lo; int hi; int T;

    public BuildTree(Patient[] patients, int lo, int hi, int t) {
        this.patients = patients;
        this.lo = lo;
        this.hi = hi;
        T = t;
    }

    @Override
    protected Node compute() {
        if (hi - lo <= T) { //sequential cut-off
            int female = 0; //start sum females and ICUs at 0
            int numICU = 0;
            for (int i = lo; i < hi; i++){ //traverse the patients in the range
                Patient patient = patients[i];
                if (patient.sex ==  Sex.FEMALE) female += 1; //if condition met, increment counter
                if (patient.icu && patient.comorbidities) numICU += 1;
            }
            return new Node(null, null, lo, hi, female, numICU); //return new leaf node with counters
        }
        else {
            int lom = lo /2;
            int him = hi/2;
            int mid = lom+him ; //get middle
            BuildTree left = new BuildTree(patients, lo, mid, T); //create two new BuildTree tasks
            BuildTree right = new BuildTree(patients, mid, hi, T);
            left.fork(); //create a new thread for the left task
            Node rightNode = right.compute(); //use current thread to solve the right task
            Node leftNode = left.join(); //wait until the left task is finished
            int sumFemales = leftNode.numFemales + rightNode.numFemales; //take sum of females and ICU
            int sumICU = leftNode.numICU + rightNode.numICU;
            return new Node(leftNode, rightNode, lo, hi, sumFemales, sumICU); //create a new non-leaf node
        }
    }
}
