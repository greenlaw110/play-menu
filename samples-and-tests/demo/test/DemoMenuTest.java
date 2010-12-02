
import java.util.List;

import models.IMenu;
import models._Menu;

import org.junit.Test;

import play.modules.menu.MenuPlugin;
import play.test.UnitTest;

public class DemoMenuTest extends UnitTest {
    
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
    public void testReverseRouting() {
        IMenu m = new _Menu();
        m.setUrl("@{Demo.index}");
        String url = MenuPlugin.url(m);
        assertEquals("/", url);
        m.setUrl("@{Demo.doc(abc,dd)}");
        url = MenuPlugin.url(m);
        assertEquals("/doc", url);
    }
    
}
