/**
 * Created by 12045 on 2018/7/4.
 */
var wholeAppInfoId,wholeStartTime,wholeEndTime;
(function () {

// 获取用户所有小程序
    allSmallProgram();
    // 自定义时间选择
    laydateTime();

    selecyCondition()

    dateSelecteTime()

    coreDataSel()


    // coreDataShow()
    // $("#navbarH").height($("#page-wrapper").height() - 80)


}())

// 获取用户所有小程序
function allSmallProgram() {
    $.ajax({
        url: "/wechat/appinfo/findAppInfoListByUser",
        dataType: "json",
        success: function (data) {

            if(data.success){
                var arrData = data.wechatAppInfoEntityList;
                var html='';
                for(var i=0;i<arrData.length;i++){

                    html += '<li data-appInfoId="'+ arrData[i].appInfoId+'" data-appid="'+ arrData[i].appid +'" data-secret="'+ arrData[i].secret +'"><a href="javascript:void(0)">'+arrData[i].appName+ '</a></li>';
                }
                $("#contain_head_left_ul").html(html);

                $($($("#user_manage .dropdown-menu")[0]).find("li")[0]).trigger("click");
            }
        }
    })
}
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
function chartShow(id, type) {
    $('#' + id).removeAttr('_echarts_instance_');
    $("#" + id).parent().prev(".gridster-box").attr("type", type);
// 基于准备好的dom，初始化echarts实例
    var myChartLine = echarts.init(document.getElementById(id));
    if (type == "line") {
        var option = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                data: [820, 932, 901, 934, 1290, 1330, 1320],
                type: 'line'
            }]
        };
    } else if (type == "pie") {
        var option = {
            title: {
                text: '80%',
                x: 'center',
                y: 'center',
                textStyle: {
                    fontWeight: 'normal',
                    color: '#0580f2',
                    fontSize: '40'
                }
            },
            color: ['rgba(176, 212, 251, 1)'],
            legend: {
                show: true,
                itemGap: 12,
                data: ['男', '女'],
                bottom: 20
            },

            series: [{
                name: 'Line 1',
                type: 'pie',
                clockWise: true,
                radius: ['50%', '66%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        }
                    }
                },
                hoverAnimation: false,
                data: [{
                    value: 80,
                    name: '男',
                    itemStyle: {
                        normal: {
                            color: { // 完成的圆环的颜色
                                colorStops: [{
                                    offset: 0,
                                    color: '#00cefc' // 0% 处的颜色
                                }, {
                                    offset: 1,
                                    color: '#367bec' // 100% 处的颜色
                                }]
                            },
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            }
                        }
                    }
                }, {
                    name: '女',
                    value: 20
                }]
            }]
        }
    }


// 使用刚指定的配置项和数据显示图表。
    myChartLine.setOption(option);
    window.onresize = function () {
        myChartLine.resize();
    }
    // $('#'+id).resize(function () {
    //     myChartLine.resize();
    // });
    // $('#'+id).parent().resize(function () {
    //     myChartLine.resize();
    // });
}
// 折线图展示
function chartLineShow(data) {
    var  xAxisData = [],datas=[];
    for(var i =0;i<data.length;i++){
        xAxisData.push(data[i].groupTime)
        datas.push(data[i].countNum)
    }

// 基于准备好的dom，初始化echarts实例
    var myChartLine = echarts.init(document.getElementById('main_line'));
    var option = {
        tooltip: {
            trigger: 'item'
            // formatter: "{a} <br/>{b} : {c}%"
        },
        xAxis: {
            type: 'category',
            data: xAxisData
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: datas,
            type: 'line'
        }]
    };

// 使用刚指定的配置项和数据显示图表。
    myChartLine.setOption(option);

    window.onresize = function () {
        myChartLine.resize();
    }
}

