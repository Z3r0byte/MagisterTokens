package eu.z3r0byteapps.magistertokens.Container;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bas on 3-3-17.
 */

public class License implements Serializable {

    @SerializedName("trial")
    public Boolean isTrial;

    @SerializedName("valid")
    public Boolean valid;

    @SerializedName("endDate")
    public String endDate;

    public License() {
    }

    public License(Boolean isTrial, Boolean valid, String endDate) {
        this.isTrial = isTrial;
        this.valid = valid;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "License{" +
                "isTrial=" + isTrial +
                ", valid=" + valid +
                ", endDate=" + endDate +
                '}';
    }
}
