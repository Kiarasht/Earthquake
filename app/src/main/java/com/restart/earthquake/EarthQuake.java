package com.restart.earthquake;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Class representing a single earth quake. Holds objects that together can help a user differentiate
 * between each earth quake.
 */
class EarthQuake implements Parcelable {
    private String dateTime;
    private String src;
    private String eqID;
    private String address;
    private double magnitude;
    private double depth;
    private double lat;
    private double lng;

    /**
     * Constructor in creating a new earth quake object.
     *
     * @param dateTime  date and time the earth quake took place
     * @param src       source station for earthquake data
     * @param eqID      ID of earthquake as it is provided by source
     * @param magnitude magnitude of earthquake in Richter scale
     * @param depth     depth of earthquake in km
     * @param address   address found using Google reverse geocoding API using lat & lng
     * @param lat       latitude at which the earth quake took place
     * @param lng       longitude at which the earth quake took place
     */
    EarthQuake(@NonNull String dateTime, @NonNull String src, @NonNull String eqID, double magnitude,
               double depth, @Nullable String address, double lat, double lng) {

        this.dateTime = dateTime;
        this.src = src;
        this.eqID = eqID;
        this.address = address;
        this.magnitude = magnitude;
        this.depth = depth;
        this.lat = lat;
        this.lng = lng;
    }

    String getDateTime() {
        return dateTime;
    }

    String getSrc() {
        return src;
    }

    String getEqID() {
        return eqID;
    }

    String getAddress() {
        return address;
    }

    double getMagnitude() {
        return magnitude;
    }

    double getDepth() {
        return depth;
    }

    double getLat() {
        return lat;
    }

    double getLng() {
        return lng;
    }

    /**
     * Override the toString method to print an earth quake object by returning a string object
     * that represents each of its variable members.
     *
     * @return parsed string containing the variables and their values
     */
    @Override
    public String toString() {
        return "EarthQuake{" +
                "dateTime='" + dateTime + '\'' +
                ", src='" + src + '\'' +
                ", eqID='" + eqID + '\'' +
                ", magnitude=" + magnitude +
                ", depth=" + depth +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    /**
     * Compares two earth quake objects.
     *
     * @param o incoming earth quake object
     * @return true or false if they are equal or not
     */
    @Override
    public boolean equals(Object o) { // TODO: comparison instead of just equality
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EarthQuake that = (EarthQuake) o;

        if (Double.compare(that.magnitude, magnitude) != 0) return false;
        if (Double.compare(that.depth, depth) != 0) return false;
        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lng, lng) != 0) return false;
        if (!dateTime.equals(that.dateTime)) return false;
        if (!src.equals(that.src)) return false;
        if (!eqID.equals(that.eqID)) return false;
        return address.equals(that.address);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateTime);
        dest.writeString(src);
        dest.writeString(eqID);
        dest.writeString(address);
        dest.writeDouble(magnitude);
        dest.writeDouble(depth);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    /**
     * Read back parcelable data.
     *
     * @param in incoming parcel
     */
    private EarthQuake(Parcel in) {
        dateTime = in.readString();
        src = in.readString();
        eqID = in.readString();
        address = in.readString();
        magnitude = in.readDouble();
        depth = in.readDouble();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<EarthQuake> CREATOR = new Creator<EarthQuake>() {
        @Override
        public EarthQuake createFromParcel(Parcel in) {
            return new EarthQuake(in);
        }

        @Override
        public EarthQuake[] newArray(int size) {
            return new EarthQuake[size];
        }
    };
}
