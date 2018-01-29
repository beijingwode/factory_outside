package com.wode.factory.outside.mapper;

import java.util.List;

/**
 * Created by zoln on 2015/7/24.
 */
public interface FactoryBaseDao<E> extends  EntityDao<E,Long> {
	public List<E> selectByModel(E query);
	void insert(E entity);
}
