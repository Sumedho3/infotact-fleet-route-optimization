package com.infotact.fleet.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {
	
	@NotNull(message = "Target vehicle ID allocation parameter is required")
	private Long vehicleId;
	
	@NotEmpty(message = "Route optimization requires an array listing at least one delivery task destination ID target")
    private List<Long> deliveryTaskIds; // Array list of target drops to organize sequence optimization

}
