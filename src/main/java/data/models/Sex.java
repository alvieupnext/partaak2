package data.models;

/** Indicates the sex of the person: male, female, or unknown. */
public enum Sex {
    MALE    { public String toString() { return "Male"   ;} },
    FEMALE  { public String toString() { return "Female" ;} },
    UNKNOWN { public String toString() { return "Missing";} },
}
