package play.modules.menu;

import java.util.ArrayList;
import java.util.List;

import models._menu.IMenu;

public class MenuCache {
    private List<IMenu> all_ = null;
    private List<IMenu> a() {
        if (null == all_) all_ = MenuPlugin.allMenus();
        return all_;
    }
    public void clear() {
        all_ = null;
    }
    public List<IMenu> _topLevelMenus() {
        return f_(new F(){
            @Override public boolean check(IMenu menu) {
                return menu.getParent() == null;
            }
        });
    }
    public List<IMenu> _topLevelMenusByLabel(final String label) {
        return f_(new F(){
            @Override public boolean check(IMenu menu) {
                return menu.getParent() == null && menu.hasLabel(label);
            }
        });
    }
    public IMenu _find(final Object id) {
        if (null == id) throw new NullPointerException();
        List<IMenu> l = f_(new F(){
            @Override public boolean check(IMenu menu) {
                return id.toString().equals(menu._getId().toString());
            }
        });
        return l.size() == 0 ? null : l.get(0);
    }
    public List<IMenu> getSubMenus(final IMenu parentMenu) {
        return f_(new F(){
            @Override public boolean check(IMenu menu) {
                return parentMenu.equals(menu.getParent());
            }
        });
    }
    public List<IMenu> getSubMenusByLabel(final IMenu parentMenu, final String label) {
        return f_(new F(){
            @Override public boolean check(IMenu menu) {
                return menu.getParent().equals(parentMenu) && menu.hasLabel(label);
            }
        });
    }
    private List<IMenu> f_(F f) {
        List<IMenu> l = new ArrayList();
        for (IMenu menu: a()) {
            if (f.check(menu)) l.add(menu);
        }
        return l;
    }
    private interface F {
        boolean check(IMenu menu);
    }
}
