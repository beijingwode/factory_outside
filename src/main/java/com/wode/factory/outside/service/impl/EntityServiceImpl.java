package com.wode.factory.outside.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.wode.factory.outside.mapper.EntityDao;
import com.wode.factory.outside.service.EntityService;

public abstract class EntityServiceImpl<E,PK extends Serializable> implements EntityService<E,PK>  {
	
	abstract public EntityDao<E,PK> getDao();
	
	@Override
	public E getById(PK id) throws DataAccessException {
		return getDao().getById(id);
	}

	@Override
	public void removeById(PK id) throws DataAccessException {
		getDao().deleteById(id);
	}

	@Override
	public E save(E entity)
			throws DataAccessException {
		return getDao().save(entity);
	}
	@Override
	public void update(E entity) throws DataAccessException {
		getDao().update(entity);
	}
	@Override
	public void saveOrUpdate(E entity)
			throws DataAccessException {
		getDao().saveOrUpdate(entity);
	}
	@Override
	public boolean isUnique(E entity, String uniquePropertyNames)
			throws DataAccessException {
		return getDao().isUnique(entity, uniquePropertyNames);
	}
	@Override
	public List<E> findAll() throws DataAccessException {
		return getDao().findAll();
	}
}
