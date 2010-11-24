package zk;

class TestService {

    static transactional = false

    String hello() {
        return "hello zk-grails"
    }
    String getHello() {
        return "hello zk-grails"
    }
    Boolean getShow() {
        return false
    }
    def propertyMissing(String name) { 
        return name
    }
}