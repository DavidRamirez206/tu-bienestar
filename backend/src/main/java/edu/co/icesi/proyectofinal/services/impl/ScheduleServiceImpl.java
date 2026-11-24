package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Schedule;
import edu.co.icesi.proyectofinal.exception.BadRequestException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.ScheduleRepository;
import edu.co.icesi.proyectofinal.services.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Comparator<Schedule> BY_DAY_AND_TIME = Comparator
            .comparing((Schedule schedule) -> DayOfWeek.valueOf(schedule.getDayOfWeek()))
            .thenComparing(Schedule::getStartTime);

    private final ScheduleRepository scheduleRepository;
    private final ActivityRepository activityRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ActivityRepository activityRepository) {
        this.scheduleRepository = scheduleRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Schedule> search(String dayOfWeek, String type) {
        String wantedDay = dayOfWeek == null || dayOfWeek.isBlank() ? null : normalizeDay(dayOfWeek);
        String wantedType = type == null || type.isBlank() ? null : type.trim().toUpperCase();
        return scheduleRepository.findAll().stream()
                .filter(schedule -> wantedDay == null || wantedDay.equals(schedule.getDayOfWeek()))
                .filter(schedule -> wantedType == null || wantedType.equals(schedule.getActivity().getType()))
                .sorted(BY_DAY_AND_TIME)
                .toList();
    }

    @Override
    public List<Schedule> findByActivityId(Integer activityId) {
        findActivity(activityId);
        return scheduleRepository.findByActivityIdActivity(activityId).stream()
                .sorted(BY_DAY_AND_TIME)
                .toList();
    }

    @Override
    public Schedule findById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule with id " + id + " not found"));
    }

    @Override
    public Schedule create(Integer activityId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Schedule schedule = new Schedule();
        fill(schedule, activityId, dayOfWeek, startTime, endTime);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule update(Integer id, Integer activityId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Schedule schedule = findById(id);
        fill(schedule, activityId, dayOfWeek, startTime, endTime);
        return scheduleRepository.save(schedule);
    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.delete(findById(id));
    }

    private void fill(Schedule schedule, Integer activityId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
        schedule.setActivity(findActivity(activityId));
        schedule.setDayOfWeek(normalizeDay(dayOfWeek));
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
    }

    private Activity findActivity(Integer activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new BadRequestException("El evento " + activityId + " no existe"));
    }

    private String normalizeDay(String dayOfWeek) {
        try {
            return DayOfWeek.valueOf(dayOfWeek.trim().toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("El día debe ser MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY o SUNDAY");
        }
    }
}
