package com.qdong.communal.library.module.network;

import java.io.Serializable;

/**
 * LoginResponse
 * 登录接口返回的实体
 * Created By:Chuck
 * Des:
 * on 2018/12/7 14:47
 */
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * userInfo : {"account":"16600006666","loginLock":"0"}
     * token : eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxNjYwMDAwNjY2NiIsInVzZXJJZCI6IjUiLCJleHAiOjE1NDQxNzkxOTV9.jCJRk0Sx3vN0u-TajeVqvdvzMOdwjp1Rw7GGAz8d5UEYAkcSN8D6qjjreTCGZPTfsaxNixe7D5spqjsclLooraq5hiuI9LJ_9o1hh4HjX0YhPC7XfC2JMKfj4JplEHwomu84v9xOSolqgPkL5-98YFQYX6gnHEtLiMc_yK-alKE
     */

    private UserInfoBean userInfo;
    private String token;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class UserInfoBean implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * account : 16600006666
         * loginLock : 0
         */

        private String account;
        private String loginLock;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getLoginLock() {
            return loginLock;
        }

        public void setLoginLock(String loginLock) {
            this.loginLock = loginLock;
        }

        @Override
        public String toString() {
            return "UserInfoBean{" +
                    "account='" + account + '\'' +
                    ", loginLock='" + loginLock + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "userInfo=" + userInfo +
                ", token='" + token + '\'' +
                '}';
    }
}
