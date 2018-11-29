package data.driven.cm.component;

public enum RewardTypeEnum {
    //口令，京口令，淘口令
        TOKEN("TOKEN",0),
    //卡密
        SECURECODE("SECURECODE",1),
    //实物
        GOODS("GOODS",2),
    //图片(海报、二维码)
        IMG("IMG",3);
        private String name;
        private int index;
        RewardTypeEnum(String name, int index){
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
