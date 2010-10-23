package play.modules.menu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.core.SetClasspathOperation;

import models.IMenu;
import models._Menu;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.db.jpa.JPAPlugin;
import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.Scope;
import play.mvc.Scope.Params;
import play.mvc.Scope.RenderArgs;
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
            prototype_.loadMenu();
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
        String jpaEntities = Play.configuration.getProperty("jpa.entities", "").trim();
        if (!"".equals(jpaEntities)) {
            jpaEntities += ",models._Menu";
        } else {
            jpaEntities = "models._Menu";
        }
        Play.configuration.put("jpa.entities", jpaEntities);
    }
    
    private void init_() {
        String menuClass = Play.configuration.getProperty("menu.class", "models._Menu");
        setMenuClass(menuClass);
    }
    
    @Override
    public void afterApplicationStart() {
        init_();
    }
    
    @Override
    public void beforeActionInvocation(Method actionMethod) {
        Scope.RenderArgs binding = Scope.RenderArgs.current();
        Request request = Request.current();
        binding.put("_menu_current", request.url);

        setRenderArgs_("_menu_context");
        setRenderArgs_("_menu_label");
    }
    
    private static void setRenderArgs_(String name) {
        String val = Params.current().get(name);
        if (null == val) val = Session.current().get(name);
        if (null != val) RenderArgs.current().put(name, val);
    }
    
    private static Pattern p1_ = null; {
        p1_ = Pattern.compile("'(.*)'");
    }
    
    private static Pattern p2_ = null; {
        p2_ = Pattern.compile("@\\{(.*)\\}");
    }
    
    public static String url(IMenu menu) {
        String s = menu.getUrl();
        if (null == s) return null;
        s = s.trim();
        Matcher m = p1_.matcher(s);
        if (m.find()) {
            s = m.group(1);
        }
        m = p2_.matcher(s);
        if (m.find()) {
            s = m.group(1);
            return Router.reverse(s).url;
        } else {
            return s;
        }
    }

}
