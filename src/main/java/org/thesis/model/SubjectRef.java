package org.thesis.model;

public class SubjectRef {
    public String subjectCategory;
    public String accountToken;
    public String customerToken;

    // Must remain null in conforming payloads. They are present only to make DEV-1 visible.
    public String rawNationalIdentifier;
    public String rawAccountNumber;

    public SubjectRef() {
    }
}
