package com.pranavaeet.astro.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="planetdata")

public class PlanetData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String planetname;
    private double meanlongitude;
    private double semimajoraxis;
    private double eccentricity;
    private double inclination;
    private double longitudeofacendingnode;
    private double logitudeofperihelion;
    private double meananomaly;
    private double orbitalperiod;
    @JsonIgnore
    private int page=1;
    private int size =10;

    // Getters
//    public String getPlanetName() { return planetname; }
//    public double getMeanLongitude() { return meanlongitude; }
//    public double getSemiMajorAxis() { return semimajoraxis; }
//    public double getEccentricity() { return eccentricity; }
//    public double getInclination() { return inclination; }
//    public double getLongitudeOfAscendingNode() { return longitudeofacendingnode; }
//    public double getLongitudeOfPerihelion() { return logitudeofperihelion; }
//    public double getMeanAnomaly() { return meananomaly; }
//    public double getOrbitalPeriod() { return orbitalperiod; }
//
//    // Setters
//    public void setPlanetName(String planetName) { this.planetname = planetName; }
//    public void setMeanLongitude(double meanLongitude) { this.meanlongitude = meanLongitude; }
//    public void setSemiMajorAxis(double semiMajorAxis) { this.semimajoraxis = semiMajorAxis; }
//    public void setEccentricity(double eccentricity) { this.eccentricity = eccentricity; }
//    public void setInclination(double inclination) { this.inclination = inclination; }
//    public void setLongitudeOfAscendingNode(double longitudeOfAscendingNode) { this.longitudeofacendingnode = longitudeOfAscendingNode; }
//    public void setLongitudeOfPerihelion(double longitudeOfPerihelion) { this.logitudeofperihelion = longitudeOfPerihelion; }
//    public void setMeanAnomaly(double meanAnomaly) { this.meananomaly = meanAnomaly; }
//    public void setOrbitalPeriod(double orbitalPeriod) { this.orbitalperiod = orbitalPeriod; }
}


