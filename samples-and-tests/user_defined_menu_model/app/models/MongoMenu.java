package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models._menu.IMenu;

import org.bson.types.ObjectId;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;


@Entity(value="play_menu")
public class MongoMenu extends Model implements IMenu {
    
    @Indexed(unique=true, dropDups=true)
    public String name;
    public String cssClass;
    public String url;
    public String title;
    public String context;
    public Set<String> labels;
    
    private ObjectId parentId = null;
    
    @Override
    public String toString() {
        return name;
    }
    
    public static MongoMenu findByName(String name) {
        return (MongoMenu)filter("name", name).get();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        if (null == name) throw new NullPointerException();
        this.name = name;
    }
    
    public String getCssClass() {
        return cssClass;
    }
    
    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public boolean hasLabel(String label) {
        if (null == labels) return false;
        return labels.contains(label);
    }
    
    @Override
    public void setLabels(Collection<String> labels) {
        if (null == labels) return;
        this.labels = new HashSet(labels);
    }
    
    @Override
    public IMenu getParent() {
        return (MongoMenu)q().filter("_id", parentId)._get();
    }
    
    private MorphiaQuery parentFilter_() {
        return MongoMenu.filter("parentId", this._getId());
    }
    
    @Override
    public List<IMenu> getSubMenus() {
        List<MongoMenu> menus = parentFilter_().asList();
        List<IMenu> l = new ArrayList<IMenu>();
        l.addAll(menus);
        return l;
    }
    
    @Override
    public List<IMenu> getSubMenusByLabel(String label) {
        List<MongoMenu> menus = parentFilter_().filter("labels", label).asList();
        List<IMenu> l = new ArrayList<IMenu>();
        l.addAll(menus);
        return l;
    }
    
    @Override
    public List<IMenu> _topLevelMenus() {
        List<MongoMenu> menus = MongoMenu.filter("parentId", null).asList();
        List<IMenu> l = new ArrayList<IMenu>();
        l.addAll(menus);
        return l;
    }
    
    @Override
    public List<IMenu> _topLevelMenusByLabel(String label) {
        List<MongoMenu> menus = MongoMenu.filter("parentId", null).filter("labels", label).asList();
        List<IMenu> l = new ArrayList<IMenu>();
        l.addAll(menus);
        return l;
    }

    @Override
    public List<IMenu> _all() {
        return (List)MongoMenu.all().asList();
    }

    @Override
    public long _count() {
        return MongoMenu.count();
    }

    @Override
    public IMenu _findById(String id) {
        return (MongoMenu)MongoMenu.findById(id);
    }

    @Override
    public Object _getId() {
        return getId();
    }

    @Override
    public IMenu _newInstance() {
        return new MongoMenu();
    }

    @Override
    public void _purge() {
        ds().getCollection(MongoMenu.class).drop();
    }

    @Override
    public void setParent(IMenu parent) {
        parentId = (ObjectId)parent._getId();
    }
    
}
