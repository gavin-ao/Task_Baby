/**
 * Created by 12045 on 2018/10/22.
 */

(function () {
    // 退出登录
    // $("#loginOut").on("click", function (){
    //     console.log(6645346)
    //     loginOut();
    // });

    $("#addAccount").off("click");
    $("#addAccount").on("click", function () {
        $("#manageAdd").show();
    });
    $("#closeBtn").off("click");
    $("#closeBtn").on("click", function () {
        $("#manageAdd").hide();
    });
    $("#submitBtn").off("click");
    $("#submitBtn").on("click", function () {
        var params = decodeURIComponent($("#formsearch").serialize())
        var dataPars = params.split("&");
        var dataObjArr = []
        for (var i = 0; i < dataPars.length; i++) {
            var par = dataPars[i].split("=");
            dataObjArr[par[0]] = par[1];
        }
        console.log(dataObjArr)
    });
    $(document).ready(function () {
        $('#example').DataTable({
            searching: false, //去掉搜索框方法一：百度上的方法，但是我用这没管用
            bLengthChange: false,   //去掉每页显示多少条数据方法
            oLanguage: {    // 汉化
                sLengthMenu: "每页显示 _MENU_ 条",
                sZeroRecords: "没有找到符合条件的数据",
                sProcessing: "加载中...",
                sInfo: "当前第 _START_ - _END_ 条　共计 _TOTAL_ 条",
                sInfoEmpty: "没有记录",
                sInfoFiltered: "(从 _MAX_ 条记录中过滤)",
                sSearch: "搜索：",
                oPaginate: {
                    "sFirst": "首页",
                    "sPrevious": "前一页",
                    "sNext": "后一页",
                    "sLast": "尾页"
                }
            },
            aoColumnDefs: [
                {
                    "aTargets": [6],
                    "mRender": function (data, type, full) {
                        return "<button class='copy_btn btn btn-primary' style='padding: 2px 4px;margin-left: 8px;' >修改</button><button class='copy_btn btn btn-primary' style='padding: 2px 4px;margin-left: 8px;' >删除</button>";
                    }
                },
                {
                    "bSortable": false,
                    "aTargets": [1, 2, 3, 4, 5, 6]
                },

            ],
            'fnDrawCallback': function (table) {
                $("#example_paginate").append("<div style='display: inline-block; position: relative;top: -2px;'>到第 <input type='text' id='changePages' class='input-text' style='width:50px;height:27px'> 页 <a class='btn btn-default shiny' href='javascript:void(0);' id='dataTables-btn' style='text-align:center'>确认</a></div>");
                var oTable = $("#example").dataTable();
                $('#dataTables-btn').click(function (e) {
                    if ($("#changePages").val() && $("#changePages").val() > 0) {
                        var redirectpage = $("#changePages").val() - 1;
                    } else {
                        var redirectpage = 0;
                    }
                    oTable.fnPageChange(redirectpage);
                });

                $('#changePages').keyup(function (e) {
                    if (e.keyCode == 13) {
                        if ($(this).val() && $(this).val() > 0) {
                            var redirectpage = $(this).val() - 1;
                        } else {
                            var redirectpage = 0;
                        }
                        oTable.fnPageChange(redirectpage);
                    }
                })

            }
        });

        // 初始化刪除按钮
        $('#example tbody').on('click', 'button.delete_btn', function (e) {
            e.preventDefault();
            if (confirm("确定要删除该属性？")) {
                var table = $('#example').DataTable();
                table.row($(this).parents('tr')).remove().draw();
            }

        });
    });

    //上传图片

    $("#upImage").off("click");
    $("#upImage").on("click", function (e) {
        $("#file").click();
    })
    $("#corpper").off("click");
    $("#corpper").on("click", function (e) {
        e.preventDefault();
        $('#show').imgAreaSelect({handles: true, onSelectEnd : preview});
    })
    $("#saveCorpper").off("click");
    $("#saveCorpper").click(function (e) {
        e.preventDefault();
        var pic = $('#show').attr('src');
        console.log(pic)
        $('#show').attr("src",pic);
    });
    //活动时间 选择
    laydateTime();


}());
//判断活动id是否重复
function exitId(tar) {
    console.log($(tar).val())
    return false;
}
//判断每个字段允许的长度
function fieldLength(tar) {
    console.log($(tar).val())
    console.log($(tar).attr("name"))
    var titleName = $.trim($(tar).attr("name"));
    var titleValue = $.trim($(tar).val());
    switch (titleName){
        case 'storeTitle':
            judgmenLength(titleValue,20);
            break;
        case 'invitingButton':
            judgmenLength(titleValue,36);
            break;
        case 'helpButton':
            judgmenLength(titleValue,26);
            break;
        case 'boosterButton':
            judgmenLength(titleValue,26);
            break;
        case 'fullHelpButton':
            judgmenLength(titleValue,26);
            break;
        case 'partsActivity':
            judgmenLength(titleValue,18);
            break;
        case 'posterCopywriting':
            judgmenLength(titleValue,72);
            break;
        case 'shareTitle':
            judgmenLength(titleValue,52);
            break;
    }
    return false;
}
//js锚点效果
function anchorss(id) {
    console.log(id)
    document.getElementById(id).scrollIntoView(true);
    return false;
}
//上传海报图片
function changepic() {
    var reads = new FileReader();
    f = document.getElementById('file').files[0];
    reads.readAsDataURL(f)
    console.log(f)
    if(f){
        fileSize =f.size;
        var size = fileSize / 1024;
        if (size > 300) {
            alert("文件大小不能大于300Kb！");
            file.value = "";
            return false;
        }else if (size <= 0) {
            alert("文件大小不能为0Kb！");
            file.value = "";
            return false;
        }else{
            reads.onload = function (e) {
                console.log(this)
                document.getElementById('show').src = this.result;
                $("#selShow").attr("src",this.result);
                //开启裁剪功能
                $('#show ').imgAreaSelect(
                    {
                        handles:true,
                        fadeSpeed:200,
                        imageHeight:100,
                        imageWidth:100,
                        onSelectEnd : preview
                    }
                );

            };
        }
    }else{
        return false;
    }


}

