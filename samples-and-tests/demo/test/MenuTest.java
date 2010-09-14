
import java.util.List;

import models.IMenu;
import models.Menu;
import models._Menu;

import org.junit.Test;

import play.test.UnitTest;

public class MenuTest extends UnitTest {

    
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
        assertTrue(l.size() == 1);
        assertTrue(l.contains(play));
        
        IMenu doc = _Menu.findByName("learn");
        List<IMenu> l0 = doc.getSubMenus();
        assertTrue(l0.isEmpty());
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
        assertSame(l.size(), 1);
        assertTrue(l.contains(play));
        
        IMenu doc = Menu.findByName("learn");
        List<IMenu> l0 = doc.getSubMenus();
        assertTrue(l0.isEmpty());
    }
}
