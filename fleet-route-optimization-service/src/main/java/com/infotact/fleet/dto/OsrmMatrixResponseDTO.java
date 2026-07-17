package com.infotact.fleet.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "OsrmMatrixResponse",
        description = "Data schema handling dynamic geographic multi-stop node matrix outputs from remote servers"
)
public class OsrmMatrixResponseDTO {

    @Schema(description = "OSRM engine service operational transaction confirmation response code status value string", example = "Ok")
    @JsonProperty("code")
    private String code;

    @Schema(
        description = "Two-dimensional distance array displaying structural spatial path cost weights tracking grid relationships between sequential nodes measured in meters (m)", 
        example = "[[0.0, 1540.5], [1535.2, 0.0]]"
    )
    @JsonProperty("distances")
    private double[][] distances;

    @Schema(
        description = "Two-dimensional duration array displaying dynamic temporal path cost tracking grids between sequential nodes measured in total seconds (s)", 
        example = "[[0.0, 120.4], [118.9, 0.0]]"
    )
    @JsonProperty("durations")
    private double[][] durations;
}