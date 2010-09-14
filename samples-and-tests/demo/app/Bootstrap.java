import models.Menu;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;


@OnApplicationStart
public class Bootstrap extends Job {
    @Override
    public void doJob() throws Exception {
        if (Menu.count() == 0) Fixtures.load("menu.yml");
    }
}
