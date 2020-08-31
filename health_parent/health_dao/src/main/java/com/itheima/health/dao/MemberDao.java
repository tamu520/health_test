package com.itheima.health.dao;

import com.itheima.health.pojo.Member;

public interface MemberDao {
    Member findByTelephone(String phone);

    void add(Member newMember);

    Integer findMemberCountBeforeDate(String s);


    int findMemberCountByDate(String todayDate);

    int findMemberTotalCount();

    int findMemberCountAfterDate(String date);
}
