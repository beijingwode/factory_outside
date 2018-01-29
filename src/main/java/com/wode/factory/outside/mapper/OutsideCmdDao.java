package com.wode.factory.outside.mapper;

import java.util.List;

import com.wode.factory.outside.model.OutsideCmd;

/**
 * Created by zoln on 2015/7/24.
 */
public interface OutsideCmdDao extends  FactoryBaseDao<OutsideCmd> {

	List<OutsideCmd> select10Exec(OutsideCmd q);
}
