package com.kang.shop.usertestserver.plus.shop.service.impl;

import com.kang.shop.usertestserver.plus.shop.entity.TTestJpa;
import com.kang.shop.usertestserver.plus.shop.mapper.TTestJpaMapper;
import com.kang.shop.usertestserver.plus.shop.service.TTestJpaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄康
 * @since 2019-05-09
 */
@Service
@Primary
public class TTestJpaServiceImpl extends ServiceImpl<TTestJpaMapper, TTestJpa> implements TTestJpaService {

}
