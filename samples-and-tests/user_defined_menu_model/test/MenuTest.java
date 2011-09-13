
import java.util.List;

import models.MongoMenu;
import models._menu.IMenu;
import models._menu.JPAMenu;

import org.junit.Before;
import org.junit.Test;

import play.modules.menu.MenuPlugin;
import play.test.UnitTest;

public class MenuTest extends UnitTest {
    
    @Before
    public void prepareData() {
        MenuPlugin._purge();
        MenuPlugin.load("menu.yml");
    }
    
//    @Test
//    public void testDefaultMenuImpl() {
//        IMenu play = JPAMenu.findByName("play");
//        assertNotNull(play);
//        
//        assertTrue(play.getSubMenus().size() == 4);
//       
//        IMenu module = JPAMenu.findByName("modules");
//        assertTrue(module.getName().equals("modules"));
//        
//        assertTrue(module.getSubMenus().size() == 2);
//        assertTrue(module.getParent().equals(play));
//        
//        List<JPAMenu> l = JPAMenu.find("parent is null").fetch();
//        assertTrue(l.size() == 2);
//        assertTrue(l.contains(play));
//        
//        IMenu doc = JPAMenu.findByName("learn");
//        List<IMenu> l0 = doc.getSubMenus();
//        assertTrue(l0.isEmpty());
//        
//        assertTrue(play.hasLabel("play"));
//        assertSame(1, play._topLevelMenusByLabel("play").size());
//        assertSame(1, play._topLevelMenusByLabel("_global").size());
//        assertSame(2, module.getSubMenusByLabel("module").size());
//        
//    }
    
    @Test
    public void testCustomMenuImpl() {
        IMenu play = MongoMenu.findByName("play");
        assertNotNull(play);
        
        assertSame(play.getSubMenus().size(), 4);
       
        IMenu module = MongoMenu.findByName("modules");
        assertTrue(module.getName().equals("modules"));
        
        assertSame(module.getSubMenus().size(), 2);
        assertTrue(module.getParent().equals(play));
        
        List<MongoMenu> l = MongoMenu.filter("parentId", null).asList();
        assertSame(l.size(), 2);
        assertTrue(l.contains(play));
        
        IMenu doc = MongoMenu.findByName("learn");
        List<IMenu> l0 = doc.getSubMenus();
        assertTrue(l0.isEmpty());

        assertTrue(play.hasLabel("play"));
        assertSame(play._topLevelMenusByLabel("play").size(), 1);
        assertSame(2, module.getSubMenusByLabel("module").size());
    }
}
