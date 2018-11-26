package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.ActHelpDetailEntity;
import data.driven.cm.entity.taskbaby.ActHelpEntity;

import java.util.Map;

/**
 * @author  lxl
 */
public interface ActivityTrackerService {
    String KEY_HELP_REQUIRE="require";
    String KEY_HELP_HELP="help";
    String KEY_HELP_REMAIN ="remain";
    /**
     * 被助力者的OpenId
     */
    String KEY_HELP_FANS_OPENID = "fansOpenId";
    /**
     * 助力者的openId
     */
    String KEY_HELP_HELP_OPENID = "helpOpenId";
    String KEY_HELP_HELP_ID="helpId";

    public Map<String,Integer> getHelpCount(String helpId,String activityId);

    public Map<String,Object> getTrackInfo(String helpDetailId,String activityId,String access_token);

    public ActHelpEntity getTrack(String helpId);

    public ActHelpDetailEntity getTrackDetail(String helpIdDetialId);

    public void updateActHelpStatus(String helpId,int status);
}
