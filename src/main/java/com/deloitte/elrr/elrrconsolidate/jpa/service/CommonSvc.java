package com.deloitte.elrr.elrrconsolidate.jpa.service;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;

/**
 * @author mnelakurti
 *
 * @param <T> t
 * @param <ID> id
 *
 */
public interface CommonSvc<T, ID extends Serializable> {

    /**
     *
     * @param entity
     * @return ID
     */
    ID getId(T entity);

    /**
     *
     * @return CrudRepository
     */
    CrudRepository<T, ID> getRepository();

    /**
     *
     * @return Iterable<T> iterable
     */
    default Iterable<T> findAll() {
        return getRepository().findAll();
    }

    /**
     *
     * @param id
     * @return Optional<T>
     */
    default Optional<T> get(ID id) {
        return getRepository().findById(id);
    }

    /**
     *
     * @param entity
     * @return T
     */
    default T save(T entity) {
        return getRepository().save(entity);
    }

    /**
     *
     * @param entities
     * @return Iterable<T>
     */
    default Iterable<T> saveAll(Iterable<T> entities) {
        return getRepository().saveAll(entities);
    }

    /**
     *
     * @param id
     * @throws ResourceNotFoundException
     */
    default void delete(ID id) throws ResourceNotFoundException {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
        } else {
            throw new ResourceNotFoundException(
                    " Id not found for delete : " + id);
        }
    }

    /**
     *
     */
    default void deleteAll() {
        getRepository().deleteAll();

    }

    /**
     *
     * @param entity
     * @throws ResourceNotFoundException
     */
    default void update(T entity) throws ResourceNotFoundException {
        if (getRepository().existsById(getId(entity))) {
            getRepository().save(entity);
        } else {

            throw new ResourceNotFoundException(
                    "Not found record in DB to update: " + entity);
        }
    }

}
