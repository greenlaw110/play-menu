package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import play.Logger;
import play.db.jpa.JPABase;
import play.db.jpa.Model;
import play.db.jpa.GenericModel.JPAQuery;
import play.mvc.Scope.Params;

@Entity
@Table(name="_menu")
public class _Menu extends Model implements IMenu {
    
    @Column(unique=true)
    public String name;
    public String url;
    public String title;
    public String context;
    
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
    
    @Override
    public List<IMenu> getTopLevelMenus() {
        return _Menu.find("parent is null").fetch();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByContext(String context) {
        return _Menu.find("parent is null and context = ?", context).fetch();
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
