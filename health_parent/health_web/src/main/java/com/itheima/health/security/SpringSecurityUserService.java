package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取user的详细信息
        User byUsername = userService.findByUsername(username);
        System.out.println(byUsername);

        if(null!=byUsername) {
            //创建存放权限的集合
            List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

            SimpleGrantedAuthority ga = null;
            //遍历user信息的角色表
            Set<Role> roles = byUsername.getRoles();
            for (Role role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getKeyword()));

                Set<Permission> permissions = role.getPermissions();
                if (null != permissions) {
                    //遍历角色对应的权限
                    for (Permission permission : permissions) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(permission.getKeyword()));
                    }
                }
            }
            return new org.springframework.security.core.userdetails.User(byUsername.getUsername(), byUsername.getPassword(), grantedAuthorities);
        }
        return null;
    }
}
