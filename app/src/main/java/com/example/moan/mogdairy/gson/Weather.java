package com.example.moan.mogdairy.gson;

import com.google.gson.annotations.SerializedName;

public class Weather {
    public Location location;

    public Now now;

    @SerializedName("last_update")
    public String lastUpdate;
}
