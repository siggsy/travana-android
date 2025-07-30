package com.VegaSolutions.lpptransit.data.lppapi.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("name")
    val name: String,

    @SerializedName("int_id")
    val intId: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("ref_id")
    val refId: String,

    @SerializedName("route_groups_on_station")
    val routeGroupsOnStation: List<String>,
) : Parcelable {

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

    val towards: Boolean
        get() = refId.toIntOrNull()?.mod(2)?.equals(0) ?: false

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(intId)
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
        dest.writeString(refId)
        dest.writeStringList(routeGroupsOnStation)
    }

    companion object CREATOR : Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}
