package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        //判断用户是不是会员,根据手机查找,因为需要验证码才能通过
        Member member = memberDao.findByTelephone(telephone);
        //如果不是会员则新增会员
        if (null == member) {
            //否则是新增会员
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());

            //通过selectKey拿到新增id
            memberDao.add(member);
        }
        return member;
    }
}
