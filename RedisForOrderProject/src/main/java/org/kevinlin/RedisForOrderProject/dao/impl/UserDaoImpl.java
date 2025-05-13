package org.kevinlin.springbootmall.dao.impl;

import org.kevinlin.springbootmall.dao.UserDao;
import org.kevinlin.springbootmall.model.Role;
import org.kevinlin.springbootmall.model.Users;
import org.kevinlin.springbootmall.rowmapper.RoleRowMapper;
import org.kevinlin.springbootmall.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private RoleRowMapper roleRowMapper;
    @Override
    public Long createUser(Users user) {
        String sql = "INSERT INTO users (user_id ,email, password, created_date, last_modified_date) " +
                "VALUES (:user_id, :email, :password, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", user.getUserId());
        map.put("email", user.getEmail());
        map.put("password", user.getPassword());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);


        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));


        return user.getUserId();

    }

    @Override
    public Users getUserById(Long userId) {
        String sql = "SELECT user_id, email, password, created_date, last_modified_date " +
                "FROM users WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<Users> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if(userList.size() > 0) {
            return userList.get(0);
        }else {
            return null;
        }

    }

    @Override
    public Users getUserByEmail(String email) {
        String sql = "SELECT user_id, email, password, created_date, last_modified_date " +
                "FROM users WHERE email = :email";

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<Users> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if(userList.size() > 0) {
            return userList.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        String sql = "SELECT role.role_id, role.role_name FROM role " +
                "JOIN user_has_role ON role.role_id = user_has_role.role_id " +
                "WHERE user_has_role.user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, roleRowMapper);

        return roleList;
    }

    @Override
    public void addRoleForUserId(Long userId, Role role) {
        String sql = "INSERT INTO user_has_role(user_id, role_id) VALUES (:userId, :roleId)";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
