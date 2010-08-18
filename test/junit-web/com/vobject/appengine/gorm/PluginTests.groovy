package com.vobject.appengine.gorm
import com.google.appengine.api.datastore.KeyFactory

class PluginTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
				clearData()
    }

    protected void tearDown() {
        clearData()
				super.tearDown()
    }

   private void clearData() {    
         def list = new ArrayList(User.list()) 
         list.addAll(Role.list())
         list.addAll(Permission.list())
         list.batchDelete()
    }
   
    void testNewBatchInsert() {
        def users = [new User(email:"anonymous@anonymous.com", name:"Anonymous"),
	             new User(email:"admin@vobject.com", password:"admin", name:"Administrator"),
	             new User(email:"user1@vobject.com", password:"user1", name:"User 1")]
       	users.batchSave()
				assertEquals "testNewBatchInsert", users.size(), User.count()
    }

    void testAppendBatchInsert() {
        [new User(email:"anonymous@anonymous.com", name:"Anonymous"),
	 new User(email:"admin@vobject.com", password:"admin", name:"Administrator"),
	 new User(email:"user1@vobject.com", password:"user1", name:"User 1")].batchSave()
        def users = new ArrayList(User.list()) // to prevent java.lang.UnsupportedOperationException: Query result sets are not modifiable
        users <<  new User(email:"user2@vobject.com", password:"user2", name:"User 2")
        users <<  new User(email:"user3@vobject.com", password:"user3", name:"User 3")
        users.batchSave()
        assertEquals "testAppendBatchInsert", users.size(), User.count()
    }

    void testBatchDelete() {
        [new User(email:"anonymous@anonymous.com", name:"Anonymous"),
	 new User(email:"admin@vobject.com", password:"admin", name:"Administrator"),
	 new User(email:"user1@vobject.com", password:"user1", name:"User 1")].batchSave()
        User.list().batchDelete()
        assertEquals "testBatchDelete", 0, User.count()
    }

    void testBatchUpdate() {
        [new User(email:"anonymous@anonymous.com", name:"Anonymous"),
				 new User(email:"admin@vobject.com", password:"admin", name:"Administrator"),
				 new User(email:"user1@vobject.com", password:"user1", name:"User 1")].batchSave()
				def users = User.list() 
				// render "testBatchUpdate. Get users after first insert: <br />"
				// renderUsers(users)
				users[2].name = "Modified User 1"
				def keys = users.batchSave()
				assertEquals 	"testBatchUpdate - Number of keys return should be 3", 3, keys.size()
				// render "testBatchUpdate. Get users after batch update: <br />"
				// printUsers (User.list()) // BUG? After execute this statement only the "version should equals to 2" test case will PASS 
				User.list().size()
				def user1 = User.get(users[2].id)
        assertEquals "testBatchUpdate - user name updated", users[2].name, user1.name
        assertEquals "testBatchUpdate - version should be equals to 2", new Long(2), user1.version
    }

    void testBatchUpdateShouldThrowException() {
			 def users = [new User(email:"anonymous@anonymous.com", name:"Anonymous"),
			 new User(email:"admin@vobject.com", password:"admin", name:"Administrator"),
			 new User(email:"user1@vobject.com", password:"user1", name:"User 1")]
			 users.each { user ->
			   user.version = 2   
			 }
			users.batchSave()
			users.clear()
			users = User.list()
			// render("testBatchUpdateShouldThrowException. Get users after first insert: <br />")
			// renderUsers(users)
			users[2].name = "Modified User 1"
		  users[2].version = users[2].version - 1
			try {
			    users.batchSave()
			    throw new RuntimeException("This should throw an OptimisticLockingFailureException")
			} catch (Exception e) {
			    println "EXCEPTION: ${e.getMessage()}"
			    String expectedValue = "Another user has updated"
			    assertEquals "testBatchUpdateShouldThrowException", expectedValue, e.getMessage().substring(0, expectedValue.length())
			}
    }

    void testBatchInsertWithTransactionShouldThrowException() {
        def role = new Role(name:"Test User")
				try {
				    Role.withTransaction {
				        if (role.save(flush: true)) {
				            println "testBatchInsertWithTransactionCommit. role.id = ${role.id}"
				          [new Permission(roleId:role.id, controller:"user", action:"show"),
				           new Permission(roleId:role.id, controller:"user", action:"list"),
				           new Permission(roleId:role.id, controller:"user", action:"index"),
				           new Permission(roleId:role.id, controller:"role", action:"show"),
				           new Permission(roleId:role.id, controller:"role", action:"list"),
				           new Permission(roleId:role.id, controller:"role", action:"index")].batchSave()
				        }
				    }
				    throw new RuntimeException("This should throw an IllegalArgumentException")
				} catch (Exception e) {
				    println "EXCEPTION: ${e.getMessage()}"
				    String expectedValue = "can't operate on multiple entity groups in a single transaction."
				    assertEquals "testBatchInsertWithTransactionShouldThrowException", expectedValue, e.getMessage().substring(0, expectedValue.length())
				}
    }

    private void printUsers(List users) {
        users.each { bean ->
	    		println "${bean.id?KeyFactory.stringToKey(bean.id):"NULL"}, ${bean}" 
        	}
    }		
	
}
