package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Enrollment;
import edu.co.icesi.proyectofinal.entity.keys.EnrollmentId;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.exception.ConflictException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.EnrollmentRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, ActivityRepository activityRepository,
                                 UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Enrollment enroll(Integer userId, Integer activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity with id " + activityId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
        EnrollmentId id = id(userId, activityId);
        if (enrollmentRepository.existsById(id)) {
            throw new ConflictException("Ya estás inscrito en esta actividad");
        }
        if (activity.getEndDate() != null && activity.getEndDate().isBefore(LocalDate.now())) {
            throw new ConflictException("Esta actividad ya terminó");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        enrollment.setUser(user);
        enrollment.setActivity(activity);
        enrollment.setEnrollmentDate(LocalDate.now());
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void cancel(Integer userId, Integer activityId) {
        enrollmentRepository.delete(findById(userId, activityId));
    }

    @Override
    public Enrollment findById(Integer userId, Integer activityId) {
        return enrollmentRepository.findById(id(userId, activityId))
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> findByUserId(Integer userId) {
        return enrollmentRepository.findByUserIdUser(userId);
    }

    private EnrollmentId id(Integer userId, Integer activityId) {
        EnrollmentId id = new EnrollmentId();
        id.setUserId(userId);
        id.setActivityId(activityId);
        return id;
    }
}
