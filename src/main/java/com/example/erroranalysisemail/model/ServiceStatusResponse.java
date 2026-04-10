package com.example.erroranalysisemail.model;

public class ServiceStatusResponse {
    private ServiceStatus serviceStatus;

    public ServiceStatusResponse() {
    }

    public ServiceStatusResponse(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}
