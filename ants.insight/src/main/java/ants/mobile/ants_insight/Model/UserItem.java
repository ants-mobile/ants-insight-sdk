package ants.mobile.ants_insight.Model;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import adx.Utils;
import ants.mobile.ants_insight.Constants.Constants;

public class UserItem {
    private String userName;
    private String email;
    private String address;
    private String lastName;
    private String firstName;
    private String phone;
    private String customerId;
    private String gender;
    private String birthday;
    private String avatar;
    private String userDescription;
    private List<Other> otherList;

    public UserItem(String userName, String email, String address, String lastName, String firstName, String phone, String customerId,
                    String gender, String birthday, String avatar, String userDescription) {
        this.userName = userName;
        this.email = email;
        this.address = address;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.customerId = customerId;
        this.gender = gender;
        this.birthday = birthday;
        this.avatar = avatar;
        this.userDescription = userDescription;
    }


    public UserItem() {
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    JSONObject getUserInfo(Context mContext) {
        JSONObject param = new JSONObject();

        String data = Utils.getAssetJsonData(mContext);
        Type type = new TypeToken<InsightConfig>() {
        }.getType();
        InsightConfig config = new Gson().fromJson(data, type);

        if (config == null)
            return null;

        try {
            param.put("type", "lead");
            param.put("onesignal_id", Utils.getSharedPreValue(mContext, Constants.KEY_ONE_SIGNAL_ID));

            if (!TextUtils.isEmpty(config.getIdentifyId().getKeyName())) {
                switch (config.getIdentifyId().getKeyName()) {
                    case "1":
                        param.put("id", !config.getIdentifyId().isHashMd5() ? email : convertStringToMD5(email));
                        break;
                    case "2":
                        param.put("id", !config.getIdentifyId().isHashMd5() ? phone : convertStringToMD5(phone));
                        break;
                    case "3":
                        param.put("id", !config.getIdentifyId().isHashMd5() ? customerId : convertStringToMD5(customerId));
                        break;
                    default:
                        param.put("id", convertStringToMD5(phone + convertStringToMD5(email)));
                        break;
                }
            }

            for (UserModel userModel : config.getUserModels()) {
                if (!TextUtils.isEmpty(userModel.getKeyName())) {
                    switch (userModel.getKeyName()) {
                        case "name":
                            param.put("name", !userModel.isHashMd5() ? firstName + lastName : convertStringToMD5(firstName) + convertStringToMD5(lastName));
                            break;
                        case "first_name":
                            param.put("first_name", !userModel.isHashMd5() ? firstName : convertStringToMD5(firstName));
                            break;
                        case "last_name":
                            param.put("last_name", !userModel.isHashMd5() ? lastName : convertStringToMD5(lastName));
                            break;
                        case "phone":
                            param.put("phone", !userModel.isHashMd5() ? phone : convertStringToMD5(phone));
                            break;
                        case "customer_id":
                            param.put("customer_id", !userModel.isHashMd5() ? customerId : convertStringToMD5(customerId));
                            break;
                        case "email":
                            param.put("email", !userModel.isHashMd5() ? customerId : convertStringToMD5(customerId));
                            break;
                        case "birthday":
                            param.put("birthday", !userModel.isHashMd5() ? birthday : convertStringToMD5(birthday));
                            break;
                        case "address":
                            param.put("address", !userModel.isHashMd5() ? address : convertStringToMD5(address));
                            break;
                        case "username":
                            param.put("username", !userModel.isHashMd5() ? userName : convertStringToMD5(userName));
                            break;
                        case "gender":
                            param.put("gender", !userModel.isHashMd5() ? gender : convertStringToMD5(gender));
                            break;
                        default:
                            break;
                    }
                }
            }
            param.put("avatar", avatar);
            param.put("description", userDescription);

            if (otherList != null && otherList.size() > 0) {
                for (Other other : otherList) {
                    param.put(other.key, other.value);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return param;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Other> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<Other> otherList) {
        this.otherList = otherList;
    }

    private static String convertStringToMD5(final String input) {
        if (!TextUtils.isEmpty(input)) {
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                digest.update(input.getBytes());
                byte[] messageDigest = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));

                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        } else
            return "";
    }

}
