package models._menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.Logger;
import play.db.jpa.JPABase;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
@Table(name = "play_menu")
public class JPAMenu extends Model implements IMenu {

    @Column
    public String name;
    public String cssClass;
    public String url;
    public String title;
    public String context;
    @Transient
    public Set<String> labels;
    private String labelStr_;

    @SuppressWarnings("unused")
    @PrePersist
    private void setLabels_() {
        Logger.debug("saving labels ...");
        if (null == labels) {
            return;
        }
        labelStr_ = Utils.join(labels, ",");
    }

    @SuppressWarnings("unused")
    @PostLoad
    private void loadLabels_() {
        Logger.debug("loading labels ...");
        if (labelStr_ == null) {
            labels = new HashSet();
        } else {
        	labels = new HashSet(Arrays.asList(labelStr_.split(",")));
        }
    }

    @ManyToOne
    public JPAMenu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    public List<JPAMenu> children;

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + url.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof JPAMenu) {
            JPAMenu that = (JPAMenu)o;
            return that._key().equals(this._key());
        }
        return false;
    }

    public static JPAMenu findByName(String name) {
        return find("byName", name).first();
    }
    
    @Override
    public Object _getId() {
        return getId();
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (null == name)
            throw new NullPointerException();
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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public boolean hasLabel(String label) {
    	if (null == label) return labels.size() == 0;
        return labels.contains(label);
    }

    public void setLabels(Collection<String> label) {
        if (null == label)
            return;
        this.labels = new HashSet(label);
    }

    @Override
    public IMenu getParent() {
        return parent;
    }
    
    @Override
    public void setParent(IMenu parent) {
        this.parent = (JPAMenu)parent;
    }

    @Override
    public List<IMenu> getSubMenus() {
        List<IMenu> l = new ArrayList(children);
        return l;
    }

    public List<IMenu> getSubMenusByLabel(String label) {
        List<IMenu> l = new ArrayList();
        for (IMenu m : children) {
            if (m.hasLabel(label)) {
                l.add(m);
            }
        }
        return l;
    }

    @Override
    public List<IMenu> _topLevelMenus() {
        return JPAMenu.find("parent is null").fetch();
    }

    @Override
    public List<IMenu> _topLevelMenusByLabel(String label) {
        List<IMenu> l0 = _topLevelMenus();
        List<IMenu> l = new ArrayList();
        for (IMenu m : l0) {
            if (m.hasLabel(label)) {
                l.add(m);
            }
        }
        return l;
    }
    
    @Override
    public List<IMenu> _all() {
        return (List)findAll();
    }
    
    @Override
    public IMenu _newInstance() {
        return new JPAMenu();
    }
    
    @Override
    public long _count() {
        return count();
    }

    @Override
    public IMenu _findById(String id) {
        return (JPAMenu)findById(id);
    }

    @Override
    public void _purge() {
        deleteAll();
    }
    
//    public static <T extends JPABase> T create(String name, Params params) {
//        try {
//            return (T) play.db.jpa.JPQL.instance.create("_Menu", name, params);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getCause());
//        }
//    }
//
    /**
     * Count entities
     * 
     * @return number of entities of this class
     */
    public static long count() {
        return play.db.jpa.JPQL.instance.count("JPAMenu");
    }

//    /**
//     * Count entities with a special query. Example : Long moderatedPosts =
//     * Post.count("moderated", true);
//     * 
//     * @param query
//     *            HQL query or shortcut
//     * @param params
//     *            Params to bind to the query
//     * @return A long
//     */
//    public static long count(String query, Object... params) {
//        return play.db.jpa.JPQL.instance.count("JPAMenu", query, params);
//    }

    /**
     * Find all entities of this type
     */
    public static <T extends JPABase> List<T> findAll() {
        return play.db.jpa.JPQL.instance.findAll("JPAMenu");
    }

    /**
     * Find the entity with the corresponding id.
     * 
     * @param id
     *            The entity id
     * @return The entity
     * @throws Exception
     */
    public static <T extends JPABase> T findById(Object id) {
        try {
            return (T) play.db.jpa.JPQL.instance.findById("JPAMenu", id);
        } catch (Exception e) {
            Logger.warn(e, "Error findById(%1$s) for entity class: ", id, "JPAMenu");
            return null;
        }
    }

    /**
     * Prepare a query to find entities.
     * 
     * @param query
     *            HQL query or shortcut
     * @param params
     *            Params to bind to the query
     * @return A JPAQuery
     */
    public static JPAQuery find(String query, Object... params) {
        return play.db.jpa.JPQL.instance.find("JPAMenu", query, params);
    }

//    /**
//     * Prepare a query to find *all* entities.
//     * 
//     * @return A JPAQuery
//     */
//    public static JPAQuery all() {
//        return play.db.jpa.JPQL.instance.all("_Menu");
//    }
//
//    /**
//     * Batch delete of entities
//     * 
//     * @param query
//     *            HQL query or shortcut
//     * @param params
//     *            Params to bind to the query
//     * @return Number of entities deleted
//     */
//    public static int delete(String query, Object... params) {
//        return play.db.jpa.JPQL.instance.delete("_Menu", query, params);
//    }

    /**
     * Delete all entities
     * 
     * @return Number of entities deleted
     */
    public static int deleteAll() {
        return play.db.jpa.JPQL.instance.deleteAll("JPAMenu");
    }

}
