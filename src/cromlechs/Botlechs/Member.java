package cromlechs.Botlechs;

import com.sun.source.tree.Tree;

import java.util.HashMap;
import java.util.TreeSet;

public class Member {
    private static HashMap<String, Exclusivity> vip;

    public static void main(String args[]) {
        vip = new HashMap<>();
        vip.put("259841719141531649", Exclusivity.ADMIN);//EmperorBob
        vip.put("350503897644924929", Exclusivity.DEVELOPER);//Cromlechs
        vip.put("757373268784316486", Exclusivity.ADMIN);//Botlechs
    }

    public static Exclusivity getAccessLevel(String user) {
        return vip.getOrDefault(user, Exclusivity.STANDARD);
    }

    public enum Exclusivity {
        STANDARD((byte)0),
        ADMIN((byte)2),
        DEVELOPER((byte)127);

        private int level;
        private Exclusivity(byte level) {
            this.level = level;
        }
        public boolean hasAccess(String user) {
            if(level == STANDARD.level) {
                return true;
            } else {
                return getAccessLevel(user).level >= level;
            }
        }
    };
}
