package ants.mobile.ants_insight.Model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("key")
    private String keyName;
    @SerializedName("hash_md5")
    private boolean hashMd5;

    public UserModel() {
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public boolean isHashMd5() {
        return hashMd5;
    }

    public void setHashMd5(boolean hashMd5) {
        this.hashMd5 = hashMd5;
    }
}