function preview(img, selection) {
    console.log(img)
    console.log(selection)
    var scaleX = 100 / selection.width;
    var scaleY = 100 / selection.height;
    // var img = new Image();
    //传路径
    img.src = document.getElementById('show').src;

}
//活动时间 选择
function laydateTime() {
    lay('#version').html('-v' + laydate.v);
    var myDate = new Date();
    var time = currentTime(myDate);
//执行一个laydate实例
//     开始时间
    laydate.render({
        elem: '#startTime' //指定元素
        , value: time
        , done: function (value, date) {
            $(".contain_main_title .time1").html(value)
            wholeStartTime = value;
            if(!wholeEndTime){
                wholeEndTime = value;
            }
            changeTimeAfterDataChange()
        }

    });

    // 结束时间
    laydate.render({
        elem: '#endTime' //指定元素
        , value: time
        , done: function (value, date) {
            $(".contain_main_title .time2").html(value)
            if(!wholeStartTime){
                wholeStartTime = value;
            }
            wholeEndTime = value
            changeTimeAfterDataChange()
        }
    });
}

// 获取当前时间
function currentTime(myDate) {
    var year = myDate.getFullYear();
    var mounth = (myDate.getMonth() + 1) > 9 ? (myDate.getMonth() + 1) : "0" + (myDate.getMonth() + 1);
    var date = myDate.getDate() > 9 ? myDate.getDate() : "0" + myDate.getDate();
    return year + "-" + mounth + "-" + date;
}

//判断字数长度 是否符合要求
function judgmenLength(text,lens) {
    if(text==""){
        $.MsgBox.Alert("温馨提示", "内容不能为空。");
    }
    var realLength = 0, len = text.length, charCode = -1;
    for (var i = 0; i < len; i++) {
        charCode = text.charCodeAt(i);
        if (charCode >= 0 && charCode <= 128)
            realLength += 1;
        else
            realLength += 2;
    }
    console.log(realLength)
    if(realLength>lens){
        $.MsgBox.Alert("温馨提示", "超出字数限制。");
        return false;
    }

}


