package com.storyPost.PhotoVideoDownloader.data.retrofit.response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntagramProfileResponse {

    @SerializedName("logging_page_id")
    @Expose
    private String loggingPageId;
    @SerializedName("show_suggested_profiles")
    @Expose
    private Boolean showSuggestedProfiles;
    @SerializedName("graphql")
    @Expose
    private Graphql graphql;
    @SerializedName("show_follow_dialog")
    @Expose
    private Boolean showFollowDialog;
    @SerializedName("toast_content_on_load")
    @Expose
    private Object toastContentOnLoad;

    public String getLoggingPageId() {
        return loggingPageId;
    }

    public void setLoggingPageId(String loggingPageId) {
        this.loggingPageId = loggingPageId;
    }

    public Boolean getShowSuggestedProfiles() {
        return showSuggestedProfiles;
    }

    public void setShowSuggestedProfiles(Boolean showSuggestedProfiles) {
        this.showSuggestedProfiles = showSuggestedProfiles;
    }

    public Graphql getGraphql() {
        return graphql;
    }

    public void setGraphql(Graphql graphql) {
        this.graphql = graphql;
    }

    public Boolean getShowFollowDialog() {
        return showFollowDialog;
    }

    public void setShowFollowDialog(Boolean showFollowDialog) {
        this.showFollowDialog = showFollowDialog;
    }

    public Object getToastContentOnLoad() {
        return toastContentOnLoad;
    }

    public void setToastContentOnLoad(Object toastContentOnLoad) {
        this.toastContentOnLoad = toastContentOnLoad;
    }

    public class Graphql {

        @SerializedName("user")
        @Expose
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }

    public static class User implements Parcelable {

        @SerializedName("biography")
        @Expose
        private String biography;
        @SerializedName("blocked_by_viewer")
        @Expose
        private Boolean blockedByViewer;
        @SerializedName("country_block")
        @Expose
        private Boolean countryBlock;
        @SerializedName("external_url")
        @Expose
        private String externalUrl;
        @SerializedName("external_url_linkshimmed")
        @Expose
        private String externalUrlLinkshimmed;
        @SerializedName("edge_followed_by")
        @Expose
        private EdgeFollowedBy edgeFollowedBy;
        @SerializedName("followed_by_viewer")
        @Expose
        private Boolean followedByViewer;
        @SerializedName("edge_follow")
        @Expose
        private EdgeFollow edgeFollow;
        @SerializedName("follows_viewer")
        @Expose
        private Boolean followsViewer;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("has_channel")
        @Expose
        private Boolean hasChannel;
        @SerializedName("has_blocked_viewer")
        @Expose
        private Boolean hasBlockedViewer;
        @SerializedName("highlight_reel_count")
        @Expose
        private Integer highlightReelCount;
        @SerializedName("has_requested_viewer")
        @Expose
        private Boolean hasRequestedViewer;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("is_business_account")
        @Expose
        private Boolean isBusinessAccount;
        @SerializedName("is_joined_recently")
        @Expose
        private Boolean isJoinedRecently;
        @SerializedName("business_category_name")
        @Expose
        private Object businessCategoryName;
        @SerializedName("is_private")
        @Expose
        private Boolean isPrivate;
        @SerializedName("is_verified")
        @Expose
        private Boolean isVerified;
        @SerializedName("edge_mutual_followed_by")
        @Expose
        private EdgeMutualFollowedBy edgeMutualFollowedBy;
        @SerializedName("profile_pic_url")
        @Expose
        private String profilePicUrl;
        @SerializedName("profile_pic_url_hd")
        @Expose
        private String profilePicUrlHd;
        @SerializedName("requested_by_viewer")
        @Expose
        private Boolean requestedByViewer;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("connected_fb_page")
        @Expose
        private Object connectedFbPage;
        @SerializedName("edge_felix_video_timeline")
        @Expose
        private EdgeFelixVideoTimeline edgeFelixVideoTimeline;
        @SerializedName("edge_owner_to_timeline_media")
        @Expose
        private EdgeOwnerToTimelineMedia edgeOwnerToTimelineMedia;
        @SerializedName("edge_saved_media")
        @Expose
        private EdgeSavedMedia edgeSavedMedia;
        @SerializedName("edge_media_collections")
        @Expose
        private EdgeMediaCollections edgeMediaCollections;

        public User(){}

        protected User(Parcel in) {
            biography = in.readString();
            byte tmpBlockedByViewer = in.readByte();
            blockedByViewer = tmpBlockedByViewer == 0 ? null : tmpBlockedByViewer == 1;
            byte tmpCountryBlock = in.readByte();
            countryBlock = tmpCountryBlock == 0 ? null : tmpCountryBlock == 1;
            externalUrl = in.readString();
            externalUrlLinkshimmed = in.readString();
            byte tmpFollowedByViewer = in.readByte();
            followedByViewer = tmpFollowedByViewer == 0 ? null : tmpFollowedByViewer == 1;
            byte tmpFollowsViewer = in.readByte();
            followsViewer = tmpFollowsViewer == 0 ? null : tmpFollowsViewer == 1;
            fullName = in.readString();
            byte tmpHasChannel = in.readByte();
            hasChannel = tmpHasChannel == 0 ? null : tmpHasChannel == 1;
            byte tmpHasBlockedViewer = in.readByte();
            hasBlockedViewer = tmpHasBlockedViewer == 0 ? null : tmpHasBlockedViewer == 1;
            if (in.readByte() == 0) {
                highlightReelCount = null;
            } else {
                highlightReelCount = in.readInt();
            }
            byte tmpHasRequestedViewer = in.readByte();
            hasRequestedViewer = tmpHasRequestedViewer == 0 ? null : tmpHasRequestedViewer == 1;
            id = in.readString();
            byte tmpIsBusinessAccount = in.readByte();
            isBusinessAccount = tmpIsBusinessAccount == 0 ? null : tmpIsBusinessAccount == 1;
            byte tmpIsJoinedRecently = in.readByte();
            isJoinedRecently = tmpIsJoinedRecently == 0 ? null : tmpIsJoinedRecently == 1;
            byte tmpIsPrivate = in.readByte();
            isPrivate = tmpIsPrivate == 0 ? null : tmpIsPrivate == 1;
            byte tmpIsVerified = in.readByte();
            isVerified = tmpIsVerified == 0 ? null : tmpIsVerified == 1;
            profilePicUrl = in.readString();
            profilePicUrlHd = in.readString();
            byte tmpRequestedByViewer = in.readByte();
            requestedByViewer = tmpRequestedByViewer == 0 ? null : tmpRequestedByViewer == 1;
            username = in.readString();
        }

        public static final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) {
                return new User(in);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };

        public String getBiography() {
            return biography;
        }

        public void setBiography(String biography) {
            this.biography = biography;
        }

        public Boolean getBlockedByViewer() {
            return blockedByViewer;
        }

        public void setBlockedByViewer(Boolean blockedByViewer) {
            this.blockedByViewer = blockedByViewer;
        }

        public Boolean getCountryBlock() {
            return countryBlock;
        }

        public void setCountryBlock(Boolean countryBlock) {
            this.countryBlock = countryBlock;
        }

        public String getExternalUrl() {
            return externalUrl;
        }

        public void setExternalUrl(String externalUrl) {
            this.externalUrl = externalUrl;
        }

        public String getExternalUrlLinkshimmed() {
            return externalUrlLinkshimmed;
        }

        public void setExternalUrlLinkshimmed(String externalUrlLinkshimmed) {
            this.externalUrlLinkshimmed = externalUrlLinkshimmed;
        }

        public EdgeFollowedBy getEdgeFollowedBy() {
            return edgeFollowedBy;
        }

        public void setEdgeFollowedBy(EdgeFollowedBy edgeFollowedBy) {
            this.edgeFollowedBy = edgeFollowedBy;
        }

        public Boolean getFollowedByViewer() {
            return followedByViewer;
        }

        public void setFollowedByViewer(Boolean followedByViewer) {
            this.followedByViewer = followedByViewer;
        }

        public EdgeFollow getEdgeFollow() {
            return edgeFollow;
        }

        public void setEdgeFollow(EdgeFollow edgeFollow) {
            this.edgeFollow = edgeFollow;
        }

        public Boolean getFollowsViewer() {
            return followsViewer;
        }

        public void setFollowsViewer(Boolean followsViewer) {
            this.followsViewer = followsViewer;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Boolean getHasChannel() {
            return hasChannel;
        }

        public void setHasChannel(Boolean hasChannel) {
            this.hasChannel = hasChannel;
        }

        public Boolean getHasBlockedViewer() {
            return hasBlockedViewer;
        }

        public void setHasBlockedViewer(Boolean hasBlockedViewer) {
            this.hasBlockedViewer = hasBlockedViewer;
        }

        public Integer getHighlightReelCount() {
            return highlightReelCount;
        }

        public void setHighlightReelCount(Integer highlightReelCount) {
            this.highlightReelCount = highlightReelCount;
        }

        public Boolean getHasRequestedViewer() {
            return hasRequestedViewer;
        }

        public void setHasRequestedViewer(Boolean hasRequestedViewer) {
            this.hasRequestedViewer = hasRequestedViewer;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getIsBusinessAccount() {
            return isBusinessAccount;
        }

        public void setIsBusinessAccount(Boolean isBusinessAccount) {
            this.isBusinessAccount = isBusinessAccount;
        }

        public Boolean getIsJoinedRecently() {
            return isJoinedRecently;
        }

        public void setIsJoinedRecently(Boolean isJoinedRecently) {
            this.isJoinedRecently = isJoinedRecently;
        }

        public Object getBusinessCategoryName() {
            return businessCategoryName;
        }

        public void setBusinessCategoryName(Object businessCategoryName) {
            this.businessCategoryName = businessCategoryName;
        }

        public Boolean getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }

        public EdgeMutualFollowedBy getEdgeMutualFollowedBy() {
            return edgeMutualFollowedBy;
        }

        public void setEdgeMutualFollowedBy(EdgeMutualFollowedBy edgeMutualFollowedBy) {
            this.edgeMutualFollowedBy = edgeMutualFollowedBy;
        }

        public String getProfilePicUrl() {
            return profilePicUrl;
        }

        public void setProfilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
        }

        public String getProfilePicUrlHd() {
            return profilePicUrlHd;
        }

        public void setProfilePicUrlHd(String profilePicUrlHd) {
            this.profilePicUrlHd = profilePicUrlHd;
        }

        public Boolean getRequestedByViewer() {
            return requestedByViewer;
        }

        public void setRequestedByViewer(Boolean requestedByViewer) {
            this.requestedByViewer = requestedByViewer;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Object getConnectedFbPage() {
            return connectedFbPage;
        }

        public void setConnectedFbPage(Object connectedFbPage) {
            this.connectedFbPage = connectedFbPage;
        }

        public EdgeFelixVideoTimeline getEdgeFelixVideoTimeline() {
            return edgeFelixVideoTimeline;
        }

        public void setEdgeFelixVideoTimeline(EdgeFelixVideoTimeline edgeFelixVideoTimeline) {
            this.edgeFelixVideoTimeline = edgeFelixVideoTimeline;
        }

        public EdgeOwnerToTimelineMedia getEdgeOwnerToTimelineMedia() {
            return edgeOwnerToTimelineMedia;
        }

        public void setEdgeOwnerToTimelineMedia(EdgeOwnerToTimelineMedia edgeOwnerToTimelineMedia) {
            this.edgeOwnerToTimelineMedia = edgeOwnerToTimelineMedia;
        }

        public EdgeSavedMedia getEdgeSavedMedia() {
            return edgeSavedMedia;
        }

        public void setEdgeSavedMedia(EdgeSavedMedia edgeSavedMedia) {
            this.edgeSavedMedia = edgeSavedMedia;
        }

        public EdgeMediaCollections getEdgeMediaCollections() {
            return edgeMediaCollections;
        }

        public void setEdgeMediaCollections(EdgeMediaCollections edgeMediaCollections) {
            this.edgeMediaCollections = edgeMediaCollections;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(biography);
            dest.writeByte((byte) (blockedByViewer == null ? 0 : blockedByViewer ? 1 : 2));
            dest.writeByte((byte) (countryBlock == null ? 0 : countryBlock ? 1 : 2));
            dest.writeString(externalUrl);
            dest.writeString(externalUrlLinkshimmed);
            dest.writeByte((byte) (followedByViewer == null ? 0 : followedByViewer ? 1 : 2));
            dest.writeByte((byte) (followsViewer == null ? 0 : followsViewer ? 1 : 2));
            dest.writeString(fullName);
            dest.writeByte((byte) (hasChannel == null ? 0 : hasChannel ? 1 : 2));
            dest.writeByte((byte) (hasBlockedViewer == null ? 0 : hasBlockedViewer ? 1 : 2));
            if (highlightReelCount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(highlightReelCount);
            }
            dest.writeByte((byte) (hasRequestedViewer == null ? 0 : hasRequestedViewer ? 1 : 2));
            dest.writeString(id);
            dest.writeByte((byte) (isBusinessAccount == null ? 0 : isBusinessAccount ? 1 : 2));
            dest.writeByte((byte) (isJoinedRecently == null ? 0 : isJoinedRecently ? 1 : 2));
            dest.writeByte((byte) (isPrivate == null ? 0 : isPrivate ? 1 : 2));
            dest.writeByte((byte) (isVerified == null ? 0 : isVerified ? 1 : 2));
            dest.writeString(profilePicUrl);
            dest.writeString(profilePicUrlHd);
            dest.writeByte((byte) (requestedByViewer == null ? 0 : requestedByViewer ? 1 : 2));
            dest.writeString(username);
        }
    }

    public class ThumbnailResource {

        @SerializedName("src")
        @Expose
        private String src;
        @SerializedName("config_width")
        @Expose
        private Integer configWidth;
        @SerializedName("config_height")
        @Expose
        private Integer configHeight;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public Integer getConfigWidth() {
            return configWidth;
        }

        public void setConfigWidth(Integer configWidth) {
            this.configWidth = configWidth;
        }

        public Integer getConfigHeight() {
            return configHeight;
        }

        public void setConfigHeight(Integer configHeight) {
            this.configHeight = configHeight;
        }

    }

    public class PageInfo___ {

        @SerializedName("has_next_page")
        @Expose
        private Boolean hasNextPage;
        @SerializedName("end_cursor")
        @Expose
        private Object endCursor;

        public Boolean getHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(Boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public Object getEndCursor() {
            return endCursor;
        }

        public void setEndCursor(Object endCursor) {
            this.endCursor = endCursor;
        }

    }

    public class PageInfo__ {

        @SerializedName("has_next_page")
        @Expose
        private Boolean hasNextPage;
        @SerializedName("end_cursor")
        @Expose
        private Object endCursor;

        public Boolean getHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(Boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public Object getEndCursor() {
            return endCursor;
        }

        public void setEndCursor(Object endCursor) {
            this.endCursor = endCursor;
        }

    }

    public class PageInfo_ {

        @SerializedName("has_next_page")
        @Expose
        private Boolean hasNextPage;
        @SerializedName("end_cursor")
        @Expose
        private String endCursor;

        public Boolean getHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(Boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public String getEndCursor() {
            return endCursor;
        }

        public void setEndCursor(String endCursor) {
            this.endCursor = endCursor;
        }

    }

    public class PageInfo {

        @SerializedName("has_next_page")
        @Expose
        private Boolean hasNextPage;
        @SerializedName("end_cursor")
        @Expose
        private Object endCursor;

        public Boolean getHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(Boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public Object getEndCursor() {
            return endCursor;
        }

        public void setEndCursor(Object endCursor) {
            this.endCursor = endCursor;
        }

    }

    public class Owner {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("username")
        @Expose
        private String username;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    public class Node_ {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public class Node {

        @SerializedName("__typename")
        @Expose
        private String typename;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("edge_media_to_caption")
        @Expose
        private EdgeMediaToCaption edgeMediaToCaption;
        @SerializedName("shortcode")
        @Expose
        private String shortcode;
        @SerializedName("edge_media_to_comment")
        @Expose
        private EdgeMediaToComment edgeMediaToComment;
        @SerializedName("comments_disabled")
        @Expose
        private Boolean commentsDisabled;
        @SerializedName("taken_at_timestamp")
        @Expose
        private Integer takenAtTimestamp;
        @SerializedName("dimensions")
        @Expose
        private Dimensions dimensions;
        @SerializedName("display_url")
        @Expose
        private String displayUrl;
        @SerializedName("edge_liked_by")
        @Expose
        private EdgeLikedBy edgeLikedBy;
        @SerializedName("edge_media_preview_like")
        @Expose
        private EdgeMediaPreviewLike edgeMediaPreviewLike;
        @SerializedName("location")
        @Expose
        private Object location;
        @SerializedName("gating_info")
        @Expose
        private Object gatingInfo;
        @SerializedName("media_preview")
        @Expose
        private String mediaPreview;
        @SerializedName("owner")
        @Expose
        private Owner owner;
        @SerializedName("thumbnail_src")
        @Expose
        private String thumbnailSrc;
        @SerializedName("thumbnail_resources")
        @Expose
        private List<ThumbnailResource> thumbnailResources = null;
        @SerializedName("is_video")
        @Expose
        private Boolean isVideo;
        @SerializedName("accessibility_caption")
        @Expose
        private String accessibilityCaption;

        public String getTypename() {
            return typename;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public EdgeMediaToCaption getEdgeMediaToCaption() {
            return edgeMediaToCaption;
        }

        public void setEdgeMediaToCaption(EdgeMediaToCaption edgeMediaToCaption) {
            this.edgeMediaToCaption = edgeMediaToCaption;
        }

        public String getShortcode() {
            return shortcode;
        }

        public void setShortcode(String shortcode) {
            this.shortcode = shortcode;
        }

        public EdgeMediaToComment getEdgeMediaToComment() {
            return edgeMediaToComment;
        }

        public void setEdgeMediaToComment(EdgeMediaToComment edgeMediaToComment) {
            this.edgeMediaToComment = edgeMediaToComment;
        }

        public Boolean getCommentsDisabled() {
            return commentsDisabled;
        }

        public void setCommentsDisabled(Boolean commentsDisabled) {
            this.commentsDisabled = commentsDisabled;
        }

        public Integer getTakenAtTimestamp() {
            return takenAtTimestamp;
        }

        public void setTakenAtTimestamp(Integer takenAtTimestamp) {
            this.takenAtTimestamp = takenAtTimestamp;
        }

        public Dimensions getDimensions() {
            return dimensions;
        }

        public void setDimensions(Dimensions dimensions) {
            this.dimensions = dimensions;
        }

        public String getDisplayUrl() {
            return displayUrl;
        }

        public void setDisplayUrl(String displayUrl) {
            this.displayUrl = displayUrl;
        }

        public EdgeLikedBy getEdgeLikedBy() {
            return edgeLikedBy;
        }

        public void setEdgeLikedBy(EdgeLikedBy edgeLikedBy) {
            this.edgeLikedBy = edgeLikedBy;
        }

        public EdgeMediaPreviewLike getEdgeMediaPreviewLike() {
            return edgeMediaPreviewLike;
        }

        public void setEdgeMediaPreviewLike(EdgeMediaPreviewLike edgeMediaPreviewLike) {
            this.edgeMediaPreviewLike = edgeMediaPreviewLike;
        }

        public Object getLocation() {
            return location;
        }

        public void setLocation(Object location) {
            this.location = location;
        }

        public Object getGatingInfo() {
            return gatingInfo;
        }

        public void setGatingInfo(Object gatingInfo) {
            this.gatingInfo = gatingInfo;
        }

        public String getMediaPreview() {
            return mediaPreview;
        }

        public void setMediaPreview(String mediaPreview) {
            this.mediaPreview = mediaPreview;
        }

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public String getThumbnailSrc() {
            return thumbnailSrc;
        }

        public void setThumbnailSrc(String thumbnailSrc) {
            this.thumbnailSrc = thumbnailSrc;
        }

        public List<ThumbnailResource> getThumbnailResources() {
            return thumbnailResources;
        }

        public void setThumbnailResources(List<ThumbnailResource> thumbnailResources) {
            this.thumbnailResources = thumbnailResources;
        }

        public Boolean getIsVideo() {
            return isVideo;
        }

        public void setIsVideo(Boolean isVideo) {
            this.isVideo = isVideo;
        }

        public String getAccessibilityCaption() {
            return accessibilityCaption;
        }

        public void setAccessibilityCaption(String accessibilityCaption) {
            this.accessibilityCaption = accessibilityCaption;
        }

    }

    public class Edge_ {

        @SerializedName("node")
        @Expose
        private Node_ node;

        public Node_ getNode() {
            return node;
        }

        public void setNode(Node_ node) {
            this.node = node;
        }

    }

    public class EdgeSavedMedia {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("page_info")
        @Expose
        private PageInfo__ pageInfo;
        @SerializedName("edges")
        @Expose
        private List<Object> edges = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public PageInfo__ getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo__ pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<Object> getEdges() {
            return edges;
        }

        public void setEdges(List<Object> edges) {
            this.edges = edges;
        }

    }

    public class EdgeOwnerToTimelineMedia {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("page_info")
        @Expose
        private PageInfo_ pageInfo;
        @SerializedName("edges")
        @Expose
        private List<Edge> edges = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public PageInfo_ getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo_ pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<Edge> getEdges() {
            return edges;
        }

        public void setEdges(List<Edge> edges) {
            this.edges = edges;
        }

    }

    public class EdgeMutualFollowedBy {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("edges")
        @Expose
        private List<Object> edges = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<Object> getEdges() {
            return edges;
        }

        public void setEdges(List<Object> edges) {
            this.edges = edges;
        }

    }

    public class EdgeMediaToComment {

        @SerializedName("count")
        @Expose
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

    public class EdgeMediaToCaption {

        @SerializedName("edges")
        @Expose
        private List<Edge_> edges = null;

        public List<Edge_> getEdges() {
            return edges;
        }

        public void setEdges(List<Edge_> edges) {
            this.edges = edges;
        }

    }

    public class EdgeMediaPreviewLike {

        @SerializedName("count")
        @Expose
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

    public class EdgeMediaCollections {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("page_info")
        @Expose
        private PageInfo___ pageInfo;
        @SerializedName("edges")
        @Expose
        private List<Object> edges = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public PageInfo___ getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo___ pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<Object> getEdges() {
            return edges;
        }

        public void setEdges(List<Object> edges) {
            this.edges = edges;
        }

    }

    public class EdgeLikedBy {

        @SerializedName("count")
        @Expose
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

    public class EdgeFollowedBy {

        @SerializedName("count")
        @Expose
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

    public class EdgeFollow {

        @SerializedName("count")
        @Expose
        private Integer count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

    public class EdgeFelixVideoTimeline {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("page_info")
        @Expose
        private PageInfo pageInfo;
        @SerializedName("edges")
        @Expose
        private List<Object> edges = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<Object> getEdges() {
            return edges;
        }

        public void setEdges(List<Object> edges) {
            this.edges = edges;
        }

    }

    public class Edge implements Parcelable{
        public Edge(){}

        @SerializedName("node")
        @Expose
        private Node node;

        protected Edge(Parcel in) {
        }

        public  final Creator<Edge> CREATOR = new Creator<Edge>() {
            @Override
            public Edge createFromParcel(Parcel in) {
                return new Edge(in);
            }

            @Override
            public Edge[] newArray(int size) {
                return new Edge[size];
            }
        };

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    public class Dimensions {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("width")
        @Expose
        private Integer width;

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

    }

}