package eu.z3r0byteapps.magistertokens.Container;

/**
 * Created by bas on 14-2-17.
 */

public class Token {
    private Integer id;
    private String token;

    public Token() {
    }

    public Token(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
