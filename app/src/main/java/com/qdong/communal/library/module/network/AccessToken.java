package com.qdong.communal.library.module.network;

import java.io.Serializable;

/**
 * AccessToken
 * Created By:Chuck
 * Des:
 * on 2018/9/25 21:17
 */
public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * accessToken : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBUFBJRCI6ImlsaW5rbGluayIsImlzcyI6InRlY2N5YyIsIlBSSU5DSVBBTCI6ImMzNDJjZWNiN2FhYzRmM2U5NGEyMmJmNTRhNmMxMmM3IiwiZXhwIjoxNTM3ODg0OTc5LCJpYXQiOjE1Mzc4ODEzNzksIkNMSUVOVEtFWSI6IjFkODc4OWE1MmE3NTQ5NWU4N2ZiMmFlMDk3NjNmOTE5In0.lCPf9CvIs8qW6SCsP--GCDhWYeicZ1OvAiG0FcmA6L4
     * refreshToken : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBUFBJRCI6ImlsaW5rbGluayIsImlzcyI6InRlY2N5YyIsIlBSSU5DSVBBTCI6ImMzNDJjZWNiN2FhYzRmM2U5NGEyMmJmNTRhNmMxMmM3IiwiZXhwIjoxNTM3OTY3Nzc5LCJpYXQiOjE1Mzc4ODEzNzksIkNMSUVOVEtFWSI6IjFkODc4OWE1MmE3NTQ5NWU4N2ZiMmFlMDk3NjNmOTE5In0.MUUJlqVcNpVlGzFpyaSejfwn-57znkW_HSVRuT9RfPM
     */

    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
