package com.infotact.fleet.repository;

import com.infotact.fleet.entity.DeliveryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {

    List<DeliveryTask> findByStatus(String status);

    List<DeliveryTask> findByRouteId(Long routeId);

    List<DeliveryTask> findByStatusAndRouteIsNull(String status);
}
