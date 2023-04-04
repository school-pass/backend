package gbsw.plutter.project.PMS.config;

public interface JwtService {
    String createToken(String subject, long ttlMillis);
    String getSubject(String token);
    void isUsable(String jwt) throws Exception;

}
