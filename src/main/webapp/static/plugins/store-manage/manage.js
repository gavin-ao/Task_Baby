/**
 * Created by 12045 on 2018/10/22.
 */

(function () {
    // 退出登录
    // $("#loginOut").on("click", function (){
    //     console.log(6645346)
    //     loginOut();
    // });
    // 添加账号
    $("#addAccount").off("click");
    $("#addAccount").on("click", function () {
        $("#manageAdd").show();
    });
    $("#closeBtn").off("click");
    $("#closeBtn").on("click", function () {
        $("#manageAdd").hide();
    });
    // 提交用户信息
    $("#submitBtn").off("click");
    $("#submitBtn").on("click", function () {
        $("#manageAdd").hide();
        var params = decodeURIComponent($("#formsearch").serialize())
        var dataPars = params.split("&");
        var dataObjArr = {}, dataArr = [];
        var table = $('#example').DataTable();
        var rowData = table.row(0).data();
        for (var i = 0; i < rowData.length; i++) {
            dataArr.push("");
        }
        for (var i = 0; i < dataPars.length; i++) {
            var par = dataPars[i].split("=");
            dataObjArr[par[0]] = par[1];
            dataArr[i] = par[1];
        }
        console.log(dataObjArr)
        var rowData = table.row(0).data(dataArr);
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
                    "mRender": function (data, type, full, meta) {
                        // console.log(meta)
                        return "<button class='modify_btn btn btn-primary' style='padding: 2px 4px;margin-left: 8px;' >修改</button><button class='delete_btn btn btn-primary' style='padding: 2px 4px;margin-left: 8px;' >删除</button>";
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
        // 初始化修改按钮
        $('#example tbody').on('click', 'button.modify_btn', function (e) {
            e.preventDefault();
            // var index = $(this).context._DT_RowIndex; //行号
            // console.log(index)
            if (confirm("确定要修改该属性？")) {
                var table = $('#example').DataTable();
                var rowData = table.row($(this).parents('tr').context._DT_RowIndex).data();
                console.log(table.row($(this).parents('tr').context._DT_RowIndex).data())
                $("#manageAdd").show();
                $("#formsearch input[name='storeUser']").val(rowData[0])
                $("#formsearch input[name='storePassword']").val(rowData[1])
                $("#formsearch input[name='storePrice']").val(rowData[2])
                $("#formsearch input[name='storeTime']").val(rowData[3])
                $("#formsearch input[name='storeLate']").val(rowData[4])
                $("#formsearch input[name='storeAddress']").val(rowData[5])
                // table.row($(this).parents('tr')).remove().draw();
            }

        });
    });

    $('#distpicker').distpicker({
        province: '---- 所在省 ----',
        city: '---- 所在市 ----',
        district: '---- 所在区 ----'
    });

}());

function exitName(tar) {
    console.log($(tar).val())
    return false;
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



