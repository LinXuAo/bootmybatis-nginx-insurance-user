package com.service.Impl;

import com.mapper.InsuranceUserMapper;
import com.po.InsuranceUser;
import com.service.InsuranceUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InsuranceUserServiceImpl implements InsuranceUserService {
    @Autowired
    private InsuranceUserMapper insuranceUserMapper;
    @Override
    public Integer save(InsuranceUser user) {
        return insuranceUserMapper.save(user);
    }

    @Override
    public InsuranceUser findByUserCode(String userCode) {
        return insuranceUserMapper.findByUserCode(userCode);
    }

    @Override
    public Integer updateActived(String userCode) {
        return insuranceUserMapper.updateActived(userCode);
    }

//    @Override
//    public InsuranceUser login(String userCode) {
//        return insuranceUserMapper.login(userCode);
//    }
}
