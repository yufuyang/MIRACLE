package com.example.miracle.common.utils;

import com.example.miracle.modules.company.entity.CompanyUser;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.platform.entity.PlatformUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.example.miracle.common.constant.CommonConstant.*;

@Slf4j
@Component
public class JwtUtil {

    private static final String SECRET_KEY = "miracle_secret";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(PlatformUser platformUser) {
        Date now = new Date();

        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        String token = Jwts.builder()
                .setSubject(platformUser.getUsername())
                .claim("userId", platformUser.getId())
                .claim("role", PLATFORM_ROLE)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 将token存入Redis，设置过期时间
        String redisKey = TOKEN_KEY_PREFIX + PLATFORM_ROLE + ":" + platformUser.getId();

        redisTemplate.opsForValue().set(redisKey, token, EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return token;
    }

    public String generateToken(CompanyUser companyUser) {
        Date now = new Date();

        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        String token = Jwts.builder()
                .setSubject(companyUser.getUsername())
                .claim("userId", companyUser.getId())
                .claim("role", COMPANY_ROLE)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 将token存入Redis，设置过期时间
        String redisKey = TOKEN_KEY_PREFIX + COMPANY_ROLE + ":" + companyUser.getId();

        redisTemplate.opsForValue().set(redisKey, token, EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return token;
    }

    public String generateToken(MerchantUser merchantUser) {
        Date now = new Date();

        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        String token = Jwts.builder()
                .setSubject(merchantUser.getUsername())
                .claim("userId", merchantUser.getId())
                .claim("role", MERCHANT_ROLE)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 将token存入Redis，设置过期时间
        String redisKey = TOKEN_KEY_PREFIX + MERCHANT_ROLE + ":" + merchantUser.getId();

        redisTemplate.opsForValue().set(redisKey, token, EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return token;
    }

    public Claims parseToken(String token) {

        try {

            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

        } catch (Exception e) {
            log.error("JWT token解析失败：", e);
            return null;
        }
    }

    public Long getUserId(String token) {

        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    public String getUserRole(String token) {

        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("role", String.class);
        }
        return null;
    }

    public void invalidateToken(String token) {

        Long userId = getUserId(token);

        String userRole = getUserRole(token);

        if (userId != null) {
            // 从Redis中删除token
            String redisKey = TOKEN_KEY_PREFIX + userRole + ":" + userId;

            redisTemplate.delete(redisKey);

            // 将token加入黑名单
            Claims claims = parseToken(token);

            if (claims != null) {

                Date expiration = claims.getExpiration();

                long ttl = expiration.getTime() - System.currentTimeMillis();

                if (ttl > 0) {

                    redisTemplate.opsForValue().set(TOKEN_BLACKLIST_PREFIX + token, "1", ttl, TimeUnit.MILLISECONDS
                    );
                }
            }
        }
    }

    public boolean validateToken(String token) {

        // 检查token是否在黑名单中
        if (Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token))) {
            return false;
        }

        // 验证token是否有效
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }

        // 检查token是否与Redis中存储的一致
        Long userId = getUserId(token);

        String userRole = getUserRole(token);

        String redisKey = TOKEN_KEY_PREFIX + userRole + ":" + userId;

        String storedToken = redisTemplate.opsForValue().get(redisKey);

        return token.equals(storedToken);
    }
}