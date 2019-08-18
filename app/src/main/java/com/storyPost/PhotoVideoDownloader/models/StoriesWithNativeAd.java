package com.storyPost.PhotoVideoDownloader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.ads.NativeExpressAdView;

public class StoriesWithNativeAd implements Parcelable {

    NativeExpressAdView nativeExpressAdView;
    String model;

    protected StoriesWithNativeAd(Parcel in) {
        model = in.readString();
    }

    public static final Creator<StoriesWithNativeAd> CREATOR = new Creator<StoriesWithNativeAd>() {
        @Override
        public StoriesWithNativeAd createFromParcel(Parcel in) {
            return new StoriesWithNativeAd(in);
        }

        @Override
        public StoriesWithNativeAd[] newArray(int size) {
            return new StoriesWithNativeAd[size];
        }
    };

    public NativeExpressAdView getNativeExpressAdView() {
        return nativeExpressAdView;
    }

    public void setNativeExpressAdView(NativeExpressAdView nativeExpressAdView) {
        this.nativeExpressAdView = nativeExpressAdView;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(model);
    }
}
