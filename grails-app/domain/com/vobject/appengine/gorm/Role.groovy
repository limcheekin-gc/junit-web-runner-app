package com.vobject.appengine.gorm

import javax.persistence.*;

@Entity
class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @Version
    Long version
    
    String name
    String description
    String createdBy
    Date dateCreated
    String lastUpdatedBy
    Date lastUpdated
    
    @Transient
    Set<Permission> permissions

    static constraints = {
    	id visible:false
    }
    
    Set getPermissions() {
	if (!permissions && id) {
	    Permission.withTransaction { 
		permissions = Permission.findAllByRoleId(id)	
	    }
	    log.debug "permissions.size() = ${permissions.size()}"
	}
	permissions;
    }

    def deleteAllPermissions() {
    	getPermissions()
	    permissions.batchDelete()
	    permissions.clear()
	    permissions = null;
    }

    String toString() {
	"${id},${name},${description}"
    }
}
