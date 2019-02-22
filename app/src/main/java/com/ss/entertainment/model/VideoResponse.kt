package com.ss.entertainment.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class VideoResponse(
    @SerializedName("seasons")
    @Expose
    var seasons: List<Season>? = null
)


class Season(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("episodes")
    @Expose
    var episodes: List<Episode>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(Episode)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeTypedList(episodes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Season> {
        override fun createFromParcel(parcel: Parcel): Season {
            return Season(parcel)
        }

        override fun newArray(size: Int): Array<Season?> {
            return arrayOfNulls(size)
        }
    }
}


class Episode(
    @SerializedName("videoUrl")
    @Expose
    var videoUrl: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: Thumbnail? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Thumbnail::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(videoUrl)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeParcelable(thumbnail, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Episode> {
        override fun createFromParcel(parcel: Parcel): Episode {
            return Episode(parcel)
        }

        override fun newArray(size: Int): Array<Episode?> {
            return arrayOfNulls(size)
        }
    }
}

class Thumbnail(

    @SerializedName("originalUrl")
    @Expose
    var originalUrl: String? = null,
    @SerializedName("originalWidth")
    @Expose
    var originalWidth: Int? = null/*,
    @SerializedName("resolutions")
    @Expose
    var resolutions: List<Resolution>? = null,*/


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(originalUrl)
        parcel.writeValue(originalWidth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Thumbnail> {
        override fun createFromParcel(parcel: Parcel): Thumbnail {
            return Thumbnail(parcel)
        }

        override fun newArray(size: Int): Array<Thumbnail?> {
            return arrayOfNulls(size)
        }
    }
}

/*
class Resolution (

    @SerializedName("height")
    @Expose
    var height: Int? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null,
    @SerializedName("tag")
    @Expose
    var tag: String? = null,
    @SerializedName("width")
    @Expose
    var width: Int? = null

)*/
