package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.exception.BadRequestException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.EnrollmentRepository;
import edu.co.icesi.proyectofinal.repository.ScheduleRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.services.ActivityService;
import edu.co.icesi.proyectofinal.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;
    private final SpaceRepository spaceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    public ActivityServiceImpl(ActivityRepository activityRepository, SpaceRepository spaceRepository,
                               EnrollmentRepository enrollmentRepository, ScheduleRepository scheduleRepository,
                               NotificationService notificationService) {
        this.activityRepository = activityRepository;
        this.spaceRepository = spaceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.notificationService = notificationService;
    }

    /** Tipos de evento que se pueden filtrar (HU8.2). */
    public static final List<String> TYPES = List.of("CLASE", "TORNEO", "GIMNASIO");
    private static final String DEFAULT_TYPE = "CLASE";

    @Override
    public List<Activity> search(boolean upcoming, LocalDate from, LocalDate to, String type) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final");
        }
        String wantedType = type == null || type.isBlank() ? null : normalizeType(type);
        LocalDate today = LocalDate.now();
        return activityRepository.findAll().stream()
                .filter(activity -> wantedType == null || wantedType.equals(activity.getType()))
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
        Activity saved = activityRepository.save(activity);
        notifyNewActivity(saved);
        return saved;
    }

    // HU15: avisa a todos los usuarios. Si el aviso falla, el evento igual queda creado.
    private void notifyNewActivity(Activity activity) {
        try {
            notificationService.notifyAllUsers(NotificationService.TYPE_NEW_ACTIVITY,
                    "Nuevo evento: " + activity.getName(), activity.getIdActivity(), NotificationService.REF_ACTIVITY);
        } catch (RuntimeException ex) {
            log.warn("No se pudo notificar el evento {}: {}", activity.getIdActivity(), ex.getMessage());
        }
    }

    @Override
    public Activity update(Integer id, Activity activity) {
        Activity existing = findById(id);
        validateDates(activity);
        existing.setName(activity.getName());
        existing.setDescription(activity.getDescription());
        existing.setType(typeOrDefault(activity.getType()));
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

    private String typeOrDefault(String type) {
        return type == null || type.isBlank() ? DEFAULT_TYPE : normalizeType(type);
    }

    private String normalizeType(String type) {
        String normalized = type.trim().toUpperCase();
        if (!TYPES.contains(normalized)) {
            throw new BadRequestException("El tipo de evento debe ser clase, torneo o gimnasio");
        }
        return normalized;
    }

    private void validateDates(Activity activity) {
        if (activity.getEndDate().isBefore(activity.getStartDate())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}
