import models.MongoMenu;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.menu.MenuPlugin;
import play.modules.morphia.utils.MorphiaFixtures;
import play.test.Fixtures;


@OnApplicationStart
public class Bootstrap extends Job {
    @Override
    public void doJob() throws Exception {
        MorphiaFixtures.delete(models.MongoMenu.class);
        MorphiaFixtures.load("menu.yml");
        MenuPlugin.setMenuClass(models.MongoMenu.class);
    }
}
