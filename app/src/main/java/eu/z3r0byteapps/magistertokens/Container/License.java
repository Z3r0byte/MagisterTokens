package eu.z3r0byteapps.magistertokens.Container;

/**
 * Created by bas on 3-3-17.
 */

public class License {
    Boolean isTrial;
    Boolean valid;
    Integer daysLeft;

    public License() {
    }

    public License(Boolean isTrial, Boolean valid, Integer daysLeft) {
        this.isTrial = isTrial;
        this.valid = valid;
        this.daysLeft = daysLeft;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Boolean getTrial() {
        return isTrial;
    }

    public void setTrial(Boolean trial) {
        isTrial = trial;
    }
}
