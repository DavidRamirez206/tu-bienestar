package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.exception.BadRequestException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.EnrollmentRepository;
import edu.co.icesi.proyectofinal.repository.ScheduleRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.services.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final SpaceRepository spaceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ScheduleRepository scheduleRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, SpaceRepository spaceRepository,
                               EnrollmentRepository enrollmentRepository, ScheduleRepository scheduleRepository) {
        this.activityRepository = activityRepository;
        this.spaceRepository = spaceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Activity> search(boolean upcoming, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final");
        }
        LocalDate today = LocalDate.now();
        return activityRepository.findAll().stream()
                .filter(activity -> !upcoming || !endsBefore(activity, today))
                .filter(activity -> from == null || !endsBefore(activity, from))
                .filter(activity -> to == null || activity.getStartDate() == null || !activity.getStartDate().isAfter(to))
                .sorted(Comparator.comparing(Activity::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private boolean endsBefore(Activity activity, LocalDate date) {
        return activity.getEndDate() != null && activity.getEndDate().isBefore(date);
    }

    @Override
    public Activity findById(Integer id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity with id " + id + " not found"));
    }

    @Override
    public Activity save(Activity activity) {
        activity.setSpace(resolveSpace(activity));
        validateDates(activity);
        return activityRepository.save(activity);
    }

    @Override
    public Activity update(Integer id, Activity activity) {
        Activity existing = findById(id);
        validateDates(activity);
        existing.setName(activity.getName());
        existing.setDescription(activity.getDescription());
        existing.setStartDate(activity.getStartDate());
        existing.setEndDate(activity.getEndDate());
        existing.setSpace(resolveSpace(activity));
        return activityRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Activity existing = findById(id);
        enrollmentRepository.deleteAll(enrollmentRepository.findByActivityIdActivity(id));
        scheduleRepository.deleteAll(scheduleRepository.findByActivityIdActivity(id));
        activityRepository.delete(existing);
    }

    private Space resolveSpace(Activity activity) {
        Integer spaceId = activity.getSpace().getIdSpace();
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BadRequestException("El espacio " + spaceId + " no existe"));
    }

    private void validateDates(Activity activity) {
        if (activity.getEndDate().isBefore(activity.getStartDate())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}
