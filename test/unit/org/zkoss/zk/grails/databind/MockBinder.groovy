package org.zkoss.zk.grails.databind

class MockBinder {

    def vm

    def propertyMissing(String name) {
        return vm."$name"
    }

    def propertyMissing(String name, value) {
        vm."$name" = value
    }

}
