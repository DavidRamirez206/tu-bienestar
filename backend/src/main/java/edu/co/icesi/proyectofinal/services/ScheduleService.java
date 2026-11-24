package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Schedule;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleService {
    /**
     * Lista los horarios ordenados por dia y hora de inicio.
     *
     * @param dayOfWeek si llega, solo los de ese dia (MONDAY ... SUNDAY)
     * @param type      si llega, solo los de eventos de ese tipo
     */
    List<Schedule> search(String dayOfWeek, String type);
    List<Schedule> findByActivityId(Integer activityId);
    Schedule findById(Integer id);
    Schedule create(Integer activityId, String dayOfWeek, LocalTime startTime, LocalTime endTime);
    Schedule update(Integer id, Integer activityId, String dayOfWeek, LocalTime startTime, LocalTime endTime);
    void delete(Integer id);
}
