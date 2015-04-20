package com.zkk.utreasure.dao;

import java.util.List;
import java.util.Map;

import com.zkk.utreasure.entity.ShUser;

public interface ShUserMapper {

	int insertSelective(ShUser shUser);

	ShUser getByName(Map paramMap);

	List<ShUser> findUser(Map<String, Object> params);

}
