package com.deloitte.elrr.elrrconsolidate.jpa.service;

/**
 * 
 */
 
import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;

 
/**
 * @author mnelakurti
 *
 */
public interface CommonSvc<T, ID extends Serializable> {
	public default Iterable<T> findAll() {
		return getRepository().findAll();
	}

	public default Optional<T> get(ID id) {
		return getRepository().findById(id);
	}

	public default T save(T entity) {
		return getRepository().save(entity);
	}

	public default Iterable<T> saveAll(Iterable<T> entities) {
		return getRepository().saveAll(entities);
	}

	public default void delete(ID id) throws ResourceNotFoundException {
		if (getRepository().existsById(id)) {
			getRepository().deleteById(id);
		} else {
			throw new ResourceNotFoundException(" Id not found for delete : " + id);
		}
	}

	public default void deleteAll() {
		getRepository().deleteAll();
		
	}

	public default void update(T entity) throws ResourceNotFoundException {
		if (getRepository().existsById(getId(entity))) {
			getRepository().save(entity);
		} else {

			throw new ResourceNotFoundException("Not found record in DB to update: " + entity);
		}
	}

	public ID getId(T entity);

	public CrudRepository<T, ID> getRepository();
}