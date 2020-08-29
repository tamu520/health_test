package security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 想要从数据库中查询信息,类必须实现UserDetailsService接口
 * 剩下的框架会自动校验密码
 */
public class UserService implements UserDetailsService {

    @Reference
    private com.itheima.health.service.UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //数据库中获取数据
        //User admin = userService.findByUsername("admin");
        com.itheima.health.pojo.User admin = findByUsername("admin");

        //User admin = userService.findByUsername("admin");

        //给当前的用户 存放 权限的集合
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        /*
            数据库中角色表和权限表都可以放进GrantedAuthority集合中
            只是role的表,表示的范围较大,里面包含详细 的 对后台操作的权限
         */
        SimpleGrantedAuthority ga=null;
        if(admin!=null){
            Set<Role> roles = admin.getRoles();
            if(roles!=null){
                for (Role role : roles) {
                    //将角色表的 信息存入GrantedAuthority集合
                    //创建SimpleGrantedAuthority存入权限名
                    ga=new SimpleGrantedAuthority(role.getName());
                    //向集合添加权限
                    authorities.add(ga);
                    //再遍历角色表中的 所有权限
                    for (Permission permission : role.getPermissions()) {
                        ga = new SimpleGrantedAuthority(permission.getName());
                        authorities.add(ga);
                    }
                }
            }
            //返回UserDetails的实现类User
            //构造方法传入 用户名,密码,权限集合
            System.out.println(admin.getUsername());
            System.out.println(admin.getPassword());
            System.out.println(authorities);
            return new org.springframework.security.core.userdetails.User(admin.getUsername(),admin.getPassword(),authorities);
        }
        return null;

//
//        // 从数据库查询到的
//        User user = findByUsername(username);
//        // security需要的登陆用户信息
//        // 用户名、密码、权限集合
//        // String username, String password, authorities
//        // 这里的密码是明文
//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        // 添加角色，授权
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
//        authorities.add(authority);
//        authority = new SimpleGrantedAuthority("ABC");
//        authorities.add(authority);
//        authority = new SimpleGrantedAuthority("ADD");
//        authorities.add(authority);
//        // security需要登陆用户信息
//        org.springframework.security.core.userdetails.User securityUser =
//                new org.springframework.security.core.userdetails.User(username,"{noop}"+user.getPassword(),authorities);
//        return securityUser;
//
    }
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 加密密码
        System.out.println(encoder.encode("admin"));

        // 验证密码
        // 原密码
        // 加密后的密码
        System.out.println(encoder.matches("1234", "$2a$10$P7Qx8eKUPX5lngz9UEstUOaDRldEWrj9Rbyox/ShyeoxvPbEHTvni"));
        System.out.println(encoder.matches("1234", "$2a$10$5q.0a0F0hRix8TBJxQ4DB.ekwGzPs3e47hoQVNR7cihi/Rob.G3T6"));
        System.out.println(encoder.matches("1234", "$2a$10$voh.1PJRXQazoijK72sIoOslpmLYPyB.6LtT7aUrXqUO5G8Aw43we"));

        System.out.println(encoder.matches("1234", "$2a$10$u/BcsUUqZNWUxdmDhbnoeeobJy6IBsL1Gn/S0dMxI2RbSgnMKJ.4a"));
    }

    private User findByUsername (String username){
        if("admin".equals(username)) {
            com.itheima.health.pojo.User user = new com.itheima.health.pojo.User();
            user.setUsername("admin");
            user.setPassword("$2a$10$K/2QDoKxELA0Ha4F4jJV3.TYB6.agFsPlDK9cyWz.CVbkaOPbc8Pq");

            Role role = new Role();
            role.setName("ROLE_ADMIN");
            Permission permission = new Permission();
            permission.setName("ADD_CHECKITEM");
            role.getPermissions().add(permission);

            Set<Role> roleList = new HashSet<Role>();
            roleList.add(role);

            user.setRoles(roleList);
            return user;
        }
        return null;
    }
}
