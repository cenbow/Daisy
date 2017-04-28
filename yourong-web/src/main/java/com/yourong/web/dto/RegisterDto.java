package com.yourong.web.dto;

public class RegisterDto {
    private long mobile;
    private String pngCode;
    private String phonecode;
    private String password;
    private String repassword;
    private String shortUrl;
    public long getMobile() {
        return mobile;
    }
    public void setMobile(long mobile) {
        this.mobile = mobile;
    }
    public String getPngCode() {
        return pngCode;
    }
    public void setPngCode(String pngCode) {
        this.pngCode = pngCode;
    }
    public String getPhonecode() {
        return phonecode;
    }
    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRepassword() {
        return repassword;
    }
    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
    public String getShortUrl() {
        return shortUrl;
    }
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    

}
