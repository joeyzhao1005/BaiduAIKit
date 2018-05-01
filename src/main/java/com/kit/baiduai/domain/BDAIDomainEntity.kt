package com.kit.baiduai.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by Zhao on 2017/12/23.
 */

data class BDAIDomainEntity(@SerializedName("domain") val domain: String?, @SerializedName("intent") val intent: String?, @SerializedName("object") val data: HashMap<String, String>?, @SerializedName("score") val score: Double?) {
}