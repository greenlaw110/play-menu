package models;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;


@Entity(value="my_menu")
public class Menu extends Model implements IMenu {
    
    @Indexed(unique=true, dropDups=true)
    public String name;
    public String url;
    public String title;
    public String context;
    
    @Reference(value="p")
    public Menu parent;
    
    public static Menu findByName(String name) {
        return (Menu)filter("name", name).get();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public String getContext() {
        return context;
    }
    
    @Override
    public IMenu getParentMenu() {
        return parent;
    }
    
    @Override
    public List<IMenu> getSubMenus() {
        return Menu.filter("parent", this).asList();
    }
    
    @Override
    public List<IMenu> getSubMenusByContext(String context) {
        return Menu.filter("parent", this).filter("context", context).asList();
    }
    
    @Override
    public List<IMenu> getTopLevelMenus() {
        return (List<IMenu>)filter("parent", null).asList();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByContext(String context) {
        return filter("parent", null).filter("context", context).asList();
    }
}
