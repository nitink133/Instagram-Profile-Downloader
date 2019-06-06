package com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetUserInfoResponse {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("status")
    @Expose
    private String status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class HdProfilePicUrlInfo {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

    }

    public class HdProfilePicVersion {

        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("url")
        @Expose
        private String url;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }


    public class User {

        @SerializedName("pk")
        @Expose
        private Integer pk;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("is_private")
        @Expose
        private Boolean isPrivate;
        @SerializedName("profile_pic_url")
        @Expose
        private String profilePicUrl;
        @SerializedName("profile_pic_id")
        @Expose
        private String profilePicId;
        @SerializedName("is_verified")
        @Expose
        private Boolean isVerified;
        @SerializedName("has_anonymous_profile_picture")
        @Expose
        private Boolean hasAnonymousProfilePicture;
        @SerializedName("media_count")
        @Expose
        private Integer mediaCount;
        @SerializedName("follower_count")
        @Expose
        private Integer followerCount;
        @SerializedName("following_count")
        @Expose
        private Integer followingCount;
        @SerializedName("following_tag_count")
        @Expose
        private Integer followingTagCount;
        @SerializedName("biography")
        @Expose
        private String biography;
        @SerializedName("external_url")
        @Expose
        private String externalUrl;
        @SerializedName("total_igtv_videos")
        @Expose
        private Integer totalIgtvVideos;
        @SerializedName("total_ar_effects")
        @Expose
        private Integer totalArEffects;
        @SerializedName("usertags_count")
        @Expose
        private Integer usertagsCount;
        @SerializedName("is_interest_account")
        @Expose
        private Boolean isInterestAccount;
        @SerializedName("hd_profile_pic_versions")
        @Expose
        private List<HdProfilePicVersion> hdProfilePicVersions = null;
        @SerializedName("hd_profile_pic_url_info")
        @Expose
        private HdProfilePicUrlInfo hdProfilePicUrlInfo;
        @SerializedName("has_highlight_reels")
        @Expose
        private Boolean hasHighlightReels;
        @SerializedName("can_be_reported_as_fraud")
        @Expose
        private Boolean canBeReportedAsFraud;
        @SerializedName("is_potential_business")
        @Expose
        private Boolean isPotentialBusiness;
        @SerializedName("auto_expand_chaining")
        @Expose
        private Boolean autoExpandChaining;
        @SerializedName("highlight_reshare_disabled")
        @Expose
        private Boolean highlightReshareDisabled;

        public Integer getPk() {
            return pk;
        }

        public void setPk(Integer pk) {
            this.pk = pk;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Boolean getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public String getProfilePicUrl() {
            return profilePicUrl;
        }

        public void setProfilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
        }

        public String getProfilePicId() {
            return profilePicId;
        }

        public void setProfilePicId(String profilePicId) {
            this.profilePicId = profilePicId;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }

        public Boolean getHasAnonymousProfilePicture() {
            return hasAnonymousProfilePicture;
        }

        public void setHasAnonymousProfilePicture(Boolean hasAnonymousProfilePicture) {
            this.hasAnonymousProfilePicture = hasAnonymousProfilePicture;
        }

        public Integer getMediaCount() {
            return mediaCount;
        }

        public void setMediaCount(Integer mediaCount) {
            this.mediaCount = mediaCount;
        }

        public Integer getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Integer followerCount) {
            this.followerCount = followerCount;
        }

        public Integer getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(Integer followingCount) {
            this.followingCount = followingCount;
        }

        public Integer getFollowingTagCount() {
            return followingTagCount;
        }

        public void setFollowingTagCount(Integer followingTagCount) {
            this.followingTagCount = followingTagCount;
        }

        public String getBiography() {
            return biography;
        }

        public void setBiography(String biography) {
            this.biography = biography;
        }

        public String getExternalUrl() {
            return externalUrl;
        }

        public void setExternalUrl(String externalUrl) {
            this.externalUrl = externalUrl;
        }

        public Integer getTotalIgtvVideos() {
            return totalIgtvVideos;
        }

        public void setTotalIgtvVideos(Integer totalIgtvVideos) {
            this.totalIgtvVideos = totalIgtvVideos;
        }

        public Integer getTotalArEffects() {
            return totalArEffects;
        }

        public void setTotalArEffects(Integer totalArEffects) {
            this.totalArEffects = totalArEffects;
        }

        public Integer getUsertagsCount() {
            return usertagsCount;
        }

        public void setUsertagsCount(Integer usertagsCount) {
            this.usertagsCount = usertagsCount;
        }

        public Boolean getIsInterestAccount() {
            return isInterestAccount;
        }

        public void setIsInterestAccount(Boolean isInterestAccount) {
            this.isInterestAccount = isInterestAccount;
        }

        public List<HdProfilePicVersion> getHdProfilePicVersions() {
            return hdProfilePicVersions;
        }

        public void setHdProfilePicVersions(List<HdProfilePicVersion> hdProfilePicVersions) {
            this.hdProfilePicVersions = hdProfilePicVersions;
        }

        public HdProfilePicUrlInfo getHdProfilePicUrlInfo() {
            return hdProfilePicUrlInfo;
        }

        public void setHdProfilePicUrlInfo(HdProfilePicUrlInfo hdProfilePicUrlInfo) {
            this.hdProfilePicUrlInfo = hdProfilePicUrlInfo;
        }

        public Boolean getHasHighlightReels() {
            return hasHighlightReels;
        }

        public void setHasHighlightReels(Boolean hasHighlightReels) {
            this.hasHighlightReels = hasHighlightReels;
        }

        public Boolean getCanBeReportedAsFraud() {
            return canBeReportedAsFraud;
        }

        public void setCanBeReportedAsFraud(Boolean canBeReportedAsFraud) {
            this.canBeReportedAsFraud = canBeReportedAsFraud;
        }

        public Boolean getIsPotentialBusiness() {
            return isPotentialBusiness;
        }

        public void setIsPotentialBusiness(Boolean isPotentialBusiness) {
            this.isPotentialBusiness = isPotentialBusiness;
        }

        public Boolean getAutoExpandChaining() {
            return autoExpandChaining;
        }

        public void setAutoExpandChaining(Boolean autoExpandChaining) {
            this.autoExpandChaining = autoExpandChaining;
        }

        public Boolean getHighlightReshareDisabled() {
            return highlightReshareDisabled;
        }

        public void setHighlightReshareDisabled(Boolean highlightReshareDisabled) {
            this.highlightReshareDisabled = highlightReshareDisabled;
        }

    }

}


