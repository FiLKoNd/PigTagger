import com.filkond.pigtagger.Kit;
import com.filkond.pigtagger.PigTagger;
import com.filkond.pigtagger.Tier;

public class Main {
    public static void main(String[] args) {
        PigTagger.updateTiers();
        Tier tier = Kit.SMP.getTierByNickname("xFigvam");
        System.out.println(tier);
    }
}
