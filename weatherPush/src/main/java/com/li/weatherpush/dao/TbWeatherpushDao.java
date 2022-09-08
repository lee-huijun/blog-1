package com.li.weatherpush.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li.weatherpush.entity.TbWeatherpush;
import org.apache.ibatis.annotations.Mapper;

/**
 * (TbWeatherpush)表数据库访问层
 *
 * @author lihuijun
 * @since 2022-09-03 00:51:45
 */
@Mapper
public interface TbWeatherpushDao extends BaseMapper<TbWeatherpush> {

}

