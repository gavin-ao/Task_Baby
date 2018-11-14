package data.driven.cm.util;

import java.util.*;

/**
 * @author hejinkai
 * @date 2018/7/1
 */
public class DemoCount {
    static class TransRecord {
        public String fromid;
        public String toid;
        public Boolean isCounted;
        public TransRecord(String fromid, String toid) {
            this.fromid = fromid;
            this.toid = toid;
            isCounted = false;
        }

    }
    private static void count(List<TransRecord> targets, String targetid, int level,
                              Map<String, Integer> finalresult) {
        if (level == 0)
            return;
        for (TransRecord item : targets) {
            if (item.fromid.equals(targetid)) {
                if (!item.isCounted) {
                    item.isCounted = true;
                    if (!finalresult.containsKey(item.toid))
                        finalresult.put(item.toid, 1);
                    count(targets, item.toid, level - 1, finalresult);
                }
            }
        }
    }

    private static List<TransRecord> initVaule() {
        List<TransRecord> result = new ArrayList<>();
        TransRecord item = new TransRecord("A", "B");
        TransRecord item2 = new TransRecord("B", "C");
        TransRecord item3 = new TransRecord("C", "D");
        TransRecord item4 = new TransRecord("C", "A");
        TransRecord item5 = new TransRecord("C", "B");
        TransRecord item6 = new TransRecord("D", "E");
        result.add(item);
        result.add(item2);
        result.add(item3);
        result.add(item4);
        result.add(item5);
        result.add(item6);
        return result;
    }

    public static void main(String[] args) {
        System.out.println("All things is ok!");
        List<TransRecord> targets = initVaule();
        Map<String, Integer> result = new HashMap<>();
        result.put("A", 1);
        count(targets, "A", 3, result);
        System.out.println(result.size() - 1);
        System.out.println(Arrays.toString(result.keySet().toArray()));
    }
}
