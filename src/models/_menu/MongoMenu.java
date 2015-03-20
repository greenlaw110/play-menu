package models._menu;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


import org.bson.types.ObjectId;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.exceptions.ConfigurationException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

public class MongoMenu implements IMenu {
    
    private ObjectId _id;
    @Override
    public Object _getId() {
        return _id;
    }
    public void setId(String id) {
        _id = new ObjectId(id);
    }

    private String name;
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        if (null == name) throw new NullPointerException();
        this.name = name;
    }

    private String cssClass;
    @Override
    public String getCssClass() {
        return cssClass;
    }
    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    private String url;
    @Override
    public String getUrl() {
        return url;
    }
    @Override
    public void setUrl(String url) {
        if (null == url) throw new NullPointerException();
        this.url = url;
    }

    private String title;
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    private Set<String> labels = new HashSet<String>();
    @Override
    public boolean hasLabel(String label) {
        return labels.contains(label);
    }

    @Override
    public void setLabels(Collection<String> labels) {
        this.labels = new HashSet<String>(labels);
    }

    private ObjectId parentId;
    private IMenu parent_;
    @Override
    public IMenu getParent() {
        if (null != parent_) return parent_;
        if (null == parentId) return null;
        parent_ = MongoDB.findById(parentId);
        return parent_;
    }
    
    @Override
    public void setParent(IMenu parent) {
        if (null == parent) {
            parentId = null;
            parent_ = null;
            return;
        }
        MongoMenu p = (MongoMenu)parent;
        if (null == p._id) p._save();
        parentId = p._id;
        parent_ = MongoDB.findById(parentId);
    }

    @Override
    public List<IMenu> getSubMenus() {
        DBObject ref = MongoDB.ref_("parentId", _id);
        return MongoDB.find(ref);
    }

    @Override
    public List<IMenu> getSubMenusByLabel(String label) {
        DBObject ref = MongoDB.ref_("parentId", _id);
        ref.put("labels", label);
        return MongoDB.find(ref);
    }
    
    @Override
    public List<IMenu> _topLevelMenus() {
        DBObject ref = MongoDB.ref_("parentId", null);
        return MongoDB.find(ref);
    }

    @Override
    public List<IMenu> _topLevelMenusByLabel(String label) {
        DBObject ref = MongoDB.ref_("parentId", null);
        ref.put("labels", label);
        return MongoDB.find(ref);
    }
    
    @Override
    public List<IMenu> _all() {
        return MongoDB.all();
    }
    
    @Override
    public IMenu _newInstance() {
        return new MongoMenu();
    }
    
    @Override 
    public long _count() {
        return MongoDB.col.count();
    }
    
    @Override 
    public IMenu _findById(String id) {
        return MongoDB.findById(new ObjectId(id));
    }
    
    @Override
    public void _save() {
        MongoDB.save(this);
    }
    
    @Override
    public void _delete() {
        MongoDB.delete(this);
    }
    
    @Override
    public void _purge() {
        MongoDB.purge();
    }
    
    public MongoMenu() {
        _id = new ObjectId();
        if (null == MongoDB.col) MongoDB.init(Play.configuration);
    }

    private MongoMenu(ObjectId id, ObjectId parentId, String name, String cssClass, String url, String title, Set<String> labels) {
        this._id = id;
        this.name = name;
        this.cssClass = cssClass;
        this.url = url;
        this.title = title;
        this.parentId = parentId;
        this.labels = labels;
        
        if (null == MongoDB.col) MongoDB.init(Play.configuration);
    }

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
        if (o instanceof MongoMenu) {
            MongoMenu that = (MongoMenu)o;
            return that._id.equals(this._id);
        }
        return false;
    }

    private static class MongoDB {
        private static DBCollection col;
        private static Mongo initMongo(Properties p) {
            Mongo m = null;
            String seeds = getProp_(p, null, "menu.mongo.db.seeds", "morphia.db.seeds");
            if (null != seeds) {
                String[] sa = seeds.split("[,;\\s]");
                if (sa.length != 0) {
                    List<ServerAddress> addrs = new ArrayList<ServerAddress>(sa.length);
                    for (String seed: sa) {
                        String[] hp = seed.split(":");
                        String host = hp[0];
                        int port = 27107;
                        if (hp.length > 1) {
                            port = Integer.parseInt(hp[1]);
                        }
                        try {
                            addrs.add(new ServerAddress(host, port));
                        } catch (UnknownHostException e) {
                            Logger.error(e, "error creating mongo connection to %s:%s",
                                    host, port);
                        }
                    }
                    if (0 < addrs.size()) {
                        m = new Mongo(addrs);
                    }
                }
            }
            if (null == m) {
                String host = getProp_(p, "localhost", "menu.mongo.db.host", "morphia.db.host", "mongo.host");
                String port = getProp_(p, "27017", "menu.mongo.db.port", "morphia.db.port", "mongo.port");
                String[] ha = host.split("[,\\s;]+");
                String[] pa = port.split("[,\\s;]+");
                int len = ha.length;
                if (len != pa.length)
                    throw new ConfigurationException(
                            "host and ports number does not match");
                if (1 == len) {
                    try {
                        return new Mongo(ha[0], Integer.parseInt(pa[0]));
                    } catch (Exception e) {
                        throw new ConfigurationException(String.format("Cannot connect to mongodb at %s:%s", host, port));
                    }
                }
                List<ServerAddress> addrs = new ArrayList<ServerAddress>(ha.length);
                for (int i = 0; i < len; ++i) {
                    try {
                        addrs.add(new ServerAddress(ha[i], Integer.parseInt(pa[i])));
                    } catch (Exception e) {
                        Logger.error(e, "Error creating mongo connection to %s:%s",
                                host, port);
                    }
                }
                if (addrs.isEmpty()) {
                    throw new ConfigurationException("Cannot connect to mongodb: no replica can be connected");
                }
                return new Mongo(addrs);
            }
            return m;
        }
        private static String getProp_(Properties p, String defVal, String... confNames) {
            String val = null;
            for (String conf: confNames) {
                val = p.getProperty(conf);
                if (null != val) break;
            }
            return null == val ? defVal : val;
        }
        static void init(Properties p) {
            Mongo m = initMongo(p);
            
            String dbname = getProp_(p, "test", "menu.mongo.db.name", "morphia.db.name", "mongo.database");
            DB db = m.getDB(dbname);
            
            String username = getProp_(p, null, "menu.mongo.db.username", "morphia.db.username", "mongo.username");
            String password = getProp_(p, null, "menu.mongo.db.password", "morphia.db.password", "mongo.password");
            if (null != username && null != password) {
                db.authenticate(username, password.toCharArray());
            }
            
            String colName = p.getProperty("menu.mongo.col.name", "play_menu");
            col = db.getCollection(colName);
            
            col.ensureIndex("labels");
            col.ensureIndex("parentId");
        }
        static MongoMenu findById(ObjectId id) {
            DBObject o = col.findOne(id);
            return null == o ? null : fromDb_(o);
        }
        static List<IMenu> find(DBObject ref) {
            DBCursor cur = col.find(ref);
            List<IMenu> l = new ArrayList<IMenu>();
            List<DBObject> l0 = cur.toArray();
            for (DBObject o: l0) {
                l.add(fromDb_(o));
            }
            return l;
        }
        static List<IMenu> all() {
            return find(new BasicDBObject());
        }
        static DBObject save(MongoMenu m) {
            if (null == m._id) m._id = new ObjectId();
            DBObject o = toDb_(m);
            DBObject ref = ref_(o);
            return col.findAndModify(ref, null, null, false, o, true, true);
        }
        private static DBObject ref_(DBObject o) {
            DBObject ref = new BasicDBObject();
            ref.put("_id", o.get("_id"));
            return ref;
        }
        private static DBObject ref_(String key, Object val) {
            DBObject ref = ref_();
            ref.put(key, val);
            return ref;
        }
        private static DBObject ref_() {
            return new BasicDBObject();
        }
        static void purge() {
            DBObject ref = ref_();
            col.remove(ref);
        }
        static void delete(MongoMenu m) {
            col.remove(toDb_(m));
        }
        static DBObject toDb_(MongoMenu m) {
            BasicDBObject o = new BasicDBObject();
            o.put("_id", m._id);
            o.put("name", m.name);
            o.put("cssClass", m.cssClass);
            o.put("title", m.title);
            o.put("url", m.url);
            o.put("parentId", m.parentId);
            o.put("labels", m.labels);
            return o;
        }
        static MongoMenu fromDb_(DBObject o) {
            ObjectId id = (ObjectId) o.get("_id");
            String name = (String) o.get("name");
            String cssClass = (String) o.get("cssClass");
            String title = (String) o.get("title");
            String url = (String) o.get("url");
            ObjectId parentId = (ObjectId) o.get("parentId");
            Set<String> labels = new HashSet<String>((Collection<String>) o.get("labels"));
            
            return new MongoMenu(id, parentId, name, cssClass, url, title, labels);
        }
    }
}
