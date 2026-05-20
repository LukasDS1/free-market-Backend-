package com.freemarket.auth_service.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "VGhpc0lzQVNlY3VyZUtleUZvckpXVFdpdGhIbWFjU0hBMjU2IQ==";

   public String getToken(UserDetails user) {
    Map<String, Object> extraClaims = new HashMap<>();

    
    com.freemarket.auth_service.model.User u = 
        (com.freemarket.auth_service.model.User) user;
        
    extraClaims.put("roles", user.getAuthorities().stream()
        .map(authority -> authority.getAuthority())
        .collect(java.util.stream.Collectors.toList()));
    
    extraClaims.put("roleId", u.getRol().getRolId());
    extraClaims.put("status", u.getStatus().name());
    extraClaims.put("userId", u.getUserId());

    return getToken(extraClaims, user);
}
public List<String> getRolesFromToken(String token) {
    return getClaim(token, claims -> claims.get("roles", List.class));
}


    private String getToken(Map <String,Object> extraClaims, UserDetails user) {
      return Jwts
      .builder().setClaims(extraClaims)
      .setSubject(user.getUsername())
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis()+1000*60*15))
      .signWith(getKey(),SignatureAlgorithm.HS256)
      .compact();
    }

     private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
       
    }


     public String getUsernameFromToken(String token) {
         return getClaim(token, Claims::getSubject);
     }


     public boolean isTokenValid(String token, UserDetails userDetails) {
      final String username = getUsernameFromToken(token);
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
     }
   
     private Claims getAllClaims(String token){
      return Jwts
      .parserBuilder()
      .setSigningKey(getKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
     }

     public <T> T getClaim(String token,Function<Claims,T> claimsResolver){
      final Claims claims = getAllClaims(token);
      return claimsResolver.apply(claims);
     }

     private Date getExpiration(String token){
      return getClaim(token, Claims::getExpiration);
     }

     private boolean isTokenExpired(String token){
      return getExpiration(token).before(new Date());

     }

     public String generateRefreshToken() {
     return UUID.randomUUID().toString();
     }




   }
