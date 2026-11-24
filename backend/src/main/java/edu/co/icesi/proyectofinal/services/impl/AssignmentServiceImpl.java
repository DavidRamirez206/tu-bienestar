package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import edu.co.icesi.proyectofinal.exception.BadRequestException;
import edu.co.icesi.proyectofinal.exception.ConflictException;
import edu.co.icesi.proyectofinal.repository.AssignmentRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.AssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final String TRAINER_ROLE = "TRAINER";

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Assignment assign(Integer userId, Integer trainerId) {
        User user = user(userId);
        User trainer = trainer(trainerId, userId);
        if (assignmentRepository.existsById(id(userId, trainerId))) {
            throw new ConflictException("El usuario ya está asignado a este entrenador");
        }
        if (assignmentRepository.existsByUserAssignmentIdUser(userId)) {
            throw new ConflictException("El usuario ya tiene un entrenador asignado. Usa reasignar para cambiarlo");
        }
        return save(user, trainer);
    }

    @Override
    @Transactional
    public Assignment reassign(Integer userId, Integer trainerId) {
        User user = user(userId);
        User trainer = trainer(trainerId, userId);
        List<Assignment> current = assignmentRepository.findByUserAssignmentIdUser(userId);
        if (current.isEmpty()) {
            throw new RuntimeException("Assignment not found");
        }
        if (current.stream().anyMatch(assignment -> assignment.getId().getTrainerId().equals(trainerId))) {
            throw new ConflictException("El usuario ya está asignado a este entrenador");
        }
        // La llave incluye al entrenador: reasignar es quitar la asignacion anterior y crear la nueva.
        current.forEach(this::remove);
        assignmentRepository.flush();
        return save(user, trainer);
    }

    @Override
    @Transactional
    public void unassign(Integer userId, Integer trainerId) {
        remove(findById(userId, trainerId));
    }

    @Override
    public Assignment findById(Integer userId, Integer trainerId) {
        return assignmentRepository.findById(id(userId, trainerId))
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    @Override
    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    @Override
    public List<Assignment> findByUserId(Integer userId) {
        return assignmentRepository.findByUserAssignmentIdUser(userId);
    }

    @Override
    public List<Assignment> findByTrainerId(Integer trainerId) {
        return assignmentRepository.findByTrainerAssignmentIdUser(trainerId);
    }

    @Override
    public List<User> findUsersByRole(String roleName) {
        return userRepository.findDistinctByUserRolesRoleName(roleName);
    }

    private User user(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
    }

    private User trainer(Integer trainerId, Integer userId) {
        if (trainerId.equals(userId)) {
            throw new BadRequestException("Un usuario no puede ser su propio entrenador");
        }
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer with id " + trainerId + " not found"));
        boolean isTrainer = trainer.getUserRoles() != null && trainer.getUserRoles().stream()
                .anyMatch(userRole -> TRAINER_ROLE.equals(userRole.getRole().getName()));
        if (!isTrainer) {
            throw new BadRequestException("El usuario seleccionado no es entrenador");
        }
        return trainer;
    }

    private Assignment save(User user, User trainer) {
        Assignment assignment = new Assignment();
        assignment.setId(id(user.getIdUser(), trainer.getIdUser()));
        assignment.setUserAssignment(user);
        assignment.setTrainerAssignment(trainer);
        assignment.setAssignmentDate(LocalDate.now());
        return assignmentRepository.save(assignment);
    }

    private void remove(Assignment assignment) {
        // Se saca tambien de las listas de User para que el cascade no la vuelva a guardar.
        // Se compara por llave porque equals de Lombok recorre las relaciones.
        if (assignment.getUserAssignment().getAssignments() != null) {
            assignment.getUserAssignment().getAssignments()
                    .removeIf(other -> other.getId().equals(assignment.getId()));
        }
        if (assignment.getTrainerAssignment().getTrainerAssignments() != null) {
            assignment.getTrainerAssignment().getTrainerAssignments()
                    .removeIf(other -> other.getId().equals(assignment.getId()));
        }
        assignmentRepository.delete(assignment);
    }

    private AssignmentId id(Integer userId, Integer trainerId) {
        AssignmentId id = new AssignmentId();
        id.setUserId(userId);
        id.setTrainerId(trainerId);
        return id;
    }
}
