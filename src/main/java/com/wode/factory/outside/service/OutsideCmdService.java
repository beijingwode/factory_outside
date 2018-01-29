package com.wode.factory.outside.service;

import java.util.List;

import com.wode.factory.outside.model.OutsideCmd;

/**
 *
 */
public interface OutsideCmdService extends FactoryEntityService<OutsideCmd> {

	List<OutsideCmd> select10Exec(OutsideCmd q);
}
