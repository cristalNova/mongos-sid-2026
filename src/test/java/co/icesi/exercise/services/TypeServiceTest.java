package co.icesi.exercise.services;

import co.icesi.exercise.model.Type;
import co.icesi.exercise.repositories.TypeRepository;
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

public class TypeServiceTest {

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private TypeService typeService;

    private Type type;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        type = new Type();
        type.setId(1);
        type.setTypeName("Cardio");
    }

    @Test
    void getAllTypes_ShouldReturnList() {
        when(typeRepository.findAll()).thenReturn(List.of(type));

        List<Type> result = typeService.getAllTypes();

        assertEquals(1, result.size());
        verify(typeRepository).findAll();
    }

    @Test
    void getTypeById_ShouldReturnType() {
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));

        Type result = typeService.getTypeById(1);

        assertEquals("Cardio", result.getTypeName());
    }

    @Test
    void getTypeById_ShouldThrowException() {
        when(typeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            typeService.getTypeById(1);
        });
    }

    @Test
    void createType_ShouldSave() {
        when(typeRepository.save(type)).thenReturn(type);

        Type result = typeService.createType(type);

        assertNotNull(result);
        verify(typeRepository).save(type);
    }

    @Test
    void updateType_ShouldUpdate() {
        Type updated = new Type();
        updated.setTypeName("Strength");

        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(typeRepository.save(any(Type.class))).thenReturn(type);

        Type result = typeService.updateType(1, updated);

        assertEquals("Strength", result.getTypeName());
        verify(typeRepository).save(type);
    }

    @Test
    void updateType_ShouldThrowException() {
        Type updated = new Type();

        when(typeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            typeService.updateType(1, updated);
        });
    }

    @Test
    void deleteType_ShouldDelete() {
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));

        typeService.deleteType(1);

        verify(typeRepository).delete(type);
    }

    @Test
    void deleteType_ShouldThrowException() {
        when(typeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            typeService.deleteType(1);
        });
    }
}
