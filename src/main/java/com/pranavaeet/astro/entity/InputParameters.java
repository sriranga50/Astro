package com.pranavaeet.astro.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputParameters {
    private double lat;
    private double lon;
    private String datetime;

}
