package com.itheima.health.service;

import com.itheima.health.pojo.Member;

import java.util.List;

public interface MemberService {
    Member findByTelephone(String telephone);

    List<Integer> getMemberReport(List<String> month);
}
