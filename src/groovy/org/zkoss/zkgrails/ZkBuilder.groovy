package org.zkoss.zkgrails

import java.util.concurrent.ConcurrentHashMap
import org.codehaus.groovy.runtime.InvokerHelper
import org.zkoss.zk.ui.event.EventListener

class ZkBuilder {

    private static ZKNODES = new ConcurrentHashMap();

    def parent
    def idComponents = [:]

    boolean resolveTag(String pack, String tag) {
        try {
            def name = tag[0].toUpperCase() + tag[1..-1];
            def c = "${pack}.${name}" as Class
            ZKNODES[tag] = c;
            return true;
        } catch(e)  {
            return false;
        }
    }

    boolean getTag(String tag) {
        if(ZKNODES.containsKey(tag)) return true

        if(tag.endsWith("Event")) {
            if(resolveTag("org.zkoss.zk.ui.event", tag)) return true
            if(resolveTag("org.zkoss.zul.event", tag)) return true
            if(resolveTag("org.zkoss.zkmax.event", tag)) return true
        } else {
            if(resolveTag("org.zkoss.zul", tag)) return true
            if(resolveTag("org.zkoss.zhtml", tag)) return true
            if(resolveTag("org.zkoss.zkex.zul", tag)) return true
            if(resolveTag("org.zkoss.zkmax.zul", tag)) return true
        }

        return false
    }

  def methodMissing(String name, args) {
    if (getTag(name)) {
      def zkObject = null
      Closure closure = null
      switch (args.length) {
        case 0:
          zkObject = newInstance(name)
          break;
        case 1:
          def arg = args[0]
          if (arg instanceof Closure) {
            closure = arg
            zkObject = newInstance(name)
          } else if (arg instanceof String) {
            zkObject = newInstance(name, [:], arg)
          } else if (arg instanceof Map) {
            zkObject = newInstance(name, arg)
          } else {
            throw new MissingMethodException(name, getClass(), args, false);
          }
          break;
        case 2:
          def arg = args[0]
          def arg2 = args[1]
          if (arg2 instanceof String && arg instanceof Map) {
            zkObject = newInstance(name, arg, arg2)
          } else if (arg instanceof String && arg2 instanceof Closure) {
            zkObject = newInstance(name, [:], arg)
            closure = arg2
          } else if (arg instanceof Map && arg2 instanceof Closure) {
            zkObject = newInstance(name, arg)
            closure = arg2
          } else {
            throw new MissingMethodException(name, getClass(), args, false);
          }
          break;
        case 3:
          zkObject = newInstance(name, args[0], args[1])
          closure = args[2]
          break;
        default:
          throw new MissingMethodException(name, getClass(), args, false);
      }

      if (closure) {
        def oldParent = parent
        parent = zkObject
        closure.delegate = this
        closure(zkObject)
        parent = oldParent
      }
      return zkObject
    }
    else if (!createEventListener(parent, name, args)) {
      throw new MissingMethodException(name, getClass(), args, false)
    }
  }

  private def createEventListener(zkObject, String name, args) {
    def listener = InvokerHelper.asList(args)[0]
    if(listener instanceof Closure) {
      Closure cls = listener
      ZkBuilder zkBuilder = new ZkBuilder()
      zkBuilder.idComponents = idComponents
      cls.delegate = zkBuilder
    }
    if (name =~ /on[A-Z]\w+/) {
      zkObject.addEventListener(name, listener as EventListener)
      return true
    }
    return false
  }

  def propertyMissing(String name) {
    if (idComponents.containsKey(name)) {
      return idComponents[name]
    }
    throw new MissingPropertyException(name, getClass())
  }

  Object newInstance(String name, def args = [:], String id = null) {
    def zkObject = ZKNODES[name].newInstance()

    attachId(id, zkObject)

    addAttributeEvents(zkObject, args)

    args.each {key, value ->
      zkObject[key] = value
    }
    if (parent) {
      zkObject.parent = parent
    }
    return zkObject
  }

  private def attachId(String id, zkObject) {
    if (id) {
      Class clazz = ZKNODES[id]
      if (clazz) {
        throw new IllegalArgumentException("Cannot use $id as an ID.  It clashes with $clazz")
      }
      zkObject.id = id
      idComponents[id] = zkObject
    }
  }

  void addAttributeEvents(zkObject, java.util.Map args) {
    args.each {key, value ->
      if (createEventListener(zkObject, key, value)) {
        args.remove(key)
      }
    }
  }
}
