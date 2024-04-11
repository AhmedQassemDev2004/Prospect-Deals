package com.app.prospectdeals;

public class Company {
    private String name;
    private String number;
    private String timeZone;
    private String direct;
    private String email;
    private String dmName;
    private String termination;
    private String specialNotes;
    private String opportunitySystem;

    public Company(String name, String number, String timeZone, String direct, String email, String dmName, String termination, String specialNotes, String opportunitySystem) {
        this.name = name;
        this.number = number;
        this.timeZone = timeZone;
        this.direct = direct;
        this.email = email;
        this.dmName = dmName;
        this.termination = termination;
        this.specialNotes = specialNotes;
        this.opportunitySystem = opportunitySystem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDmName() {
        return dmName;
    }

    public void setDmName(String dmName) {
        this.dmName = dmName;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public String getOpportunitySystem() {
        return opportunitySystem;
    }

    public void setOpportunitySystem(String opportunitySystem) {
        this.opportunitySystem = opportunitySystem;
    }
}