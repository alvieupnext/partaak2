package data.models;

/** The age group of a person: 0-9; 10-19; 20-29; 30-39; 40-49; 50-59; 60-69; 70-79; 80+. */
public enum AgeGroup {
    UNDER_TEN   { public String toString() { return "0 - 9 Years"  ;} },
    TEENER      { public String toString() { return "10 - 19 Years";} },
    TWENTIES    { public String toString() { return "20 - 29 Years";} },
    THIRTIES    { public String toString() { return "30 - 39 Years";} },
    FORTIES     { public String toString() { return "40 - 49 Years";} },
    FIFTIES     { public String toString() { return "50 - 59 Years";} },
    SIXTIES     { public String toString() { return "60 - 69 Years";} },
    SEVENTIES   { public String toString() { return "70 - 79 Years";} },
    OVER_EIGHTY { public String toString() { return "80+ Years"    ;} },
    MISSING     { public String toString() { return "Missing"      ;} },
}
