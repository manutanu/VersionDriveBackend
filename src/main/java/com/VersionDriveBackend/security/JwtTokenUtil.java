/*
* JwtTokenUtil
*  This class contains Utility methods for JWT based authentications
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.VersionDriveBackend.constants.ConstantUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable,ConstantUtils {

//	private static final long serialVersionUID = -2550185165626007488L;

	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${JWT_TOKEN_VALIDITY}")
	private String jwt_token_validity;

	
	/**
	 * @Description  retrieve username from jwt token
	 * 
	 * @Author Mritunjay Yadav
	 * @return String
	 * @param String Token
	 * @Exception 
	 * 
	 * */
	public String getUsernameFromToken(String token) {
		
		return getClaimFromToken(token, Claims::getSubject);
	
	}


	/**
	 * @Description  retrieve expiration date from jwt token
	 * 
	 * @Author Mritunjay Yadav
	 * @return Date
	 * @param String Token
	 * @Exception 
	 * 
	 * */
	public Date getExpirationDateFromToken(String token) {
		
		return getClaimFromToken(token, Claims::getExpiration);
	
	}

	/**
	 * @Description  retrieve claims from token
	 * 
	 * @Author Mritunjay Yadav
	 * @return Generic Type
	 * @param String Token , Claims
	 * @Exception 
	 * 
	 * */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
    
	/**
	 * @Description  for retrieveing any information from token we will need the secret key (parse claims form token string using signingKey)
	 * 
	 * @Author Mritunjay Yadav
	 * @return Claims
	 * @param String Token
	 * @Exception 
	 * 
	 * */
	private Claims getAllClaimsFromToken(String token) {
		
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	
	}

	
	/**
	 * @Description  check if the token has expired
	 * 
	 * @Author Mritunjay Yadav
	 * @return Boolean
	 * @param String Token
	 * @Exception 
	 * 
	 * */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	
	/**
	 * @Description  generate token for user
	 * 
	 * @Author Mritunjay Yadav
	 * @return String
	 * @param UserDetails
	 * @Exception 
	 * 
	 * */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	/** @Description while creating the token -
	 *1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	 *2. Sign the JWT using the HS512 algorithm and secret key.
	 *3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	 *   compaction of the JWT to a URL-safe string 
	 * 
	 * @Author Mritunjay Yadav
	 * @return String
	 * @param Map<String,Object> claims,String subject
	 * @Exception 
	 * 
	 * */
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		//generation of jwt token 
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(jwt_token_validity) * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	
	}

	
	/**
	 * @Description  Validate token
	 * 
	 * @Author Mritunjay Yadav
	 * @return Boolean
	 * @param String token  and UserDetails
	 * @Exception 
	 * 
	 * */
	public Boolean validateToken(String token, UserDetails userDetails) {
		
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	
	}
}