package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByActivityIdActivity(Integer activityId);

    List<Schedule> findByActivityName(String activityName);

    Optional<Schedule> findTopByOrderByStartTimeAsc();

    boolean existsByActivityIdActivity(Integer activityId);

    boolean existsByActivityName(String activityName);
}
