/**
 * Created by hadoop on 2018/11/30.
 */
function checkOnInput(input, tip) {
    if (input.validity.patternMismatch === true) {
        input.setCustomValidity(tip);
    } else {
        input.setCustomValidity('');
    }
}
/**
 * 行政区划获取
 */
$(function () {
//            alert("jquery起作用了");
    $("#province").change(function () {
        //使#city只保留第一个option子节点
        $("#city option:not(:first)").remove();
        //使#district只保留第一个option子节点
        $("#district option:not(:first)").remove();
        var province_id = $(this).val();
        if (province_id != "") {
            var url = "areaInfo";
            var args = {"areaCode": province_id, "type": "1"};
            $.getJSON(url, args, function (data) {
                for (var i = 0; i < data.length; i++) {
                    var city_id = data[i].city;
                    var city_name = data[i].name;
                    $("#city").append("<option value='" + city_id + "'>" + city_name + "</option>");
                }
            });
        }
    });
    $("#city").change(function () {
        //使#county只保留第一个option子节点
        $("#district option:not(:first)").remove();
        var city_id = $(this).val();
        if (city_id != "") {
            var url = "areaInfo";
            var args = {"areaCode": city_id, "type": "2"};
            $.getJSON(url, args, function (data) {
                for (var i = 0; i < data.length; i++) {
                    var county_id = data[i].district;
                    var county_name = data[i].name;
                    $("#district").append("<option value='" + county_id + "'>" + county_name + "</option>");
                }
            });
        }
    });
});
/**
 * 提交信息之前进行校验
 * @returns {boolean}
 */
function toVaild(){
    var detailedAddress = trim(document.getElementById("detailedAddress").value);
    var province = document.getElementById("province").value;
    var city = document.getElementById("city").value;
    var district = document.getElementById("district").value;
    if (province ==  "选择省"){
        alert("请选择省");
        return false;
    }
    if (city ==  "选择市"){
        alert("请选择市");
        return false;
    }
    if (district ==  "选择县"){
        alert("请选择县");
        return false;
    }
    if(detailedAddress == ""){
        alert("请填写详细地址");
        return false;
    }
    return true;
}
/**
 * 去掉字符两天的空格
 * @param str
 * @returns {XML|void|string}
 */
function trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}

//            function InvalidMsg(textbox) {
//                if (trim(textbox.value) == '') {
//                    textbox.setCustomValidity('请填写详细地址');
//                }
//                return true;
//            }