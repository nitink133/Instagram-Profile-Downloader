package com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "authenticated",
        "user",
        "userId",
        "oneTapPrompt",
        "fr",
        "status"
})
public class InstagramLoginResponse {

    @JsonProperty("authenticated")
    private Boolean authenticated;
    @JsonProperty("user")
    private Boolean user;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("oneTapPrompt")
    private Boolean oneTapPrompt;
    @JsonProperty("fr")
    private String fr;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("authenticated")
    public Boolean getAuthenticated() {
        return authenticated;
    }

    @JsonProperty("authenticated")
    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    @JsonProperty("user")
    public Boolean getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(Boolean user) {
        this.user = user;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("oneTapPrompt")
    public Boolean getOneTapPrompt() {
        return oneTapPrompt;
    }

    @JsonProperty("oneTapPrompt")
    public void setOneTapPrompt(Boolean oneTapPrompt) {
        this.oneTapPrompt = oneTapPrompt;
    }

    @JsonProperty("fr")
    public String getFr() {
        return fr;
    }

    @JsonProperty("fr")
    public void setFr(String fr) {
        this.fr = fr;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
