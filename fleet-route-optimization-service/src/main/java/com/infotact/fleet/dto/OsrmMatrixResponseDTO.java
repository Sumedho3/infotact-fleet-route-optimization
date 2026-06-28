package com.infotact.fleet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OsrmMatrixResponseDTO {

    // 🎯 OSRM Status Code (e.g., "Ok" indicates successful calculation)
    @JsonProperty("code")
    private String code;

    // Two-dimensional array tracking distance cost values between nodes in meters
    @JsonProperty("distances")
    private double[][] distances;

    // Two-dimensional array tracking travel duration values between nodes in seconds
    @JsonProperty("durations")
    private double[][] durations;
}