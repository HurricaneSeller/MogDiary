package gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("text")
    public String weatherBrief;

    public String code;

    @SerializedName("temperature")
    public String Temperature;
}
