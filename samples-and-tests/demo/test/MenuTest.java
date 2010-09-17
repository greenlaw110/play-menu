
import java.util.List;

import models.IMenu;
import models.Menu;
import models._Menu;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.db.Model;
import play.modules.morphia.utils.MorphiaFixtures;
import play.test.Fixtures;
import play.test.UnitTest;

public class MenuTest extends UnitTest {
    
    @Before
    public void prepareData() {
        /*
         * _Menu delete is a little bit complicated. You can't do
         * 1. Fixtures.deleteAll(): b/c _Menu is not managed by Play b/c it's not an app model
         * 2. _Menu.deleteAll(): b/c Integrity constraint violation due to parent/children relationship
        _Menu root = new _Menu();
        List<IMenu> ml = root.getTopLevelMenus();
        for (IMenu m: ml) {
            deleteMenu((_Menu)m);
        }
        
        Fixtures.load("_menu.yml");
         */
        MorphiaFixtures.deleteAll();
        Fixtures.load("menu.yml");
    }
    
    private void deleteMenu(_Menu m) {
        List<IMenu> ml = m.getSubMenus();
        for (IMenu sm: ml) {
            deleteMenu((_Menu)sm);
        }
        try {
            m.delete();
        } catch (Exception e) {
            Logger.error(e, "error delete menu: %1$s", m.getName());
        }
    }
    
    @Test
    public void testDefaultMenuImpl() {
        IMenu play = _Menu.findByName("play");
        assertNotNull(play);
        
        assertTrue(play.getSubMenus().size() == 4);
       
        IMenu module = _Menu.findByName("modules");
        assertTrue(module.getName().equals("modules"));
        
        assertTrue(module.getSubMenus().size() == 2);
        assertTrue(module.getParentMenu().equals(play));
        
        List<_Menu> l = _Menu.find("parent is null").fetch();
        assertTrue(l.size() == 2);
        assertTrue(l.contains(play));
        
        IMenu doc = _Menu.findByName("learn");
        List<IMenu> l0 = doc.getSubMenus();
        assertTrue(l0.isEmpty());
        
        assertTrue(play.hasLabel("play"));
        assertSame(1, play.getTopLevelMenusByLabel("play").size());
        assertSame(1, play.getTopLevelMenusByLabel("_global").size());
        assertSame(2, module.getSubMenusByLabel("module").size());
        
    }
    
    @Test
    public void testCustomMenuImpl() {
        IMenu play = Menu.findByName("play");
        assertNotNull(play);
        
        assertSame(play.getSubMenus().size(), 4);
       
        IMenu module = Menu.findByName("modules");
        assertTrue(module.getName().equals("modules"));
        
        assertSame(module.getSubMenus().size(), 2);
        assertTrue(module.getParentMenu().equals(play));
        
        List<_Menu> l = Menu.filter("parent", null).asList();
        assertSame(l.size(), 2);
        assertTrue(l.contains(play));
        
        IMenu doc = Menu.findByName("learn");
        List<IMenu> l0 = doc.getSubMenus();
        assertTrue(l0.isEmpty());

        assertTrue(play.hasLabel("play"));
        assertSame(play.getTopLevelMenusByLabel("play").size(), 2);
        assertSame(2, module.getSubMenusByLabel("module").size());
    }
}
