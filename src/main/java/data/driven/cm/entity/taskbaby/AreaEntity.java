package data.driven.cm.entity.taskbaby;

/**
 * @Author: lxl
 * @describe 行政区划Entity
 * @Date: 2018/11/29 18:43
 * @Version 1.0
 */
public class AreaEntity {
    /**
     * 行政区的实际编码
     */
    private String areaCode;

    /**
     * 国家
     */
    private String country;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     *区或县
     */
    private String district;

    /**
     * 行政区名称
     */
    private String name;

    /**
     * 行政区名称(去掉后面的市之类的词语)
     */
    private String anotherName;

    /**
     * 省简称
     */
    private String shortName;

    /**
     * province的整数形式
     */
    private Integer intProvince;

    /**
     * city或district的整数形式，直辖市存district，省存city
     */
    private Integer intCity;

    private String geoCoord;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getIntProvince() {
        return intProvince;
    }

    public void setIntProvince(Integer intProvince) {
        this.intProvince = intProvince;
    }

    public Integer getIntCity() {
        return intCity;
    }

    public void setIntCity(Integer intCity) {
        this.intCity = intCity;
    }

    public String getGeoCoord() {
        return geoCoord;
    }

    public void setGeoCoord(String geoCoord) {
        this.geoCoord = geoCoord;
    }
}
