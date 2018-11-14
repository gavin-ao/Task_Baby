$(function (){
    $("#loginbtn").off("click");
    $("#loginbtn").on("click", function (){
        login();
    });
    // 退出登录
    $("#loginOut").off("click");
    $("#loginOut").on("click", function (){
        console.log(6645346)
        loginOut();
    });
});

function login(){
    var validateSuccess = validate();
    if(validateSuccess){
        var userName = $("#userName").val();
        var pwd = $("#pwd").val();
        pwd = hex_md5(pwd);
        $.ajax({
            url : "/service/execuLogin",
            type : "post",
            data:{"userName": userName,"pwd":pwd},
            dataType : "json",
            success : function(data){
                var success = data.success;
                if(success && success == true){
                    // alert(data.msg);
                    console.log(data)
                    window.location.href = "/wechat/total/index";
                }else{
                    alert("登录失败");
                }
            }
        });
    }
}

function validate(){
    var userName = $("#userName").val();
    if(userName == null || $.trim(userName).length < 1){
        alert("用户名为空");
        return false;
    }

    var pwd = $("#pwd").val();
    if(pwd == null || $.trim(pwd).length < 1){
        alert("密码为空");
        return false;
    }
    return true;

}

function loginOut(){
    $.ajax({
        url : "/service/logout",
        type : "post",
        async:false,
        dataType : "json",
        success : function(){
            window.location.reload();
        }
    });
}
