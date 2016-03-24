package com.pabhinav.zovido.pojo;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author pabhinav
 */
public class SavedLogsDataParcel implements Parcelable {

    private Long id;

    private String name;

    /** optional **/
    private String agentName;

    private String date;

    private String time;

    private String duration;

    private String phoneNumber;

    private String purpose;

    private String product;

    private String sport;

    private String callRemarks;

    @Override
    public String toString() {
        return "SavedLogsDataParcel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", agentName='" + agentName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration='" + duration + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", purpose='" + purpose + '\'' +
                ", product='" + product + '\'' +
                ", sport='" + sport + '\'' +
                ", callRemarks='" + callRemarks + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedLogsDataParcel that = (SavedLogsDataParcel) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (agentName != null ? !agentName.equals(that.agentName) : that.agentName != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (sport != null ? !sport.equals(that.sport) : that.sport != null) return false;
        return !(callRemarks != null ? !callRemarks.equals(that.callRemarks) : that.callRemarks != null);
    }

    /*************************************/
    /********** Getters/setters **********/
    /*************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getCallRemarks() {
        return callRemarks;
    }

    public void setCallRemarks(String callRemarks) {
        this.callRemarks = callRemarks;
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

        out.writeLong(id);
        out.writeString(name);
        out.writeString(agentName);
        out.writeString(date);
        out.writeString(time);
        out.writeString(duration);
        out.writeString(phoneNumber);
        out.writeString(purpose);
        out.writeString(product);
        out.writeString(sport);
        out.writeString(callRemarks);
    }

    private static SavedLogsDataParcel readFromParcel(Parcel in) {

        SavedLogsDataParcel savedLogsDataParcel = new SavedLogsDataParcel();
        savedLogsDataParcel.setId(in.readLong());
        savedLogsDataParcel.setName(in.readString());
        savedLogsDataParcel.setAgentName(in.readString());
        savedLogsDataParcel.setDate(in.readString());
        savedLogsDataParcel.setTime(in.readString());
        savedLogsDataParcel.setDuration(in.readString());
        savedLogsDataParcel.setPhoneNumber(in.readString());
        savedLogsDataParcel.setPurpose(in.readString());
        savedLogsDataParcel.setProduct(in.readString());
        savedLogsDataParcel.setSport(in.readString());
        savedLogsDataParcel.setCallRemarks(in.readString());

        return savedLogsDataParcel;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public SavedLogsDataParcel createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        public SavedLogsDataParcel[] newArray(int size) {
            return new SavedLogsDataParcel[size];
        }
    };
}
