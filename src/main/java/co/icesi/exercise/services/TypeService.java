package co.icesi.exercise.services;

import co.icesi.exercise.model.Type;
import co.icesi.exercise.repositories.TypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeService {

    @Autowired
    private TypeRepository typeRepository;

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public Type getTypeById(int id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo no encontrado con id: " + id));
    }

    @Transactional
    public Type createType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    public Type updateType(int id, Type updatedType) {
        Type existingType = getTypeById(id);
        existingType.setTypeName(updatedType.getTypeName());
        return typeRepository.save(existingType);
    }

    @Transactional
    public void deleteType(int id) {
        Type existingType = getTypeById(id);
        typeRepository.delete(existingType);
    }
}
