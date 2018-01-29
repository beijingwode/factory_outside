package com.wode.factory.outside.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wode.factory.outside.mapper.OutsideCmdDao;
import com.wode.factory.outside.model.OutsideCmd;
import com.wode.factory.outside.service.OutsideCmdService;

/**
 * Created by zoln on 2015/7/24.
 */
@Service("outsideCmdService")
public class OutsideCmdServiceImpl extends FactoryEntityServiceImpl<OutsideCmd> implements OutsideCmdService {
	@Autowired
	private OutsideCmdDao dao;
	@Override
	public OutsideCmdDao getDao() {
		return dao;
	}

	@Override
	public Long getId(OutsideCmd entity) {
		return entity.getId();
	}

	@Override
	public void setId(OutsideCmd entity, Long id) {
		entity.setId(id);
	}
	
	@Override
	public List<OutsideCmd> select10Exec(OutsideCmd q){
		return dao.select10Exec(q);
	}
}
