package com.vobject.appengine.gorm

import org.datanucleus.jpa.annotations.Extension
import javax.persistence.*

@Entity
class User implements Serializable {
  @Id
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  @Extension (vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
  String id

  @Version
  Long version

  String email
  String password
  String name
  String createdBy
  Date dateCreated
  String lastUpdatedBy
  Date lastUpdated
  

  static constraints = {
      id visible:false
  }
  
  String toString(){
    "(${id},${email},${password},${name},${version})"
  }
}

