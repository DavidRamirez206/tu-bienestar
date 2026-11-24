package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.exception.ConflictException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.services.NotificationService;
import edu.co.icesi.proyectofinal.services.SpaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    private static final Logger log = LoggerFactory.getLogger(SpaceServiceImpl.class);

    private final SpaceRepository spaceRepository;
    private final ActivityRepository activityRepository;
    private final NotificationService notificationService;

    public SpaceServiceImpl(SpaceRepository spaceRepository, ActivityRepository activityRepository,
                            NotificationService notificationService) {
        this.spaceRepository = spaceRepository;
        this.activityRepository = activityRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<Space> findAll() {
        return spaceRepository.findAll();
    }

    @Override
    public Space findById(Integer id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space with id " + id + " not found"));
    }

    @Override
    public Space save(Space space) {
        Space saved = spaceRepository.save(space);
        notifyNewSpace(saved);
        return saved;
    }

    // HU15: avisa a todos los usuarios. Si el aviso falla, el espacio igual queda creado.
    private void notifyNewSpace(Space space) {
        try {
            notificationService.notifyAllUsers(NotificationService.TYPE_NEW_SPACE,
                    "Nuevo espacio disponible: " + space.getName(), space.getIdSpace(), NotificationService.REF_SPACE);
        } catch (RuntimeException ex) {
            log.warn("No se pudo notificar el espacio {}: {}", space.getIdSpace(), ex.getMessage());
        }
    }

    @Override
    public Space update(Integer id, Space space) {
        Space existing = findById(id);
        existing.setName(space.getName());
        existing.setCapacity(space.getCapacity());
        existing.setLocation(space.getLocation());
        return spaceRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        Space existing = findById(id);
        if (activityRepository.countBySpaceIdSpace(id) > 0) {
            throw new ConflictException("No se puede eliminar un espacio que tiene eventos");
        }
        spaceRepository.delete(existing);
    }
}
