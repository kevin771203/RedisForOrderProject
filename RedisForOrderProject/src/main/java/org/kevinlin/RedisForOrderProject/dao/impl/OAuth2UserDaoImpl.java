package org.kevinlin.springbootmall.dao.impl;

import org.kevinlin.springbootmall.dao.OAuth2UserDao;
import org.kevinlin.springbootmall.model.OAuth2User;
import org.kevinlin.springbootmall.rowmapper.OAuth2UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2UserDaoImpl implements OAuth2UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private OAuth2UserRowMapper oAuth2UserRowMapper;

    @Override
    public OAuth2User getOAuth2User(String provider, String providerId) {
        String sql = """
                SELECT oauth2_user_id, provider, provider_id, name, email, access_token, expires_at
                FROM oauth2_user
                WHERE provider = :provider AND provider_id = :providerId
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("provider", provider);
        map.put("providerId", providerId);

        List<OAuth2User> oAuth2UserList = namedParameterJdbcTemplate.query(sql, map, oAuth2UserRowMapper);

        if (oAuth2UserList.size() > 0) {
            return oAuth2UserList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Long createOAuth2User(OAuth2User oAuth2User) {
        String sql = """
                INSERT INTO oauth2_user(oauth2_user_id, provider, provider_id, name, email, access_token, expires_at)
                VALUES (:oauth2UserId, :provider, :providerId, :name, :email, :accessToken, :expiresAt)
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("oauth2UserId", oAuth2User.getOauth2UserId());
        map.put("provider", oAuth2User.getProvider());
        map.put("providerId", oAuth2User.getProviderId());
        map.put("name", oAuth2User.getName());
        map.put("email", oAuth2User.getEmail());
        map.put("accessToken", oAuth2User.getAccessToken());
        map.put("expiresAt", oAuth2User.getExpiresAt());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

        Long oauth2UserId = oAuth2User.getOauth2UserId();

        return oauth2UserId;
    }
}
