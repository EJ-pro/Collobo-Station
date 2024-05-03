package com.example.collobo_station.Data

object DataInfo {
    private var userInfo: UserInfo? = null

    fun getUserInfo(): UserInfo? {
        return userInfo
    }

    fun setUserInfo(userInfo: UserInfo) {
        this.userInfo = userInfo
    }
}