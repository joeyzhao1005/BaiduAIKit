package com.kit.baiduai.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by Zhao on 2017/12/23.
 */

data class BDAIDomainEntity(val domain: String, val intent: String?, @SerializedName("object") val data: HashMap<String,String>?, val score: Double?) {
}