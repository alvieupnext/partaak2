import data.models.AgeGroup;
import data.models.Patient;
import data.models.Sex;
import data.models.Status;

import java.util.Calendar;
import java.util.GregorianCalendar;

//netid: avargasg

public class MainTest {
    public static void main (String[] args) {
        try {
            //Test 1: ensure that the query system works
            //enable servers (all but the last dataserver)
            QueryTest.main();
            Client client1 = new Client("Jef");
            Client client2 = new Client("Clarisse");
            Client client3 = new Client("Olivier");
            Client client4 = new Client("Lisa");
            //the date queries should fail and the number queries are smaller
            client1.overview("comorbidities"); //70230
            client2.date(3000, "deceased"); //not found
            client3.overview("intensiveCare"); //631
            client4.date(110000, "male"); //not found
            //Test 2: link new server at runtime
            //Enable the third dataserver
            System.out.println("Enabling third server");
            //data queries work and we have higher numbers
            LinkTest.main(); //link third data server
            client1.overview("comorbidities"); //105129
            client2.date(3000, "deceased"); //30/07/2021
            client3.overview("intensiveCare"); //895
            client4.date(110000, "male"); //15/08/2021
            //Test 3: Adding new patients to the dataset is possible (only if client is connected to a hospital server)
            String hospitalName = "hospitalTest";
            HospitalServer hos = new HospitalServer(hospitalName);
            //ICU
            Patient patient1 = new Patient(new GregorianCalendar(2020, Calendar.AUGUST, 9).getTime(), Status.LAB_CONFIRMED, Sex.MALE, AgeGroup.FORTIES, true, true, false, false);
            //adding patient should throw exception when not connected to hospital
            client1.addPatient(patient1);
            client1.connect(hospitalName);
            client3.connect(hospitalName);
            //Client 1 sends new ICU case
            client1.addPatient(patient1);
            client3.overview("intensiveCare"); //should remain 895 (batch size not yet met)
            //Client 2 sends new ICU + comorbidities case
            Patient patient2 = new Patient(new GregorianCalendar(2022, Calendar.JANUARY, 21).getTime(), Status.LAB_CONFIRMED, Sex.FEMALE, AgeGroup.THIRTIES, true, true, false, true);
            client3.addPatient(patient2);
            client2.overview("intensiveCare"); //should become 897 (batch size met)
            client4.overview("comorbidities"); //should be 105130 (one higher)
        } catch (Exception e) {
            System.err.println("Test failed: " + e);
            e.printStackTrace();
        }
    }
}
