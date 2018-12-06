
$(function () {
    $(":radio").click(function () {
        // alert($("input[name='rd']:checked").val())
        var url = "day";
        var args = {"type": $("input[name='rd']:checked").val()};
        $.post(url, args, function (data) {
            //活动净增粉丝
            $("#activityNetIncreaseNumber").html(data.todayActivityNetIncreaseNumber);
            //活动新粉丝
            $("#activityAddNumber").html(data.todayAddActivityNumber);
            //活动取关粉丝
            $("#activityTakeOffNumber").html(data.getTodayActivityTakeOffNumber);
        });
    });
});