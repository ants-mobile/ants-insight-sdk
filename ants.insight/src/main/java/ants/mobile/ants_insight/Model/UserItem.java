package ants.mobile.ants_insight.Model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserItem {
    private String userName;
    private String email;
    private String address;
    private String lastName;
    private String firstName;
    private String phone;
    private String customerId;
    private boolean isLogin;
    private String gender;
    private String birthday;
    private String avatar;
    private String userDescription;

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

    public JSONObject getUserInfo() {
        JSONObject param = new JSONObject();
        try {
            if (isLogin)
                param.put("one_signal_id", "3c80bd93-ce53-4180-b423-8ae62b014f04");
            param.put("type", "lead");
            param.put("id", convertStringToMD5(phone) + convertStringToMD5(email));
            param.put("name", firstName + lastName);
            param.put("first_name", firstName);
            param.put("last_name", lastName);
            param.put("phone", phone);
            param.put("customer_id", customerId);
            param.put("email", email);
            param.put("birthday", birthday);
            param.put("address", address);
            param.put("avatar", avatar);
            param.put("username", userName);
            param.put("description", userDescription);
            param.put("gender", gender);

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

    private static String convertStringToMD5(final String input) {
        if (!TextUtils.isEmpty(input)) {
            final String MD5 = "MD5";
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
                digest.update(input.getBytes());
                byte[] messageDigest = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                    while (h.length() < 2)
                        h.insert(0, "0");
                    hexString.append(h);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        } else
            return "";
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
