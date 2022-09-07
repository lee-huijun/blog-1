package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.dao.TbReceviverDao;
import com.minzheng.blog.entity.TbWeatherpush;
import com.minzheng.blog.dao.TbWeatherpushDao;
import com.minzheng.blog.service.TbWeatherpushService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (TbWeatherpush)表服务实现类
 *
 * @author lihuijun
 * @since 2022-09-03 00:51:45
 */
@Service("tbWeatherpushService")
public class TbWeatherpushServiceImpl extends ServiceImpl<TbWeatherpushDao,TbWeatherpush> implements TbWeatherpushService {


}