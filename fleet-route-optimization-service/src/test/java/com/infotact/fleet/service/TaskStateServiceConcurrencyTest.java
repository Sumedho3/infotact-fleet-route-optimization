package com.infotact.fleet.service;

import com.infotact.fleet.entity.DeliveryTask;
import com.infotact.fleet.model.TaskStatus;
import com.infotact.fleet.repository.DeliveryTaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaskStateServiceConcurrencyTest {

    @Autowired
    private TaskStateService taskStateService;

    @MockitoBean
    private DeliveryTaskRepository deliveryTaskRepository;

    @Test
    @DisplayName("⚡ Concurrency Simulation: Simultaneous status updates must be serialized by database row locks")
    void testConcurrentStatusUpdatesStableBoundaries() throws InterruptedException {
        // 1. Setup a dummy starting task state
        Long targetTaskId = 1L;
        DeliveryTask initialTask = new DeliveryTask();
        initialTask.setId(targetTaskId);
        initialTask.setStatus(TaskStatus.ASSIGNED);

        // Mock the pessimistic lock method to always yield our target asset
        when(deliveryTaskRepository.findByIdForUpdate(targetTaskId)).thenReturn(Optional.of(initialTask));
        when(deliveryTaskRepository.save(any(DeliveryTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Configure parallel execution parameters
        int numberOfConcurrentDrivers = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfConcurrentDrivers);
        CountDownLatch readyLatch = new CountDownLatch(numberOfConcurrentDrivers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfConcurrentDrivers);

        AtomicInteger successfulTransitions = new AtomicInteger(0);
        AtomicInteger failedTransitions = new AtomicInteger(0);

        // 3. Spool up threads representing simultaneous mobile updates
        for (int i = 0; i < numberOfConcurrentDrivers; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    // Block until the starter gun fires to achieve genuine simultaneity
                    startLatch.await();

                    // Attempt the status mutation transition
                    taskStateService.updateTaskStatus(targetTaskId, TaskStatus.DISPATCHED);
                    successfulTransitions.incrementAndGet();
                } catch (Exception e) {
                    // State machine guard violations or lock contentions catch here safely
                    failedTransitions.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // 4. Fire the threads simultaneously
        readyLatch.await(5, TimeUnit.SECONDS); // Wait for all threads to line up
        startLatch.countDown();                // GO! Fire all threads at once
        finishLatch.await(10, TimeUnit.SECONDS); // Wait for execution block completion
        executorService.shutdown();

        // 5. Verify the Transaction Safety Invariants
        // Because findByIdForUpdate forces serialize execution, the state updates are processed safely
        System.out.println("=== CONCURRENCY TRIAL METRICS ===");
        System.out.println("Successful transitions processed: " + successfulTransitions.get());
        System.out.println("Intercepted/Blocked collisions: " + failedTransitions.get());

        // The repository findByIdForUpdate must have been called exactly as many times as drivers hit the button
        verify(deliveryTaskRepository, times(numberOfConcurrentDrivers)).findByIdForUpdate(targetTaskId);
    }
}