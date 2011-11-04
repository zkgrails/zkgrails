package org.zkoss.zk.grails.databind

class DataBinderTests extends GroovyTestCase {

    def testBindBean() {
        def binder = new DataBinder()
        def obj = [test:"test"]
        binder.bindBean("obj", obj)
        assert binder.getBean("obj") == [test:"test"]
    }

    def testAddBinding() {
        def binder = new DataBinder()
        def comp = new MockComponent()
        def c = new MockTypeConverter()
        binder.addBinding(comp, "value", "user.name", c)
        def a = binder.exprSubscribeMap['user.name'] as List
        def b = binder.compSubscribeMap[comp] as List
        assert a.size() == 1
        assert a[0] == b[0]
        assert a[0] == new Tuple(comp: comp, attr:"value", expr: "user.name", converter: c)
        assert binder.containsComponent(comp)
    }

    def testEval() {
        def gcl = new GroovyClassLoader()
        def user = gcl.parseClass('''
            class User {
                String name
            }
        ''').newInstance()
        def binder = new DataBinder()
        user.name = "test"
        binder.bindBean("user", user)
        assert binder.eval("user.name") == "test"
    }

    def testSet() {
        def gcl = new GroovyClassLoader()
        def user = gcl.parseClass('''
            class User {
                String name
            }
        ''').newInstance()
        user.name = "test"

        def binder = new DataBinder()

        binder.bindBean("user", user)
        binder.set("user.name", "new test")
        assert user.name == "new test"
    }

    def testFireViewChanged() {
        def binder = new DataBinder()
        def comp = new MockComponent()
        def c = new MockTypeConverter(value:"expected value")
        binder.addBinding(comp, "value", "user.name", c)
        def a = binder.exprSubscribeMap['user.name'] as List
        def b = binder.compSubscribeMap[comp] as List
        assert a.size() == 1
        assert a[0] == b[0]
        assert a[0] == new Tuple(comp: comp, attr:"value", expr: "user.name", converter: c)

        def gcl = new GroovyClassLoader()
        def user = gcl.parseClass('''
            class User {
                String name
            }
        ''').newInstance()
        user.name = "test"

        binder.bindBean("user", user)
        binder.fireViewChanged(comp, "onChange")
        assert user.name == "expected value"
    }

}
