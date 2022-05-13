package data.models;

import java.util.Date;
import java.util.Random;

/** Represents a deidentified patient record. */
public class Patient {
    public Date date;
    public Status status;
    public Sex sex;
    public AgeGroup age;

    // Underlying data can be one of Yes - No - Unknown - Missing. We only treat Yes as true.
    public boolean hospitalised;
    public boolean icu;
    public boolean deceased;
    public boolean comorbidities;

    public Patient(Date date, Status status, Sex sex, AgeGroup age, boolean hospitalised, boolean icu, boolean deceased, boolean comorbidities) {
        this.date = date;
        this.status = status;
        this.sex = sex;
        this.age = age;
        this.hospitalised = hospitalised;
        this.icu = icu;
        this.deceased = deceased;
        this.comorbidities = comorbidities;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "date=" + date +
                ", status=" + status +
                ", sex=" + sex +
                ", age=" + age +
                ", hospitalised=" + hospitalised +
                ", icu=" + icu +
                ", deceased=" + deceased +
                ", comorbidities=" + comorbidities +
                "}";
    }

    // Data generation.
    public static Patient generatePatient(Random r, Date date) {
        double val = r.nextDouble();
        Sex sex;
        if (val <= 0.46) sex = Sex.MALE;
        else if (val <= 0.97) sex = Sex.FEMALE;
        else sex = Sex.UNKNOWN;
        val = r.nextDouble();
        AgeGroup age;
        if (val <= 0.077) age = AgeGroup.UNDER_TEN;
        else if (val <= 0.207) age = AgeGroup.TEENER;
        else if (val <= 0.386) age = AgeGroup.TWENTIES;
        else if (val <= 0.553) age = AgeGroup.THIRTIES;
        else if (val <= 0.695) age = AgeGroup.FORTIES;
        else if (val <= 0.823) age = AgeGroup.FIFTIES;
        else if (val <= 0.912) age = AgeGroup.SIXTIES;
        else if (val <= 0.959) age = AgeGroup.SEVENTIES;
        else if (val <= 0.988) age = AgeGroup.OVER_EIGHTY;
        else age = AgeGroup.MISSING;
        return new Patient(
                date,
                r.nextDouble() <= 0.86 ? Status.LAB_CONFIRMED : Status.PROBABLE_CASE,
                sex,
                age,
                r.nextDouble() <= 0.03,
                r.nextDouble() <= 0.003,
                r.nextDouble() <= 0.013,
                r.nextDouble() <= 0.35
                );
    }

    // Needed for the CSV reader/writer.
    public Patient() {}

    public Date     getDate()          { return date;          }
    public Status   getStatus()        { return status;        }
    public Sex      getSex()           { return sex;           }
    public AgeGroup getAge()           { return age;           }
    public boolean  getHospitalised()  { return hospitalised;  }
    public boolean  getIcu()           { return icu;           }
    public boolean  getDeceased()      { return deceased;      }
    public boolean  getComorbidities() { return comorbidities; }

    public void setDate(Date date)                      { this.date = date;                   }
    public void setStatus(Status status)                { this.status = status;               }
    public void setSex(Sex sex)                         { this.sex = sex;                     }
    public void setAge(AgeGroup age)                    { this.age = age;                     }
    public void setHospitalised(boolean hospitalised)   { this.hospitalised = hospitalised;   }
    public void setIcu(boolean icu)                     { this.icu = icu;                     }
    public void setDeceased(boolean deceased)           { this.deceased = deceased;           }
    public void setComorbidities(boolean comorbidities) { this.comorbidities = comorbidities; }
}
