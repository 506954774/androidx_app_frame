package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.List;

/**
 * MineResponse
 * 我的接口返回的实体
 * 责任人:  关鑫
 * 修改人:  关鑫
 * 创建/修改时间: 2019/01/11  09:19
 * Copyright :  全民智慧城市 版权所有
 **/
public class MineResponse implements Serializable {

    private static final long serialVersionUID = -5204676011139121597L;

    /**
     * weibo : {"thridType":0,"nickName":"","headImg":""}
     * major : [{"id":2,"identityType":"考核员","identityName":"普通考核员1"}]
     * InspectionMember : 0
     * wechat : {"thridType":0,"nickName":"","headImg":""}
     * attention : 1
     * collection : 0
     * basis : {"id":4,"identityType":"实名认证","identityName":"个人认证"}
     * user : {"uid":11272,"account":"18500387059","md5":"c32a215320e1766baf9c8c3efb963f33","nickname":"Mr。大鑫","mobileNumber":"18500387059","cityCode":"01","userRemark":"愿一生努力，想要的都拥有，得不到的都释怀","idCard":"210283199203040531","idName":"于洪江","avatarAddress":"caozetest_dfec76d3176548359649837b08203347.jpg","integral":0,"balance":0}
     * realNameState : 1
     * fans : 1
     */
    private WeiboBean weibo;
    private int InspectionMember;
    private WechatBean wechat;
    private int attention;
    private int collection;
    private BasisBean basis;
    private UserBean user;
    private int realNameState;
    private int fans;
    private List<MajorBean> major;
    private List<IdentityByCityBean> identityByCity;
    private String loginDate;
    private int loginLock;

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public int getLoginLock() {
        return loginLock;
    }

    public void setLoginLock(int loginLock) {
        this.loginLock = loginLock;
    }

    public WeiboBean getWeibo() {
        return weibo;
    }

    public void setWeibo(WeiboBean weibo) {
        this.weibo = weibo;
    }

    public String getInspectionMemberStr() {
        String str = "";
        if (0 == InspectionMember) {
            str = "未认证";
        } else if (1 == InspectionMember) {
            str = "已认证";
        }
        return str;
    }

    public int getInspectionMember() {
        return InspectionMember;
    }

    public void setInspectionMember(int InspectionMember) {
        this.InspectionMember = InspectionMember;
    }

    public WechatBean getWechat() {
        return wechat;
    }

    public void setWechat(WechatBean wechat) {
        this.wechat = wechat;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public BasisBean getBasis() {
        return basis;
    }

    public void setBasis(BasisBean basis) {
        this.basis = basis;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getRealNameStateStr() {
        String str = "";
        if (0 == realNameState) {
            str = "未认证";
        } else if (1 == realNameState) {
            str = "已认证";
        }
        return str;
    }

    public int getRealNameState() {
        return realNameState;
    }

    public void setRealNameState(int realNameState) {
        this.realNameState = realNameState;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public List<MajorBean> getMajor() {
        return major;
    }

    public void setMajor(List<MajorBean> major) {
        this.major = major;
    }

    public List<IdentityByCityBean> getIdentityByCity() {
        return identityByCity;
    }

    public void setIdentityByCity(List<IdentityByCityBean> identityByCity) {
        this.identityByCity = identityByCity;
    }

    public static class WeiboBean implements Serializable {
        /**
         * thridType : 0
         * nickName :
         * headImg :
         */
        private int thridType;
        private String nickName;
        private String headImg;

        public int getThridType() {
            return thridType;
        }

        public void setThridType(int thridType) {
            this.thridType = thridType;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }
    }

    public static class WechatBean implements Serializable {
        /**
         * thridType : 0
         * nickName :
         * headImg :
         */
        private int thridType;
        private String nickName;
        private String headImg;

        public int getThridType() {
            return thridType;
        }

        public void setThridType(int thridType) {
            this.thridType = thridType;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }
    }

    public static class BasisBean implements Serializable {
        /**
         * id : 4
         * identityType : 实名认证
         * identityName : 个人认证
         */
        private int id;
        private String identityType;
        private String identityName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIdentityType() {
            return identityType;
        }

        public void setIdentityType(String identityType) {
            this.identityType = identityType;
        }

        public String getIdentityName() {
            return identityName;
        }

        public void setIdentityName(String identityName) {
            this.identityName = identityName;
        }
    }

    public static class UserBean implements Serializable {
        /**
         * uid : 11272
         * account : 18500387059
         * md5 : c32a215320e1766baf9c8c3efb963f33
         * nickname : Mr。大鑫
         * mobileNumber : 18500387059
         * cityCode : 01
         * userRemark : 愿一生努力，想要的都拥有，得不到的都释怀
         * idCard : 210283199203040531
         * idName : 于洪江
         * avatarAddress : caozetest_dfec76d3176548359649837b08203347.jpg
         * integral : 0
         * balance : 0
         */
        private int uid;
        private String account;
        private String md5;
        private String nickname;
        private String mobileNumber;
        private String cityCode;
        private String userRemark;
        private String idCard;
        private String idName;
        private String avatarAddress;
        private int integral;
        private int balance;
        private int loginLock;

        public int getLoginLock() {
            return loginLock;
        }

        public void setLoginLock(int loginLock) {
            this.loginLock = loginLock;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getUserRemark() {
            return userRemark;
        }

        public void setUserRemark(String userRemark) {
            this.userRemark = userRemark;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getIdName() {
            return idName;
        }

        public void setIdName(String idName) {
            this.idName = idName;
        }

        public String getAvatarAddress() {
            return avatarAddress;
        }

        public void setAvatarAddress(String avatarAddress) {
            this.avatarAddress = avatarAddress;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

    public static class MajorBean implements Serializable {
        /**
         * id : 2
         * identityType : 考核员
         * identityName : 普通考核员1
         */
        private int id;
        private String identityType;
        private String identityName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIdentityType() {
            return identityType;
        }

        public void setIdentityType(String identityType) {
            this.identityType = identityType;
        }

        public String getIdentityName() {
            return identityName;
        }

        public void setIdentityName(String identityName) {
            this.identityName = identityName;
        }
    }

    public static class IdentityByCityBean implements Serializable {
        /**
         * identityId : 6
         * cityCode : 01
         */

        private int identityId;
        private String cityCode;

        public int getIdentityId() {
            return identityId;
        }

        public void setIdentityId(int identityId) {
            this.identityId = identityId;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }
    }

    @Override
    public String toString() {
        return "MineResponse{" +
                "weibo=" + weibo +
                ", InspectionMember=" + InspectionMember +
                ", wechat=" + wechat +
                ", attention=" + attention +
                ", collection=" + collection +
                ", basis=" + basis +
                ", user=" + user +
                ", realNameState=" + realNameState +
                ", fans=" + fans +
                ", major=" + major +
                ", identityByCity=" + identityByCity +
                '}';
    }
}
