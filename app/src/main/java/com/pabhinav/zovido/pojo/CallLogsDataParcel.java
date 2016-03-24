package com.pabhinav.zovido.pojo;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author pabhinav
 */
public class CallLogsDataParcel implements Parcelable {

    private String phoneNumber;

    private String name;

    private String callType;

    private String callDate;

    private String callDuration;

    private boolean showTick;

    private boolean uploadedTick;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallLogsDataParcel that = (CallLogsDataParcel) o;

        if (showTick != that.showTick) return false;
        if (uploadedTick != that.uploadedTick) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (callType != null ? !callType.equals(that.callType) : that.callType != null)
            return false;
        if (callDate != null ? !callDate.equals(that.callDate) : that.callDate != null)
            return false;
        return !(callDuration != null ? !callDuration.equals(that.callDuration) : that.callDuration != null);

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "CallLogsDataParcel{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", callType='" + callType + '\'' +
                ", callDate='" + callDate + '\'' +
                ", callDuration='" + callDuration + '\'' +
                '}';
    }

    /*************************************/
    /********** Getters/setters **********/
    /*************************************/

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public boolean isShowTick() {
        return showTick;
    }

    public void setShowTick(boolean showTick) {
        this.showTick = showTick;
    }

    public boolean isUploadedTick() {
        return uploadedTick;
    }

    public void setUploadedTick(boolean uploadedTick) {
        this.uploadedTick = uploadedTick;
    }

    /******************************************/
    /********** Parcelable interface **********/
    /******************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){

        out.writeString(phoneNumber);
        out.writeString(name);
        out.writeString(callType);
        out.writeString(callDate);
        out.writeString(callDuration);
    }

    private static CallLogsDataParcel readFromParcel(Parcel in) {

        CallLogsDataParcel CallLogsDataParcel = new CallLogsDataParcel();
        CallLogsDataParcel.setPhoneNumber(in.readString());
        CallLogsDataParcel.setName(in.readString());
        CallLogsDataParcel.setCallType(in.readString());
        CallLogsDataParcel.setCallDate(in.readString());
        CallLogsDataParcel.setCallDuration(in.readString());

        return CallLogsDataParcel;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public CallLogsDataParcel createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        public CallLogsDataParcel[] newArray(int size) {
            return new CallLogsDataParcel[size];
        }
    };
}

