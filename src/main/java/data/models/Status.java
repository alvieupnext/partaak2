package data.models;

/** Indicates whether the case was laboratory confirmed or not. */
public enum Status {
    LAB_CONFIRMED { public String toString() { return "Laboratory-confirmed case";} },
    PROBABLE_CASE { public String toString() { return "Probable Case"            ;} },
}