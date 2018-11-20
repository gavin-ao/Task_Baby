package data.driven.cm.vo.wechat.mp;

import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @program: Task_Baby
 * @description: 地理位置事件
 * @author: Logan
 * @create: 2018-11-20 23:17
 **/

public class LocationEvent extends BaseEvent {
    //纬度
    private String Latitude;
    //经度
    private String Longitude;
    //精度
    private String Precision;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getPrecision() {
        return Precision;
    }

    public void setPrecision(String precision) {
        Precision = precision;
    }
}
