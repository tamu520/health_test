package com.itheima.health.service;

import com.itheima.health.pojo.Member;

public interface MemberService {
    Member findByTelephone(String telephone);
}
