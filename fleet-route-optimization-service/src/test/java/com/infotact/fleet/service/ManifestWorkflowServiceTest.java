package com.infotact.fleet.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infotact.fleet.entity.RouteManifest;
import com.infotact.fleet.exception.DeliveryStateConflictException;
import com.infotact.fleet.model.ManifestStatus;
import com.infotact.fleet.repository.RouteManifestRepository;

@ExtendWith(MockitoExtension.class)
class ManifestWorkflowServiceTest {

    @Mock
    private RouteManifestRepository routeManifestRepository;

    @Mock
    private TaskStateService taskStateService;

    @InjectMocks
    private ManifestWorkflowServiceImpl manifestWorkflowService;

    @Test
    @DisplayName("Driver Link Success")
    void testCompileAndLinkManifestSuccess() {

        Long manifestId = 10L;
        Long driverId = 5L;

        RouteManifest manifest = new RouteManifest();
        manifest.setId(manifestId);
        manifest.setStatus(ManifestStatus.UNASSIGNED);

        when(routeManifestRepository.findById(manifestId))
                .thenReturn(Optional.of(manifest));

        when(routeManifestRepository.existsByDriverIdAndStatusIn(eq(driverId), any()))
                .thenReturn(false);

        when(routeManifestRepository.save(any(RouteManifest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RouteManifest result =
                manifestWorkflowService.compileAndLinkManifest(manifestId, driverId);

        assertNotNull(result);
        assertEquals(driverId, result.getDriverId());

        verify(routeManifestRepository).save(manifest);
    }

    @Test
    @DisplayName("Driver Already Assigned")
    void testCompileAndLinkManifestDriverConflict() {

        Long manifestId = 10L;
        Long driverId = 5L;

        RouteManifest manifest = new RouteManifest();
        manifest.setId(manifestId);
        manifest.setStatus(ManifestStatus.UNASSIGNED);

        when(routeManifestRepository.findById(manifestId))
                .thenReturn(Optional.of(manifest));

        when(routeManifestRepository.existsByDriverIdAndStatusIn(eq(driverId), any()))
                .thenReturn(true);

        assertThrows(
                DeliveryStateConflictException.class,
                () -> manifestWorkflowService.compileAndLinkManifest(manifestId, driverId));

        verify(routeManifestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dispatch Success")
    void testFinalizeDispatchSuccess() {

        Long manifestId = 12L;

        RouteManifest manifest = new RouteManifest();
        manifest.setId(manifestId);
        manifest.setDriverId(7L);
        manifest.setStatus(ManifestStatus.UNASSIGNED);

        RouteManifest dispatched = new RouteManifest();
        dispatched.setId(manifestId);
        dispatched.setDriverId(7L);
        dispatched.setStatus(ManifestStatus.DISPATCHED);

        when(routeManifestRepository.findById(manifestId))
                .thenReturn(Optional.of(manifest));

        when(taskStateService.updateManifestAndCascadeStatus(
                manifestId,
                ManifestStatus.DISPATCHED))
                .thenReturn(dispatched);

        when(routeManifestRepository.save(any(RouteManifest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RouteManifest result =
                manifestWorkflowService.finalizeDispatch(manifestId);

        assertNotNull(result);
        assertEquals(ManifestStatus.DISPATCHED, result.getStatus());
        assertNotNull(result.getDispatchedAt());

        verify(taskStateService)
                .updateManifestAndCascadeStatus(manifestId,
                        ManifestStatus.DISPATCHED);

        verify(routeManifestRepository).save(result);
    }
}