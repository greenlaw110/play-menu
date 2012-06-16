package play.modules.menu;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models._menu.IMenu;
import models._menu.JPAMenu;

import org.yaml.snakeyaml.Yaml;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.db.jpa.JPAPlugin;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.Scope;
import play.mvc.Scope.Params;
import play.mvc.Scope.RenderArgs;
import play.mvc.Scope.Session;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * The plugin for the Menu module.
 *
 * @author greenlaw110@gmail.com
 */
public class MenuPlugin extends PlayPlugin {

    public static final String VERSION = "1.1b";

    private static String msg_(String msg, Object... args) {
        return String.format("MenuPlugin-" + VERSION + "> %1$s",
                String.format(msg, args));
    }

    private static IMenu fact_ = null;

    private static void setMenuClass_(String clzStr) {
        Logger.info(msg_("set menu class: %s", clzStr));
        try {
            Class clz = Play.classloader.loadClass(clzStr);
            fact_ = (IMenu)clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static IMenu menuInstance() {
        try {
            return fact_._newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isJPAModel_() {
        return JPAMenu.class.equals(fact_.getClass());
    }

    @Override
    public void onConfigurationRead() {
        init_();
        if (isJPAModel_()) {
            String jpaEntities = Play.configuration.getProperty("jpa.entities", "").trim();
            if (!"".equals(jpaEntities)) {
                jpaEntities += ",models._menu.JPAMenu";
            } else {
                jpaEntities = "models._menu.JPAMenu";
            }
            Play.configuration.put("jpa.entities", jpaEntities);
        }
    }

    private void init_() {
        Properties p = Play.configuration;
        String menuClass = p.getProperty("menu.class");
        if (null == menuClass) {
            for (PlayPlugin pp : Play.pluginCollection.getEnabledPlugins()) {
                String cn = pp.getClass().getName();
                if (cn.contains("Morphia") || cn.contains("Mongo")) {
                    menuClass = "models._menu.MongoMenu";
                    break;
                }
            }
        }
        if (null == menuClass) menuClass = "models._menu.JPAMenu";
        setMenuClass_(menuClass);
    }

    private static final Pattern P_K = Pattern.compile("([^(]+)\\(([^)]+)\\)");
    public static void load(String... ymlFiles) {
        VirtualFile vf = virtualFile_(ymlFiles);
        if (null == vf) return;
        _purge();
        load_(vf);
    }
    private static void load_(VirtualFile yamlFile) {
        Logger.info(msg_("loading menu from yaml file: %s", yamlFile.relativePath()));
        String renderedYaml = TemplateLoader.load(yamlFile).render();
        Yaml yaml = new Yaml();
        try {
            startTx_();
            Object o = yaml.load(renderedYaml);
            if (o instanceof LinkedHashMap<?, ?>) {
                Map<String, IMenu> all = new HashMap<String, IMenu>();
                @SuppressWarnings("unchecked")
                Map<Object, Map<?, ?>> objects = (Map<Object, Map<?, ?>>) o;
                Map<Object, String> parents = new HashMap<Object, String>();
                for (Object key : objects.keySet()) {
                    String id = key.toString().trim();
                    Matcher matcher = P_K.matcher(id);
                    if (matcher.matches()) {
                        id = matcher.group(2);
                    }
                    Map<?, ?> mm = objects.get(key);
                    String name = (String)mm.get("name");
                    String url = (String)mm.get("url");
                    String cssClass = (String)mm.get("cssClass");
                    String title = (String)mm.get("title");
                    List<String> labels = (List<String>)mm.get("labels");
                    String parent = (String)mm.get("parent");

                    IMenu menu = menuInstance();
                    menu.setName(name);
                    menu.setUrl(url);
                    menu.setCssClass(cssClass);
                    menu.setTitle(title);
                    if (null != labels) {
                        menu.setLabels(labels);
                    }
                    menu._save();
                    if (null != parent) parents.put(menu._getId(), parent);
                    all.put(id, menu);
                }
                for (IMenu menu: all.values()) {
                    String parent = (String)parents.get(menu._getId());
                    if (null != parent) {
                        if (all.containsKey(parent)) {
                            menu.setParent(all.get(parent));
                            menu._save();
                        } else {
                            throw new RuntimeException("cannot find parent[" + parent +"] in men yaml file : " + yamlFile.relativePath());
                        }
                    }
                }
            } else {
                throw new RuntimeException("menu yml format not reconized: " + yamlFile.relativePath());
            }
            commitTx_();
        } catch (Exception e) {
            rollbackTx_();
            throw new RuntimeException(e);
        }
    }

    /*
     * return the first file found by a set of names specified
     */
    private static VirtualFile virtualFile_(String... fileNames) {
        if (fileNames.length == 0) return null;
        for (String fn: fileNames) {
            VirtualFile vf = VirtualFile.search(Play.javaPath, fn);
            if (null != vf) return vf;
        }
        return null;
    }

    private void load_() {
        startTx_(); // to init JPA em if needed
        IMenu m = menuInstance();
        if (0 < m._count()) {
            if ("yml".equalsIgnoreCase(Play.configuration.getProperty("menu.loadFrom", "db"))) {
                Logger.info(msg_("Force loading menus from yaml file. clean up menu database ..."));
                // clean db
                m._purge();
            } else {
                // reuse the data in database
                commitTx_();
                return;
            }
        }
        commitTx_(); // close the previous Tx if it's started
        String fileName = Play.configuration.getProperty("menu.yamlFile", "_menu.yml");
        VirtualFile yamlFile = virtualFile_(fileName);

        if (yamlFile == null) {
            Logger.warn(msg_("Couldn't find menu plugin initial file: %s", fileName));
            return;
        }
        load_(yamlFile);
    }

    private static void startTx_() {
        if (isJPAModel_()) {
            JPAPlugin.startTx(false);
        }
    }

    private static void commitTx_() {
        if (isJPAModel_()) {
            JPAPlugin.closeTx(false);
        }
    }

    private static void rollbackTx_() {
        if (isJPAModel_()) {
            JPAPlugin.closeTx(true);
        }
    }

    @Override
    public void afterApplicationStart() {
        load_();
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        Scope.RenderArgs binding = Scope.RenderArgs.current();
        Request request = Request.current();
        binding.put("_menu_current", request.url);
        //binding.put("_menu_editing_url", Play.configuration.getProperty("menu.editing.url", Router.reverse("_menu.Configurator.edit").url));

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
        p2_ = Pattern.compile("@\\{([\\w\\.]*).*\\}");
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

    static List<IMenu> allMenus() {
        return fact_._all();
    }

    public static MenuPlugin instance() {
        return (MenuPlugin) Play.pluginCollection.getPluginInstance(MenuPlugin.class);
    }

    private static MenuCache cache_ = new MenuCache();
    private static boolean cacheEnabled_() {
        String s = Play.configuration.getProperty("menu.cache", "false");
        return Boolean.parseBoolean(s);
    }

    public static List<IMenu> getSubMenus(IMenu parentMenu) {
        if (cacheEnabled_()) return cache_.getSubMenus(parentMenu);
        return parentMenu.getSubMenus();
    }

    public static List<IMenu> getSubMenusByLabel(IMenu parentMenu, String label) {
        if (cacheEnabled_()) return cache_.getSubMenusByLabel(parentMenu, label);
        return parentMenu.getSubMenusByLabel(label);
    }

    public static IMenu _find(String id) {
        if (cacheEnabled_()) return cache_._find(id);
        return fact_._findById(id);
    }

    public static List<IMenu> _topLevelMenus() {
        if (cacheEnabled_()) return cache_._topLevelMenus();
        return fact_._topLevelMenus();
    }

    public static List<IMenu> _topLevelMenusByLabel(String label) {
        if (cacheEnabled_()) return cache_._topLevelMenusByLabel(label);
        return fact_._topLevelMenusByLabel(label);
    }

    public static void _purge() {
        fact_._purge();
    }
}
