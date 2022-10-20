package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * FileName: UserInfoBean
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/21 11:24
 * <p>
 * Description:
 */
public class UserInfoBean implements Serializable{


    /**
     * userNo : 63398209
     * account : 15217725309
     * tel : 15217725309
     * nickname : null
     * avatar : null
     * gender : 0
     * birth : null
     * height : 0.0
     * weight : 0.0
     * bust : 0.0
     * waist : 0.0
     * hipline : 0.0
     * cardNo : null
     * frontUrl : null
     * behindUrl : null
     * province : null
     * city : null
     * district : null
     * provinceName : null
     * cityName : null
     * districtName : null
     * address : null
     */

    private String userNo;
    private String account;
    private String tel;
    private String nickname;
    private String avatar;
    private int gender;
    private String birth;
    private float height;
    private float weight;
    private float bust;
    private float waist;
    private float hipline;
    private String cardNo;
    private String frontUrl;
    private String behindUrl;
    private String province;
    private String city;
    private String district;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String address;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBust() {
        return bust;
    }

    public void setBust(float bust) {
        this.bust = bust;
    }

    public float getWaist() {
        return waist;
    }

    public void setWaist(float waist) {
        this.waist = waist;
    }

    public float getHipline() {
        return hipline;
    }

    public void setHipline(float hipline) {
        this.hipline = hipline;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getBehindUrl() {
        return behindUrl;
    }

    public void setBehindUrl(String behindUrl) {
        this.behindUrl = behindUrl;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
