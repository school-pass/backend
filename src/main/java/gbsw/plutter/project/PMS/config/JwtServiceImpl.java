package gbsw.plutter.project.PMS.config;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements  JwtService{
    @Value("${JWT_SECRET_KEY}")
    private final String securityKey;

    @Override
    public String createToken(String subject, long ttlMillis){
        if(ttlMillis == 0) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(securityKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .signWith(signatureAlgorithm, signingKey);

        long nowMillis = System.currentTimeMillis();
        builder.setExpiration(new Date(nowMillis + ttlMillis));
        return builder.compact();
    }
    @Override
    public  String getSubject(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(securityKey))
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @Override
    public void isUsable(String jwt) throws Exception {
        Jws<Claims> claims = Jwts.parser().setSigningKey(securityKey).parseClaimsJws(jwt);
        log.debug(claims.toString());
    }
}
