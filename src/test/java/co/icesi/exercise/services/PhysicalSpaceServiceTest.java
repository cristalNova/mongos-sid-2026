package co.icesi.exercise.services;

import co.icesi.exercise.model.PhysicalSpace;
import co.icesi.exercise.repositories.PhysicalSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PhysicalSpaceServiceTest {

    @Mock
    private PhysicalSpaceRepository physicalSpaceRepository;

    @InjectMocks
    private PhysicalSpaceService physicalSpaceService;

    private PhysicalSpace space;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        space = new PhysicalSpace();
        space.setId(1);
        space.setName("Gym");
        space.setLocation("Building A");
        space.setCapacity(100);
    }

    @Test
    void getAllPhysicalSpaces_ShouldReturnList() {
        when(physicalSpaceRepository.findAll()).thenReturn(List.of(space));

        List<PhysicalSpace> result = physicalSpaceService.getAllPhysicalSpaces();

        assertEquals(1, result.size());
        verify(physicalSpaceRepository).findAll();
    }
    @Test
    void getPhysicalSpaceById_ShouldReturnSpace() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.of(space));

        PhysicalSpace result = physicalSpaceService.getPhysicalSpaceById(1);

        assertEquals("Gym", result.getName());
    }

    @Test
    void getPhysicalSpaceById_ShouldThrowException() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            physicalSpaceService.getPhysicalSpaceById(1);
        });
    }

    @Test
    void searchPhysicalSpacesByName_ShouldReturnList() {
        when(physicalSpaceRepository.findByNameContainingIgnoreCase("gym"))
                .thenReturn(List.of(space));

        List<PhysicalSpace> result = physicalSpaceService.searchPhysicalSpacesByName("gym");

        assertEquals(1, result.size());
        verify(physicalSpaceRepository)
                .findByNameContainingIgnoreCase("gym");
    }

    @Test
    void createPhysicalSpace_ShouldSave() {
        when(physicalSpaceRepository.save(space)).thenReturn(space);

        PhysicalSpace result = physicalSpaceService.createPhysicalSpace(space);

        assertNotNull(result);
        verify(physicalSpaceRepository).save(space);
    }

    @Test
    void updatePhysicalSpace_ShouldUpdate() {
        PhysicalSpace updated = new PhysicalSpace();
        updated.setName("New Gym");
        updated.setLocation("Building B");
        updated.setCapacity(200);

        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.of(space));
        when(physicalSpaceRepository.save(any(PhysicalSpace.class))).thenReturn(space);

        PhysicalSpace result = physicalSpaceService.updatePhysicalSpace(1, updated);

        assertEquals("New Gym", result.getName());
        assertEquals("Building B", result.getLocation());
        assertEquals(200, result.getCapacity());

        verify(physicalSpaceRepository).save(space);
    }

    @Test
    void updatePhysicalSpace_ShouldThrowException() {
        PhysicalSpace updated = new PhysicalSpace();

        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            physicalSpaceService.updatePhysicalSpace(1, updated);
        });
    }

    @Test
    void deletePhysicalSpace_ShouldDelete() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.of(space));

        physicalSpaceService.deletePhysicalSpace(1);

        verify(physicalSpaceRepository).delete(space);
    }

    @Test
    void deletePhysicalSpace_ShouldThrowException() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            physicalSpaceService.deletePhysicalSpace(1);
        });
    }
}
