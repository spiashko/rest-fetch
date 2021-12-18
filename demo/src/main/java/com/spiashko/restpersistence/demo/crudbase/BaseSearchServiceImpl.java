package com.spiashko.restpersistence.demo.crudbase;


import com.spiashko.restpersistence.demo.crudbase.entity.BaseEntity;
import com.spiashko.restpersistence.demo.crudbase.exception.EntityNotFoundException;
import com.spiashko.restpersistence.demo.crudbase.repository.BaseRepository;
import net.jodah.typetools.TypeResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseSearchServiceImpl<
        E extends BaseEntity,
        R extends BaseRepository<E>>
        implements BaseSearchService<E> {

    private final R repository;
    private final Class<E> persistentClass;

    protected BaseSearchServiceImpl(R repository) {
        this.repository = repository;

        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(BaseRepository.class, repository.getClass());
        this.persistentClass = (Class<E>) typeArguments[0];
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> findAll(Specification<E> spec) {
        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findOne(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findOne(Specification<E> spec) {
        return repository.findOne(spec);
    }

    @Transactional(readOnly = true)
    @Override
    public E findOneOrThrow(UUID id) {
        Optional<E> result = findOne(id);
        if (!result.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("No %s entity with id %s exists!", persistentClass.getSimpleName(), id));
        }
        return result.get();
    }

    @Transactional(readOnly = true)
    @Override
    public E findOneOrThrow(Specification<E> spec) {
        Optional<E> result = findOne(spec);
        if (!result.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("No %s entity with spec %s exists!", persistentClass.getSimpleName(), spec));
        }
        return result.get();
    }

    protected R getRepository() {
        return repository;
    }
}
