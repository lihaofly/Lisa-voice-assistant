package com.lihao.lisa.util;

public class WeatherMessage extends BaseMessage{

    //Attribute in keyword 'basic'
    private String mFxLink;
    private String mUpdateTime;

    //Attribute in keyword 'code'
    private String mCode;

    //Attribute in keyword 'now'
    private String mCloud;
    private String mDew;
    private String mFeelsLike;
    private String mHumidity;
    private String mIcon;
    private String mObsTime;
    private String mPrecip;
    private String mPressure;
    private String mTemp;
    private String mText;
    private String mVis;
    private String mWind360;
    private String mWindDir;
    private String mWindScale;
    private String mWindSpeed;

    //Attribute in keyword 'refer'
    private String mLicenseList;
    private String mSourcesList;

    public WeatherMessage(int mMessageResult,
                          String mFxLink, String mUpdateTime,
                          String mCode, String mCloud, String mDew,
                          String mFeelsLike, String mHumidity, String mIcon,
                          String mObsTime, String mPrecip, String mPressure,
                          String mTemp, String mText, String mVis, String mWind360,
                          String mWindDir, String mWindScale, String mWindSpeed,
                          String mLicenseList, String mSourcesList) {
        super(BaseMessage.WEATHER_MESSAGE, mMessageResult);
        this.mFxLink = mFxLink;
        this.mUpdateTime = mUpdateTime;
        this.mCode = mCode;
        this.mCloud = mCloud;
        this.mDew = mDew;
        this.mFeelsLike = mFeelsLike;
        this.mHumidity = mHumidity;
        this.mIcon = mIcon;
        this.mObsTime = mObsTime;
        this.mPrecip = mPrecip;
        this.mPressure = mPressure;
        this.mTemp = mTemp;
        this.mText = mText;
        this.mVis = mVis;
        this.mWind360 = mWind360;
        this.mWindDir = mWindDir;
        this.mWindScale = mWindScale;
        this.mWindSpeed = mWindSpeed;
        this.mLicenseList = mLicenseList;
        this.mSourcesList = mSourcesList;
    }

    public WeatherMessage(int mMessageResult) {
        super(BaseMessage.WEATHER_MESSAGE, mMessageResult);
    }

    public String getFxLink() {
        return mFxLink;
    }

    public void setFxLink(String mFxLink) {
        this.mFxLink = mFxLink;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public String getCloud() {
        return mCloud;
    }

    public void setCloud(String mCloud) {
        this.mCloud = mCloud;
    }

    public String getDew() {
        return mDew;
    }

    public void setDew(String mDew) {
        this.mDew = mDew;
    }

    public String getFeelsLike() {
        return mFeelsLike;
    }

    public void setFeelsLike(String mFeelsLike) {
        this.mFeelsLike = mFeelsLike;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String mHumidity) {
        this.mHumidity = mHumidity;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public String getObsTime() {
        return mObsTime;
    }

    public void setObsTime(String mObsTime) {
        this.mObsTime = mObsTime;
    }

    public String getPrecip() {
        return mPrecip;
    }

    public void setPrecip(String mPrecip) {
        this.mPrecip = mPrecip;
    }

    public String getPressure() {
        return mPressure;
    }

    public void setPressure(String mPressure) {
        this.mPressure = mPressure;
    }

    public String getTemp() {
        return mTemp;
    }

    public void setTemp(String mTemp) {
        this.mTemp = mTemp;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getVis() {
        return mVis;
    }

    public void setVis(String mVis) {
        this.mVis = mVis;
    }

    public String getWind360() {
        return mWind360;
    }

    public void setWind360(String mWind360) {
        this.mWind360 = mWind360;
    }

    public String getWindDir() {
        return mWindDir;
    }

    public void setWindDir(String mWindDir) {
        this.mWindDir = mWindDir;
    }

    public String getWindScale() {
        return mWindScale;
    }

    public void setWindScale(String mWindScale) {
        this.mWindScale = mWindScale;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(String mWindSpeed) {
        this.mWindSpeed = mWindSpeed;
    }

    public String getLicenseList() {
        return mLicenseList;
    }

    public void setLicenseList(String mLicenseList) {
        this.mLicenseList = mLicenseList;
    }

    public String getSourcesList() {
        return mSourcesList;
    }

    public void setSourcesList(String mSourcesList) {
        this.mSourcesList = mSourcesList;
    }

    @Override
    public String getShowMessage() {
        String str=String.format("现在的天气是%s，温度%s度, %s,%s级。", mText, mTemp,mWindDir,mWindSpeed);
        return str;
    }

    @Override
    public String toString() {
        return "WeatherMessage{" +
                "mFxLink='" + mFxLink + '\'' +
                ", mUpdateTime='" + mUpdateTime + '\'' +
                ", mCode='" + mCode + '\'' +
                ", mCloud='" + mCloud + '\'' +
                ", mDew='" + mDew + '\'' +
                ", mFeelsLike='" + mFeelsLike + '\'' +
                ", mHumidity='" + mHumidity + '\'' +
                ", mIcon='" + mIcon + '\'' +
                ", mObsTime='" + mObsTime + '\'' +
                ", mPrecip='" + mPrecip + '\'' +
                ", mPressure='" + mPressure + '\'' +
                ", mTemp='" + mTemp + '\'' +
                ", mText='" + mText + '\'' +
                ", mVis='" + mVis + '\'' +
                ", mWind360='" + mWind360 + '\'' +
                ", mWindDir='" + mWindDir + '\'' +
                ", mWindScale='" + mWindScale + '\'' +
                ", mWindSpeed='" + mWindSpeed + '\'' +
                ", mLicenseList='" + mLicenseList + '\'' +
                ", mSourcesList='" + mSourcesList + '\'' +
                ", mMessageType=" + mMessageType +
                ", mMessageResult=" + mMessageResult +
                ", mShowMessage='" + mShowMessage + '\'' +
                ", mUser=" + mUser +
                ", mCreatedAt=" + mCreatedAt +
                '}';
    }
}
