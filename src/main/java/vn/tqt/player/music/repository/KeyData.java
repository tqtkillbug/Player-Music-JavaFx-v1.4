package vn.tqt.player.music.repository;

public class KeyData {
    private String key;
    private String email;

    public KeyData() {
    }

    public KeyData(String key, String email) {
        this.key = key;
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
