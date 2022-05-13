package data.models;

/** Container class for some metrics of interest. */
public class Metrics implements java.io.Serializable {
    public long labConfirmed = 0;
    public long male = 0;
    public long female = 0;
    public long aged = 0;
    public long hospitalised = 0;
    public long intensiveCare = 0;
    public long deceased = 0;
    public long comorbidities = 0;

    public Metrics(long confirmed, long men, long woman, long elderly, long inHospital, long icu, long deaths, long otherIllnesses) {
        labConfirmed = confirmed;
        male = men;
        female = woman;
        aged = elderly;
        hospitalised = inHospital;
        intensiveCare = icu;
        deceased = deaths;
        comorbidities = otherIllnesses;
    }

    public Metrics merge(Metrics m2) {
        //add the values from the second metric object and return this metric
        labConfirmed += m2.labConfirmed;
        male += m2.male;
        female += m2.female;
        aged += m2.aged;
        hospitalised += m2.hospitalised;
        intensiveCare += m2.intensiveCare;
        deceased += m2.deceased;
        comorbidities += m2.comorbidities;
        return this;
    }

    public Metrics() {}

    @Override
    public String toString() {
        return "Metrics{" +
                "labConfirmed=" + labConfirmed +
                ", male=" + male +
                ", female=" + female +
                ", aged=" + aged +
                ", hospitalised=" + hospitalised +
                ", icu=" + intensiveCare +
                ", deceased=" + deceased +
                ", comorbidities=" + comorbidities +
                '}';
    }
}
