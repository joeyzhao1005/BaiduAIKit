package com.kit.baiduai.recognization;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author joeyzhao
 */
public class RecogResult {
    private static final int ERROR_NONE = 0;

    @SerializedName("originalJson")
    private String originalJson;

    @SerializedName("resultsRecognition")
    private String[] resultsRecognition;

    @SerializedName("originalResult")
    private String originalResult;

    /**
     * 日志id， 请求有问题请提问带上sn
     */
    @SerializedName("sn")
    private String sn;

    @SerializedName("desc")
    private String desc;

    @SerializedName("resultType")
    private String resultType;

    @SerializedName("error")
    private int error = -1;

    @SerializedName("subError")
    private int subError = -1;

    public static RecogResult parseJson(String jsonStr) {
        RecogResult result = new RecogResult();
        result.setOriginalJson(jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            result.setSubError(subError);
            result.setError(error);
            result.setDesc(json.optString("desc"));
            result.setResultType(json.optString("result_type"));
            if (error == ERROR_NONE) {
                result.setOriginalResult(json.getString("origin_result"));
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null) {
                    int size = arr.length();
                    String[] recogs = new String[size];
                    for (int i = 0; i < size; i++) {
                        recogs[i] = arr.getString(i);
                    }
                    result.setResultsRecognition(recogs);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean hasError() {
        return error != ERROR_NONE;
    }

    public boolean isFinalResult() {
        return "final_result".equals(resultType);
    }


    public boolean isPartialResult() {
        return "partial_result".equals(resultType);
    }

    public boolean isNluResult() {
        return "nlu_result".equals(resultType);
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    public String[] getResultsRecognition() {
        return resultsRecognition;
    }

    public void setResultsRecognition(String[] resultsRecognition) {
        this.resultsRecognition = resultsRecognition;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOriginalResult() {
        return originalResult;
    }

    public void setOriginalResult(String originalResult) {
        this.originalResult = originalResult;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getSubError() {
        return subError;
    }

    public void setSubError(int subError) {
        this.subError = subError;
    }
}