// 饼图展示
function chartPieShow(data) {
    if((data.newUserNum+data.oldUserNum)==0){
        var ratio = "0.00%";
        // $("#main_pie").html("无用户信息")
    }else {
        var ratio = (data.newUserNum / (data.newUserNum + data.oldUserNum) * 100).toFixed(2) + "%";


        var myChartPie = echarts.init(document.getElementById('main_pie'));
        var options = {
            title: {
                text: ratio,
                x: 'center',
                y: 'center',
                textStyle: {
                    fontWeight: 'normal',
                    color: '#0580f2',
                    fontSize: '40'
                }
            },
            color: ['rgba(176, 212, 251, 1)'],
            legend: {
                show: true,
                itemGap: 12,
                data: ['新用户', '老用户'],
                bottom: 20
            },

            series: [{
                name: 'Line 1',
                type: 'pie',
                clockWise: true,
                radius: ['50%', '66%'],
                itemStyle: {
                    normal: {
                        label: {
                            show: false
                        },
                        labelLine: {
                            show: false
                        }
                    }
                },
                hoverAnimation: false,
                data: [{
                    value: data.newUserNum,
                    name: '新用户',
                    itemStyle: {
                        normal: {
                            color: { // 完成的圆环的颜色
                                colorStops: [{
                                    offset: 0,
                                    color: '#00cefc' // 0% 处的颜色
                                }, {
                                    offset: 1,
                                    color: '#367bec' // 100% 处的颜色
                                }]
                            },
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            }
                        }
                    }
                }, {
                    name: '老用户',
                    value: data.oldUserNum
                }]
            }]
        }
        myChartPie.setOption(options);
        window.onresize = function () {
            myChartPie.resize();
        }
    }

}

