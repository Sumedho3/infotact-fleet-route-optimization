package com.infotact.fleet.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infotact.fleet.dto.DriverResponseDTO;
import com.infotact.fleet.entity.Driver;
import com.infotact.fleet.entity.Vehicle;
import com.infotact.fleet.repository.DriverRepository;
import com.infotact.fleet.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class FleetServiceAssignmentTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private FleetServiceImpl fleetService;

    private Driver validDriver;
    private Vehicle stableVehicle;
    private Vehicle brokenVehicle;

    @BeforeEach
    void setUp() {

        validDriver = new Driver();
        validDriver.setId(10L);
        validDriver.setFullName("Soumya");
        validDriver.setLicenseExpiryDate(LocalDate.now().plusYears(2));

        stableVehicle = new Vehicle();
        stableVehicle.setId(20L);
        stableVehicle.setLicensePlate("MH-12-FL-9999");
        stableVehicle.setMaintenanceStatus(false);

        brokenVehicle = new Vehicle();
        brokenVehicle.setId(30L);
        brokenVehicle.setLicensePlate("MH-12-FAIL-1111");
        brokenVehicle.setMaintenanceStatus(true);
    }

    @Test
    void shouldCompleteAssignmentWhenPayloadConfigurationsAreValid() {

        Mockito.when(driverRepository.findById(10L))
                .thenReturn(Optional.of(validDriver));

        Mockito.when(vehicleRepository.findById(20L))
                .thenReturn(Optional.of(stableVehicle));

        Mockito.when(driverRepository.save(Mockito.any(Driver.class)))
                .thenReturn(validDriver);

        Mockito.when(vehicleRepository.save(Mockito.any(Vehicle.class)))
                .thenReturn(stableVehicle);

        DriverResponseDTO result =
                fleetService.assignDriverToVehicle(10L, 20L);

        assertNotNull(result);
        assertEquals("MH-12-FL-9999",
                result.getAssignedVehiclePlate());

        Mockito.verify(driverRepository,
                Mockito.times(1)).save(validDriver);
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsUnderMaintenance() {

        Mockito.when(driverRepository.findById(10L))
                .thenReturn(Optional.of(validDriver));

        Mockito.when(vehicleRepository.findById(30L))
                .thenReturn(Optional.of(brokenVehicle));

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> {
                    fleetService.assignDriverToVehicle(10L, 30L);
                });

        assertTrue(exception.getMessage().contains("under maintenance"));

        Mockito.verify(driverRepository,
                Mockito.never()).save(Mockito.any(Driver.class));
    }
}