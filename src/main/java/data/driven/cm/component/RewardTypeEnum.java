package data.driven.cm.component;

public enum RewardTypeEnum {
        TOKEN("TOKEN",0),//口令，京口令，淘口令
        SECURECODE("SECURECODE",1), //卡密
        IVITECODE("INVITECODE",2),//邀请码
        GOODS("GOODS",3),//实物
        LESSON("LESSON",4);//课程
        private String name;
        private int index;
        private RewardTypeEnum(String name, int index){
            this.name = name;
            this.index =index;
        }
        // 普通方法
        public static String getName(int index) {
            for (RewardTypeEnum type : RewardTypeEnum.values()) {
                if (type.getIndex() == index) {
                    return type.name;
                }
            }
            return null;
        }
        // get set 方法
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getIndex() {
            return index;
        }
        public void setIndex(int index) {
            this.index = index;
        }
}
