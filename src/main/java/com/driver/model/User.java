package com.driver.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String originalIp;
    private String maskedIp;

    private Boolean connected = false;

    //Mappings
    //1. to ServiceProvider
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<ServiceProvider> serviceProviderList;

    //2. to Connection
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Connection> connectionList;

    //3. to Country
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Country originalCountry;

    public User() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginalIp() {
        return originalIp;
    }

    public void setOriginalIp(String originalIp) {
        this.originalIp = originalIp;
    }

    public String getMaskedIp() {
        return maskedIp;
    }

    public void setMaskedIp(String maskedIp) {
        this.maskedIp = maskedIp;
    }

    public Boolean getConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<ServiceProvider> getServiceProviderList() {
        return serviceProviderList;
    }

    public void setServiceProviderList(List<ServiceProvider> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    public Country getOriginalCountry() {
        return this.originalCountry;
    }

    public void setOriginalCountry(Country originalCountry) {
        this.originalCountry = originalCountry;
    }
}
