package models;

import java.util.ArrayList;
import java.util.Arrays;
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
import play.mvc.Scope.Params;
import play.utils.Utils;

@Entity
@Table(name="_menu")
public class _Menu extends Model implements IMenu {
    
    @Column(unique=true)
    public String name;
    public String url;
    public String title;
    public String context;
    @Transient
    public Set<String> tags;
    private String tagStr_;
    @PrePersist
    private void saveTags_() {
        Logger.debug("saving tags ...");
        if (null == tags) {
            return;
        }
        tagStr_ = Utils.join(tags, ",");
    }
    @PostLoad
    private void loadTags_() {
        Logger.debug("loading tags ...");
        tags = new HashSet(Arrays.asList(tagStr_.split(",")));
    }
    
    @ManyToOne
    public _Menu parent;
    
    @OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
    public List<_Menu> children;
    
    public static _Menu findByName(String name) {
        return find("byName", name).first();
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
    public String getContext() {
        return context;
    }
    
    @Override
    public void setContext(String context) {
        this.context = context;
    }
    
    @Override
    public boolean taggedBy(String tag) {
        return tags.contains(tag);
    }
    
    public void setTags(Set<String> tags) {
        if (null == tags) return;
        this.tags = new HashSet(tags);
    }
    
    @Override
    public IMenu getParentMenu() {
        return parent;
    }
    
    @Override
    public List<IMenu> getSubMenus() {
        List<IMenu> l = new ArrayList(children);
        return l;
    }
    
    @Override
    public List<IMenu> getSubMenusByContext(String context) {
        List<IMenu> l = new ArrayList();
        for (IMenu m: children) {
            if (context.equalsIgnoreCase(m.getContext())) {
                l.add(m);
            }
        }
        return l;
    }
    
    public List<IMenu> getSubMenusByTag(String tag) {
        List<IMenu> l = new ArrayList();
        for (IMenu m: children) {
            if (m.taggedBy(tag)) {
                l.add(m);
            }
        }
        return l;
    }
    
    @Override
    public List<IMenu> getTopLevelMenus() {
        return _Menu.find("parent is null").fetch();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByContext(String context) {
        return _Menu.find("parent is null and context = ?", context).fetch();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByTag(String tag) {
        List<IMenu> l0 = getTopLevelMenus();
        List<IMenu> l = new ArrayList();
        for (IMenu m: l0) {
            if (m.taggedBy(tag)) {
                l.add(m);
            }
        }
        return l;
    }
    
    public static <T extends JPABase> T create(String name, Params params) {
        try {
            return (T)play.db.jpa.JPQL.instance.create("_Menu", name, params);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Count entities
     * @return number of entities of this class
     */
    public static long count() {
        return play.db.jpa.JPQL.instance.count("_Menu");
    }

    /**
     * Count entities with a special query.
     * Example : Long moderatedPosts = Post.count("moderated", true);
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return A long
     */
    public static long count(String query, Object... params) {
        return play.db.jpa.JPQL.instance.count("_Menu", query, params);
    }

    /**
     * Find all entities of this type
     */
    public static <T extends JPABase> List<T> findAll() {
        return play.db.jpa.JPQL.instance.findAll("_Menu");
    }

    /**
     * Find the entity with the corresponding id.
     * @param id The entity id
     * @return The entity
     * @throws Exception 
     */
    public static <T extends JPABase> T findById(Object id) {
        try {
            return (T)play.db.jpa.JPQL.instance.findById("_Menu", id);
        } catch (Exception e) {
            Logger.warn(e, "Error findById(%1$s) for entity class: ", id, "_Menu");
            return null;
        }
    }

    /**
     * Prepare a query to find entities.
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return A JPAQuery
     */
    public static JPAQuery find(String query, Object... params) {
        return play.db.jpa.JPQL.instance.find("_Menu", query, params);
    }

    /**
     * Prepare a query to find *all* entities.
     * @return A JPAQuery
     */
    public static JPAQuery all() {
        return play.db.jpa.JPQL.instance.all("_Menu");
    }

    /**
     * Batch delete of entities
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return Number of entities deleted
     */
    public static int delete(String query, Object... params) {
        return play.db.jpa.JPQL.instance.delete("_Menu", query, params);
    }

    /**
     * Delete all entities
     * @return Number of entities deleted
     */
    public static int deleteAll() {
        return play.db.jpa.JPQL.instance.deleteAll("_Menu");
    }

}
