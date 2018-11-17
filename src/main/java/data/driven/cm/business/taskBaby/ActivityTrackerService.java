package data.driven.cm.business.taskBaby;

import data.driven.cm.entity.taskBaby.ActHelpDetailEntity;
import data.driven.cm.entity.taskBaby.ActHelpEntity;

import java.util.Map;

public interface ActivityTrackerService {
    String KEY_HELP_REQUIRE="require";
    String KEY_HELP_HELP="help";
    String KEY_HELP_REMAIN ="remain";
    String KEY_HELP_FANS_OPENID = "fansOpenId";//被助力者的OpenId
    String KEY_HELP_HELP_OPENID = "helpOpenId";//助力者的openId
    public Map<String,Integer> getHelpCount(String helpId,String activityId);


    public Map<String,Object> getTrackInfo(String helpDetailId,String activityId);

    public ActHelpEntity getTrack(String helpId);

    public ActHelpDetailEntity getTrackDetail(String helpIdDetialId);
}
