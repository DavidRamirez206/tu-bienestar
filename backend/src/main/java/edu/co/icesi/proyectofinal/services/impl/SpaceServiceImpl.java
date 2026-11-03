package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.exception.ConflictException;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.services.SpaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final ActivityRepository activityRepository;

    public SpaceServiceImpl(SpaceRepository spaceRepository, ActivityRepository activityRepository) {
        this.spaceRepository = spaceRepository;
        this.activityRepository = activityRepository;
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
        return spaceRepository.save(space);
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
