package ants.mobile.insight;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("name")
    private String name;
    @SerializedName("age")
    private String age;

//    private Person(Parcel in) {
//        name = in.readString();
//        age = in.readString();
//    }

//    public static final Creator<Person> CREATOR = new Creator<Person>() {
//        @Override
//        public Person createFromParcel(Parcel in) {
//            return new Person(in);
//        }
//
//        @Override
//        public Person[] newArray(int size) {
//            return new Person[size];
//        }
//    };
//
//    public Person() {
//
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(name);
//        dest.writeString(age);
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
