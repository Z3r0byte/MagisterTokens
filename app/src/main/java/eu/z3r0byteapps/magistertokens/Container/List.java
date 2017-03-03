package eu.z3r0byteapps.magistertokens.Container;

/**
 * Created by bas on 15-2-17.
 */

public class List {
    String name;
    Boolean isPreferred;
    Integer amountOfTokens;

    public List() {
    }

    public List(String name, Boolean isPreferred, Integer amountOfTokens) {
        this.name = name;
        this.isPreferred = isPreferred;
        this.amountOfTokens = amountOfTokens;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isPreferred() {
        return isPreferred;
    }

    public void setPreferred(Boolean startupList) {
        isPreferred = startupList;
    }

    public Integer getAmountOfTokens() {
        return amountOfTokens;
    }

    public void setAmountOfTokens(Integer amountOfTokens) {
        this.amountOfTokens = amountOfTokens;
    }
}
