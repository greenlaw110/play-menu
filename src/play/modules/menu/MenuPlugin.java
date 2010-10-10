package play.modules.menu;

import java.lang.reflect.Method;
import java.util.ArrayList;

import models.IMenu;
import models._Menu;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.db.jpa.JPAPlugin;
import play.mvc.Http.Request;
import play.mvc.Scope;
import play.mvc.Scope.Session;
import play.test.Fixtures;

/**
 * The plugin for the Menu module.
 * 
 * @author greenlaw110@gmail.com
 */
public class MenuPlugin extends PlayPlugin {
    
    private static IMenu prototype_ = null;
    
    public static void setMenuClass(Class clz) {
        try {
            prototype_ = (IMenu)clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void setMenuClass(String clzStr) {
        
        try {
            Class clz = Play.classloader.loadClass(clzStr);
            setMenuClass(clz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static IMenu menuInstance() {
        return prototype_;
    }
    
    @Override
    public void onConfigurationRead() {
        String menuClass = Play.configuration.getProperty("menu.class", "models._Menu");
        if (menuClass.equals("models._Menu")) {
            String jpaEntities = Play.configuration.getProperty("jpa.entities", "").trim();
            if (!"".equals(jpaEntities)) {
                jpaEntities += ",models._Menu";
            } else {
                jpaEntities = "models._Menu";
            }
            Play.configuration.put("jpa.entities", jpaEntities);
            prototype_ = new _Menu();
        } else {
            setMenuClass(menuClass);
        }
    }
    
    @Override
    public void afterApplicationStart() {
        if (Boolean.parseBoolean(Play.configuration.getProperty("menu.no_def_impl", "false"))) {
            Logger.info("default JPA menu model disabled");
            return;
        }
        Logger.info("loading menu conf...");
        JPAPlugin.startTx(false);
        try {
            if (_Menu.count() > 0) return;
            Fixtures.load("_menu.yml");
        } catch (Exception e) {
            Logger.warn(e, "error loading menu from menu.yml");
        } finally {            
            JPAPlugin.closeTx(false);
        }
        Logger.info("Menu loaded");
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        if (Boolean.parseBoolean(Play.configuration.getProperty("menu.no_def_impl", "false"))) { 
            return;
        }
        if (_Menu.count() == 0) return;
        
        Scope.RenderArgs binding = Scope.RenderArgs.current();
        Request request = Request.current();
        binding.put("_menu_current", request.url);
        
        //Object topMenuList = new ArrayList();
        //_Menu m = new _Menu();
        String ctx = Session.current().get("_menu.context");
        binding.put("_menu_context", ctx);
        String label = Session.current().get("_menu_label");
        binding.put("_menu_label", label);
        // always get all top level menus
        // let tag lib to process based on label/context setting
        // topMenuList = m.getTopLevelMenus();
        
        //binding.put("_menu_top_list", topMenuList);
    }

}
