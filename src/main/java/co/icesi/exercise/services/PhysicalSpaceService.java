package co.icesi.exercise.services;

import co.icesi.exercise.model.PhysicalSpace;
import co.icesi.exercise.repositories.PhysicalSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhysicalSpaceService {

    @Autowired
    private PhysicalSpaceRepository physicalSpaceRepository;

    public List<PhysicalSpace> getAllPhysicalSpaces() {
        return physicalSpaceRepository.findAll();
    }

    public PhysicalSpace getPhysicalSpaceById(int id) {
        return physicalSpaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espacio físico no encontrado con id: " + id));
    }

    public List<PhysicalSpace> searchPhysicalSpacesByName(String name) {
        return physicalSpaceRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public PhysicalSpace createPhysicalSpace(PhysicalSpace physicalSpace) {
        return physicalSpaceRepository.save(physicalSpace);
    }

    @Transactional
    public PhysicalSpace updatePhysicalSpace(int id, PhysicalSpace updatedPhysicalSpace) {
        PhysicalSpace existingPhysicalSpace = getPhysicalSpaceById(id);
        existingPhysicalSpace.setName(updatedPhysicalSpace.getName());
        existingPhysicalSpace.setLocation(updatedPhysicalSpace.getLocation());
        existingPhysicalSpace.setCapacity(updatedPhysicalSpace.getCapacity());
        return physicalSpaceRepository.save(existingPhysicalSpace);
    }

    @Transactional
    public void deletePhysicalSpace(int id) {
        PhysicalSpace existingPhysicalSpace = getPhysicalSpaceById(id);
        physicalSpaceRepository.delete(existingPhysicalSpace);
    }
}