// 关系图展示
function chartGraphShow(list,id) {
//    传播轨迹图
    var links = [];
    var nodes = [];
    const ORDNUME = "order"
    var childArr = [];
    var num = 0;
    function xunhuans(list) {
        for (var i = 0; i < list.length; i++) {
            var strId = list[i].totalId + list[i].toUserId;
            var indexArr = $.inArray(strId,childArr)
            if(indexArr>=0){
                list[i]["id"] = indexArr;
            }else{
                childArr.push(strId)
                list[i]["id"] = childArr.length-1;
            }
            num++;
            if (list[i].childList && list[i].childList.length) {
                xunhuans(list[i].childList);
            }

        }
    }
    xunhuans(list);
    function xunhuan(list, num) {
        var number = num;
        for (var i = 0; i < list.length; i++) {
            nodes.push( {
                id:list[i].id,
                name: list[i].toUser,
                rela: list[i].rela,
                class: ORDNUME + number
            });
            num++;
            if (list[i].childList && list[i].childList.length) {
                for (var j = 0; j < list[i].childList.length; j++) {
                    links.push({source: list[i].id, target: list[i].childList[j].id});
                    // nodes.push({
                    //     id:list[i].childList[j].id,
                    //     name: list[i].childList[j].toUser,
                    //     rela: list[i].childList[j].rela,
                    //     class: ORDNUME + num
                    // });
                }
            }
            if (list[i].childList && list[i].childList.length) {
                xunhuan(list[i].childList, num);
                num--;
            } else {
                num--;
            }

        }
    }

    xunhuan(list, 0);


    var markerArr = []
    links.forEach(function (link) {
        for(var i=0;i<nodes.length;i++){
            if (!contains(markerArr, nodes[i].class)) {
                markerArr.push(nodes[i].class)
            }
            if(nodes[i].id == link.source){
                link.source = nodes[i].id;
            }
            if(nodes[i].id == link.target){
                link.target = nodes[i].id ;
            }

        }


    });

    function contains(arr, obj) {
        var i = arr.length;
        while (i--) {
            if (arr[i] === obj) {
                return true;
            }
        }
        return false;
    }

    var dataObj = {};
    dataObj.links = links;
    dataObj.nodes = nodes;

    var zoom = d3.behavior.zoom()
        .scaleExtent([0, 10])
        .on("zoom", zoomed);
    var width = $(id).width(),
        height = $(id).height();

    var force = d3.layout.force()//layout将json格式转化为力学图可用的格式
        .nodes(d3.values(dataObj.nodes))//设定节点数组
        .links(dataObj.links)//设定连线数组
        .size([width, height])//作用域的大小
        .linkDistance(150)//连接线长度
        // .linkStrength(0)   //连接线的强硬度  [0, 1]  值越大 强度越大
        .charge(-1500)//顶点的电荷数。该参数决定是排斥还是吸引，数值越小越互相排斥
        // .theta(0.5)  //设置限制值  值越小 限制越紧
        // .gravity(0.2)    //设置重力    值越大点越集中
        // .friction(0.5)   //摩擦系数[ 0, 1]    数值越小 速度损耗越大
        .on("tick", tick)//指时间间隔，隔一段时间刷新一次画面
        .start();//开始转换

    var svg = d3.select(id).append("svg")
        .attr("width", width)
        .attr("height", height)
        .call(zoom)

    function zoomed() {
        svg.selectAll("g").attr("transform",//svg下的g标签移动大小
            "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
    }

//箭头
    var marker =
        svg.append("svg:defs").selectAll("marker")
            .data(markerArr)
            .enter().append("svg:marker")
            .attr("id", String)
            .attr("markerUnits", "userSpaceOnUse")
            .attr("viewBox", "0 -5 10 10")//坐标系的区域
            // .attr("refX",39)//箭头坐标
            .attr("refX", function (d) {
                return setArrowPlace(d)
            })//箭头坐标
            .attr("refY", 0)
            .attr("markerWidth", 12)//标识的大小
            // .attr("markerWidth", function (d) {
            //     return setArrowPlace(d)
            // })//标识的大小
            .attr("markerHeight", 12)
            // .attr("markerHeight", function (d) {
            //     return setArrowPlace(d)
            // })
            .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
            .attr("stroke-width", 1)//箭头宽度
            .append("path")
            .attr("d", "M0,-5L10,0L0,5")//箭头的路径
            // .attr('fill','#000000');//箭头颜色
            .style("fill", function (d) {

                return setColor(d);
            })


//设置连接线
    var edges_line = svg.append("g").selectAll(".edgepath")
        .data(dataObj.links)
        .enter()
        .append("path")
        .attr({
            'd': function (d) {
                return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y
            },
            'class': 'edgepath',
            'id': function (d, i) {
                return 'edgepath' + i;
            }
        })
        .style("stroke", function (d) {
            // return setColor(d.type);
            return setColor(d.source.class);
        })

        .style("pointer-events", "none")
        .style("stroke-width", 0.5)//线条粗细
        // .attr("marker-end", "url(#resolved)" );//根据箭头标记的id号标记箭头
        .attr("marker-end", function (d) {
            // return "url(#" + d.type + ")";
            return "url(#" + d.source.class + ")";
        });//根据箭头标记的id号标记箭头

    var drag = force.drag()
        .on("dragstart", function (d, i) {
            d3.event.sourceEvent.stopPropagation(); //取消默认事件
            d.fixed = true;    //拖拽开始后设定被拖拽对象为固定
        })
        .on("dragend", function (d, i) {


        })
        .on("drag", function (d, i) {

        });
//圆圈
    var circle = svg.append("g").selectAll("circle")
        .data(force.nodes())//表示使用force.nodes数据
        .enter().append("circle")
        .style("fill", function (node) {
            // return setColor(node.type);
            return setColor(node.class);
        })
        .style('stroke', function (node) {
            // return setColor(node.type);
            return setColor(node.class);
        })
        .attr("r", function (node) {
            return setRadius(node.class);
        })//设置圆圈半径
        .on("click", function (node) {
            //单击时让连接线加粗
            edges_line.style("stroke-width", function (line) {
                if (line.source.name == node.name || line.target.name == node.name) {
                    return 2;
                } else {
                    return 0.5;
                }
            });
        })
        .on("mouseover", function (d, i) {

        })
        .on("mouseout", function (d, i) {

        })
        .on("dblclick", function (d, i) {
            d.fixed = false;
        })
        .call(drag)//将当前选中的元素传到drag函数中，使顶点可以被拖动


//圆圈的提示文字
    circle.append("svg:title")
        .text(function (node) {
            return node.name
        });
    var text = svg.append("g").selectAll("text")
        .data(force.nodes())
        //返回缺失元素的占位对象（placeholder），指向绑定的数据中比选定元素集多出的一部分元素。
        .enter()
        .append("text")
        .attr("dy", ".35em")
        .attr("text-anchor", "middle")//在圆圈中加上数据
        .style('fill', function (node) {
            return "#fff";

        })
        .style("font-size", function (d) {
            return setSize(d.class);
        })
        .attr('x', function (d) {
            var re_en = /[a-zA-Z]+/g;
            //如果是全英文，不换行
            if (d.name.match(re_en)) {
                if (d.name.length <= 4) {
                    d3.select(this).append('tspan')
                        .attr('x', 0)
                        .attr('y', 2)
                        .text(function () {
                            return d.name;
                        });
                } else {
                    var top = d.name.substring(0, 4);
                    var bot = d.name.substring(4, d.name.length);

                    d3.select(this).text(function () {
                        return '';
                    });

                    d3.select(this).append('tspan')
                        .attr('x', 0)
                        .attr('y', -7)
                        .text(function () {
                            return top;
                        });

                    d3.select(this).append('tspan')
                        .attr('x', 0)
                        .attr('y', 12)
                        .text(function () {
                            return bot;
                        });
                }
            }
            // //如果小于四个字符，不换行
            else if (d.name.length <= 4) {
                d3.select(this).append('tspan')
                    .attr('x', 0)
                    .attr('y', 2)
                    .text(function () {
                        return d.name;
                    });
            } else {
                var top = d.name.substring(0, 4);
                var bot = d.name.substring(4, d.name.length);

                d3.select(this).text(function () {
                    return '';
                });

                d3.select(this).append('tspan')
                    .attr('x', 0)
                    .attr('y', -7)
                    .text(function () {
                        return top;
                    });

                d3.select(this).append('tspan')
                    .attr('x', 0)
                    .attr('y', 12)
                    .text(function () {
                        return bot;
                    });
            }
        });


    function tick() {
        //path.attr("d", linkArc);//连接线
        circle.attr("transform", transform1);//圆圈
        text.attr("transform", transform2);//顶点文字


        edges_line.attr('d', function (d) {
            var path = 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
            return path;
        });


    }

//设置连接线的坐标,使用椭圆弧路径段双向编码
    function linkArc(d) {
        return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y
    }

//设置圆圈和文字的坐标
    function transform1(d) {
        return "translate(" + d.x + "," + d.y + ")";
    }

    function transform2(d) {
        return "translate(" + (d.x) + "," + d.y + ")";
    }


// 保存为图片
    $("#imgSave").click(function () {
        // downloadImage();
        var canvas = $("#continer").find('svg')[0];
        saveSvgAsPng(canvas, 'test.png');
    });

    function downloadImage() {
        var serializer = new XMLSerializer();
        var source = serializer.serializeToString(svg.node());

        source = '<?xml version="1.0" standalone="no"?>\r\n' + source;
        var url = "data:image/svg+xml;charset=utf-8," + encodeURIComponent(source);
        document.write('<img src="' + url + '"/>');

        var canvas = document.createElement("canvas");
        canvas.width = width;
        canvas.height = height;

        var context = canvas.getContext("2d");
        var image = new Image;
        image.src = document.getElementsByTagName('img')[0].src;
        image.onload = function () {
            context.drawImage(image, 0, 0);

            var a = document.createElement("a");
            a.download = "fallback.png";
            a.href = canvas.toDataURL("image/png");
            a.click();
        };
    }

// 设置颜色
    function setColor(d) {
        var lineColor;
        var color = ["#FD485E", "#70AD47", "#FF9E00", "#4472C4", "#4EA2F0", "#E066FF"];
        // var color = ['#2ca4bf','#aacf44', '#ff9945', '#3ad1c5', '#f7cb4a','#f5a855','#a6d0e4','#DAEAF6','#0D8ABC','#124E96','#FF008E', '#749F83', '#CA8622', '#BDA29A', '#3fb27e','#97124b','#dc4444'];
        //根据关系的不同设置线条颜色
        for (var i = 0; i < markerArr.length; i++) {
            if (d == ("order" + i)) {
                if (i >= color.length) {
                    lineColor = color[color.length - 1]
                } else {
                    lineColor = color[i];
                }
                break
            } else {
                lineColor = color[color.length - 1]
            }
        }
        return lineColor;
    }

// 设置圆圈大小
    function setRadius(d) {
        var radius;
        for (var i = 0; i < markerArr.length; i++) {
            if (d == ("order" + i)) {
                radius = (40 - i * 4) >= 28 ? (40 - i * 4) : 28
                break;
            } else {
                radius = 28;
            }
        }
        return radius;
    }

// 设置字体大小
    function setSize(d) {
        var size;
        for (var i = 0; i < markerArr.length; i++) {
            if (d == ("order" + i)) {
                size = (20 - i * 4) >= 14 ? (20 - i * 4) : 14;
                break;
            } else {
                size = 14;
            }
        }
        return size;
    }

// 设置箭头大小
    function setArrowSize(d) {
        var lines = 12;
        for (var i = 0; i < markerArr.length; i++) {
            if (d == ("order" + i)) {
                lines = (16 - i * 2) >= 12 ? (16 - i * 2) : 12;
                break;
            } else {
                lines = 12;
            }
        }
        return lines;
    }

// 设置箭头位置
    function setArrowPlace(d) {
        var lines = 32;
        for (var i = 0; i < markerArr.length; i++) {
            if (d == ("order" + i)) {
                lines = (39 - i * 4) >= 32 ? (39 - i * 4) : 32;
                break
            } else {
                lines = 32;
            }
        }
        return lines;
    }

}

//漏斗图展示
function chartFunnelShow(data,id) {
    var max=1;
    for(var i=0;i<data.length;i++){
        if(max<data[i].value){
            max = data[i].value;
        }
    }
    console.log(data)
    var myChartFunnel = echarts.init(document.getElementById(id));

    var option = {
        // title: {
        //     text: '漏斗图',
        //     subtext: '纯属虚构'
        // },
        tooltip: {
            trigger: 'item',
            formatter: "{b} : {c}人"
        },
        // toolbox: {
        //     feature: {
        //         dataView: {readOnly: false},
        //         restore: {},
        //         saveAsImage: {}
        //     }
        // },
        // legend: {
        //     data: ['展现','点击','访问','咨询','订单']
        // },
        calculable: true,
        series: [
            {
                name:'漏斗图',
                type:'funnel',
                left: '10%',
                top: 60,
                //x2: 80,
                bottom: 60,
                width: '80%',
                // height: {totalHeight} - y - y2,
                min: 0,
                max: max,
                sort: 'descending',
                gap: 2,
                label: {
                    normal: {
                        show: true,
                        position: 'inside'
                    },
                    emphasis: {
                        textStyle: {
                            fontSize: 20
                        }
                    }
                },
                labelLine: {
                    normal: {
                        length: 10,
                        lineStyle: {
                            width: 1,
                            type: 'solid'
                        }
                    }
                },
                itemStyle: {
                    normal: {
                        borderColor: '#fff',
                        borderWidth: 1
                    }
                },
                data: data
            }
        ]
    };
    myChartFunnel.setOption(option);
    window.onresize = function () {
        myChartFunnel.resize();
    }
}


// 筛选条件的选择
function selecyCondition() {
    $("#user_manage .dropdown-menu").off('click',"li");
    $("#user_manage .dropdown-menu").on('click', 'li', function () {

        var that = this;
        var content = $.trim($(this).text());
        var dropMenu;
        var target = $(this).parents(".dropdown").find("button .selectIndex");
        switch ($(this.parentNode).attr("aria-labelledby")) {
            case "dropdownMenu1":
                $(target).html(content)
                var appInfoId = $(that).attr("data-appInfoId");
                if(appInfoId==1){
                    wholeAppInfoId = "5b3dd7c91d76102dd8a2d0d4"
                }else{
                    wholeAppInfoId = appInfoId
                }

                // changeTimeAfterDataChange();
                $($("#contain_main_head > div")[2]).find('input').trigger("click");
                break;
            case "dropdownMenu2":
                $(target).html(content)
                break;
        }
    });
}

// 改变时间，数据及图表相继改变
function changeTimeAfterDataChange() {
    coreDataShow(wholeAppInfoId);
    newAndOldUsers(wholeAppInfoId)
    funnelData(wholeAppInfoId)
}

// 时间选择
function dateSelecteTime() {
    var startTime, endTime;
    $("#contain_main_head").off('click', "div>input");
    $("#contain_main_head").on('click', "div>input", function () {
        var inputs = $(this).parent().siblings().find("input");
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].removeAttribute("checked");
        }
        $(this).attr("checked", true)
        className = $(this).attr("class");
        $(".datePicker").css("display", "none");
        var dateTimes;

        switch (className) {
            case "todayTime":
                dateTimes = currentTime(new Date());
                startTime = dateTimes
                endTime = dateTimes
                break;
            case "yesterdayTime":
                dateTimes = currentTime(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
                endTime = dateTimes
                startTime = dateTimes
                break;
            case "Nearly7days":
                dateTimes = currentTime(new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000));
                startTime = dateTimes
                var curdateTimes = currentTime(new Date());
                endTime = curdateTimes
                break;
            case "userdefined":
                $(".datePicker").css("display", "block");
                dateTimes = currentTime(new Date());
                startTime = dateTimes
                endTime = dateTimes

                break;
        }
        $("#startTime").val(startTime);
        $("#endTime").val(endTime);
        $(".contain_main_title .time1").html(startTime)
        $(".contain_main_title .time2").html(endTime)
        wholeStartTime = startTime;
        wholeEndTime = endTime
        changeTimeAfterDataChange()

    })
}

