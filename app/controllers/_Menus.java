package controllers;

import java.util.List;

import models.Menu;
import models._Menu;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.Scope.Session;
import play.templates.JavaExtensions;

/**
 * 
 * @author greenl
 */
public class _Menus extends Controller {
    @Before
    static void prepareMenu() throws InstantiationException, IllegalAccessException {
        Scope.RenderArgs binding = Scope.RenderArgs.current();
        binding.put("_menu_current", request.url);
        Menu m = models.Menu.class.newInstance();
        String ctx = Session.current().get("_menu.context");
        if (null != ctx) binding.put("_menu_context", ctx);
        String tag = Session.current().get("_menu.tag");
        if (null != tag) binding.put("_menu_tag", tag);
        Object topMenuList = ctx == null ? m.getTopLevelMenus() : m.getTopLevelMenusByContext(ctx);
        binding.put("_menu_top_list", topMenuList);
    }
}
