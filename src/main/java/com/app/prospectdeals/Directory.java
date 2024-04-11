package com.app.prospectdeals;

public class Directory {
    private String name;
    private int numberOfFiles;

    public Directory() {

    }

    public Directory(String name, int numberOfFiles) {
        this.name = name;
        this.numberOfFiles = numberOfFiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }
}
