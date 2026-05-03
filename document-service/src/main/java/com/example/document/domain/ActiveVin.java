package com.example.document.domain;

public class ActiveVin {

    private final String vin;
    private final String status;

    public ActiveVin(String vin, String status) {
        this.vin = vin;
        this.status = status;
    }

    public String getVin() {
        return vin;
    }

    public String getStatus() {
        return status;
    }
}
