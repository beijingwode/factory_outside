package com.wode.factory.outside.service;

import java.util.List;

/**
 *
 */
public interface FactoryEntityService<T> extends EntityService<T,Long> {

	public List<T> selectByModel(T query);
}
