package com.pranavaeet.astro.service;

import com.pranavaeet.astro.entity.PlanetData;
import com.pranavaeet.astro.repository.PlanetDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculationService {
    @Autowired
    PlanetDataRepository planetDataRepository;
    public OutputStructure PlanetCalculation(PlanetData planetData,double observerLatitude,double observerLongitude, String dateTime){
        String name=planetData.getPlanetname();
        double eccentricity =  planetData.getEccentricity();
        System.out.println(eccentricity+"in planet calc");
        double inclination = Math.toRadians(planetData.getInclination());
        double longitudeOfPerihelion = Math.toRadians(planetData.getLogitudeofperihelion());
        double longitudeOfAscendingNode = Math.toRadians(planetData.getLongitudeofacendingnode());
        double meanLongitude = Math.toRadians(planetData.getMeanlongitude());
        double orbitalPeriod = planetData.getOrbitalperiod();
        double semiMajorAxis = planetData.getSemimajoraxis();

        OutputStructure output =new OutputStructure();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        double julianDate=toJulianDate(localDateTime);
        System.out.println(dateTime+" "+localDateTime+" "+julianDate);


         double observerlatitude = Math.toRadians(observerLatitude);
         double observerlongitude = Math.toRadians(observerLongitude);


        // Calculate heliocentric position of the planet
        double[] planetHelioCoords = calculatePlanetPosition(eccentricity, inclination, longitudeOfPerihelion,
                longitudeOfAscendingNode, meanLongitude, orbitalPeriod, semiMajorAxis,julianDate);

        // Calculate heliocentric position of the Earth
        double[] earthHelioCoords = calculatePlanetPosition(0.0167,
                Math.toRadians(0.00005),
                Math.toRadians(102.9373),
                Math.toRadians(-11.26064),
                Math.toRadians(100.46435),
                365.25,
                1.000001018,
                julianDate);

        // Transform to geocentric coordinates
        double[] geocentricCoords = new double[3];
        geocentricCoords[0] = planetHelioCoords[0] - earthHelioCoords[0];
        geocentricCoords[1] = planetHelioCoords[1] - earthHelioCoords[1];
        geocentricCoords[2] = planetHelioCoords[2] - earthHelioCoords[2];

        // Convert to spherical coordinates
        double r_geocentric = Math.sqrt(geocentricCoords[0] * geocentricCoords[0] +
                geocentricCoords[1] * geocentricCoords[1] +
                geocentricCoords[2] * geocentricCoords[2]);
        double theta_geocentric = Math.atan2(geocentricCoords[1], geocentricCoords[0]); // azimuthal angle
        double phi_geocentric = Math.acos(geocentricCoords[2] / r_geocentric); // polar angle

        output.planetname=name;

        output.r=r_geocentric;
        output.theta=(Math.toDegrees(theta_geocentric));
        output.phi=(Math.toDegrees(phi_geocentric));

        // Convert to local observer coordinates (altitude and azimuth)
        double[] localCoords = convertToLocal(observerlatitude,observerlongitude,julianDate, geocentricCoords);
        output.elevation=(Math.toDegrees(localCoords[0]));
        output.azimuth=(Math.toDegrees(localCoords[1]));

        return output;
    }
    public List<OutputStructure> LocationCalculator(double observerLatitude,double observerLongitude,String dateTime) {

        List<PlanetData> AllPlanetData = planetDataRepository.findAll();
        List<OutputStructure> ans = new ArrayList<>();
        for (PlanetData planetData: AllPlanetData){
            System.out.println(planetData.getPlanetname());
            System.out.println(planetData.getEccentricity());
            System.out.println(planetData.getInclination());
        }

         for (PlanetData planetData : AllPlanetData) {
            OutputStructure p=PlanetCalculation(planetData,observerLatitude,observerLongitude,dateTime);
            ans.add(p);
             System.out.println(p.planetname+"in for loop");
             System.out.println(p.r);
             System.out.println(p.azimuth);
             System.out.println(p.phi);
             System.out.println(p.elevation);
             System.out.println(p.theta);
         }
         return ans;

    }

    public static double[] calculatePlanetPosition(double eccentricity, double inclination,
                                                   double longitudeOfPerihelion, double longitudeOfAscendingNode,
                                                   double meanLongitude, double orbitalPeriod, double semiMajorAxis,double julianDate) {

        // Calculate the mean anomaly
        double meanAnomaly = meanLongitude - longitudeOfPerihelion;

        // Solve Kepler's Equation for eccentric anomaly
        double eccentricAnomaly = solveKeplersEquation(meanAnomaly, eccentricity);

        // Calculate the true anomaly
        double trueAnomaly = 2 * Math.atan2(
                Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2),
                Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2));

        // Calculate the heliocentric distance
        double r = semiMajorAxis * (1 - eccentricity * Math.cos(eccentricAnomaly));

        // Calculate the heliocentric coordinates in the orbital plane
        double x_orb = r * Math.cos(trueAnomaly);
        double y_orb = r * Math.sin(trueAnomaly);

        // Convert to ecliptic coordinates
        double x_ecl = x_orb * (Math.cos(longitudeOfAscendingNode) * Math.cos(longitudeOfPerihelion) - Math.sin(longitudeOfAscendingNode) * Math.sin(longitudeOfPerihelion) * Math.cos(inclination))
                - y_orb * (Math.sin(longitudeOfAscendingNode) * Math.cos(longitudeOfPerihelion) + Math.cos(longitudeOfAscendingNode) * Math.sin(longitudeOfPerihelion) * Math.cos(inclination));
        double y_ecl = x_orb * (Math.cos(longitudeOfAscendingNode) * Math.sin(longitudeOfPerihelion) + Math.sin(longitudeOfAscendingNode) * Math.cos(longitudeOfPerihelion) * Math.cos(inclination))
                + y_orb * (Math.cos(longitudeOfAscendingNode) * Math.cos(longitudeOfPerihelion) - Math.sin(longitudeOfAscendingNode) * Math.sin(longitudeOfPerihelion) * Math.cos(inclination));
        double z_ecl = x_orb * (Math.sin(inclination) * Math.sin(longitudeOfAscendingNode))
                + y_orb * (Math.sin(inclination) * Math.cos(longitudeOfAscendingNode));

        return new double[]{x_ecl, y_ecl, z_ecl};
    }

    public static double solveKeplersEquation(double meanAnomaly, double eccentricity) {
        double E = meanAnomaly;
        double delta = 1e-6;
        double E1 = E - (E - eccentricity * Math.sin(E) - meanAnomaly) / (1 - eccentricity * Math.cos(E));

        while (Math.abs(E1 - E) > delta) {
            E = E1;
            E1 = E - (E - eccentricity * Math.sin(E) - meanAnomaly) / (1 - eccentricity * Math.cos(E));
        }

        return E1;
    }

    public static double toJulianDate(LocalDateTime dateTime) {
        LocalDateTime epoch = LocalDateTime.of(2000, 1, 1, 12, 0);
        double daysSinceEpoch = Duration.between(epoch, dateTime).toDays() +
                (double) Duration.between(epoch, dateTime).toMinutes() / 1440.0;
        return 2451545.0 + daysSinceEpoch;
    }

    public static double[] convertToLocal(double observerLatitude, double observerLongitude, double julianDate, double[] geocentricCoords) {
        double x = geocentricCoords[0];
        double y = geocentricCoords[1];
        double z = geocentricCoords[2];
        double r = Math.sqrt(x * x + y * y + z * z);
        double rightAscension = Math.atan2(y, x);
        double declination = Math.asin(z / r);
        double lst = localSiderealTime(observerLongitude, julianDate);
        double hourAngle = lst - rightAscension;
        double altitude = Math.asin(Math.sin(observerLatitude) * Math.sin(declination) + Math.cos(observerLatitude) * Math.cos(declination) * Math.cos(hourAngle));
        double azimuth = Math.atan2(-Math.sin(hourAngle), Math.tan(declination) * Math.cos(observerLatitude) - Math.sin(observerLatitude) * Math.cos(hourAngle));
        return new double[]{altitude, azimuth};
    }

    public static double localSiderealTime(double observerLongitude, double julianDate) {
        double jd0 = Math.floor(julianDate + 0.5) - 0.5;
        double H = (julianDate - jd0) * 24.0;
        double D = julianDate - 2451545.0;
        double D0 = jd0 - 2451545.0;
        double GMST = 6.697374558 + 0.06570982441908 * D0 + 1.00273790935 * H;
        double LST = GMST + observerLongitude / 15.0;
        return Math.toRadians((LST % 24) * 15);
    }


}