// 获取当前时间
function currentTime(myDate) {
    var year = myDate.getFullYear();
    var mounth = (myDate.getMonth() + 1) > 9 ? (myDate.getMonth() + 1) : "0" + (myDate.getMonth() + 1);
    var date = myDate.getDate() > 9 ? myDate.getDate() : "0" + myDate.getDate();
    return year + "-" + mounth + "-" + date;
}

// 核心数据选择
function coreDataSel() {

    $("#contain_main_data").off('click', "div");
    $("#contain_main_data").on('click', "div", function () {

        $(this).siblings().attr("class", "")
        $(this).attr("class", "selectData")
        var urlName = $(this).attr("data-iden");
        dataTrendDiagram(urlName);

    })

}


// 核心数据展示
function coreDataShow(appInfoId) {

    $.ajax({
        url: "/wechat/total/coreData",
        type: "post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime},
        dataType: "html",
        success: function (data) {
            $("#contain_main_data").html(data);
            $($("#contain_main_data div")[0]).trigger("click");
        }
    })
}

// 数据走势图 数据请求
function dataTrendDiagram(urlName) {
    $.ajax({
        url: "/wechat/total/"+urlName,
        dataType: "json",
        type:"post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime},
        success: function (data) {

            if(data.success){
                chartLineShow(data.data)

            }
        }
    })
}

