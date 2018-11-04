package gson;

import com.google.gson.annotations.SerializedName;

public class Location {
    public String id;

    @SerializedName("name")
    public String name;

    public String country;

    public String path;

    public String timezone;

    public String timezone_offset;

}
