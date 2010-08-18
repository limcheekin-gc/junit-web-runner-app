package com.vobject.appengine.gorm


// import com.google.appengine.api.datastore.Key
import javax.persistence.*;

@Entity
class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @Version
    Long version
    
    String controller
    
    String action

    Long roleId

    static constraints = {
    	id visible:false
    }

    String toString() {
        "${id},${controller},${action},${roleId}"
    }
}