// 根据时间范围统计新老用户占比
function newAndOldUsers(appInfoId) {
    $.ajax({
        url: "/wechat/total/totalOldAndNewUser",
        dataType: "json",
        type:"post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime},
        success: function (data) {

            if(data.success){
                chartPieShow(data)
            }
        }
    })
}

// 助力轨迹 统计
function graphData() {
    $.ajax({
        url: "/wechat/total/totalSpreadTrajectory",
        type: "post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime},
        dataType: "json",
        success: function (data) {

            $("#main_graph").html("")
            if(data.data&&data.data.length){
                chartGraphShow(data.data,"#main_graph")
            }

        }
    })

}
// 传播轨迹 统计
function graphsData() {
    $.ajax({
        url: "/wechat/total/totalSpreadTrajectory",
        type: "post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime,type:0},
        dataType: "json",
        success: function (data) {
            $("#main_graphs").html("")
            if(data.data&&data.data.length){
                chartGraphShow(data.data,"#main_graphs")
            }

        }
    })
}

// 漏斗图 统计
function funnelData() {
    $.ajax({
        url: "/wechat/total/totalFunnelView",
        type: "post",
        data: {appInfoId: wholeAppInfoId, startDate: wholeStartTime, endDate: wholeEndTime},
        dataType: "json",
        success: function (data) {
            if(data.data&&data.data.length){
                chartFunnelShow(data.data,"main_funnel")
            }
        }
    })
    // chartFunnelShow()
}


//刷新数据
$("#refreshData").off("click")
$("#refreshData").on("click",function () {
    changeTimeAfterDataChange();
})
//助力轨迹图
$("#mainGraph").off("click")
$("#mainGraph").on("click",function () {
    $(this.parentNode).css({"height":"600px"});
    graphData(wholeAppInfoId);
})
//传播轨迹图
$("#mainGraphs").off("click")
$("#mainGraphs").on("click",function () {
    $(this.parentNode).css({"height":"600px"});
    graphsData(wholeAppInfoId)
})