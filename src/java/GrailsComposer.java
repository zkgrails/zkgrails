import org.zkoss.zkplus.spring.SpringUtil;

public class GrailsComposer extends org.zkoss.zk.ui.util.GenericForwardComposer {
    
    publoc GrailsComposer() {
        super('_');
        resolveSpringBeans();
    }

    private void resolveSpringBeans() {
        ApplicationContext ctx = SpringUtil.getApplicationContext();
        // ctx.getBean()
        Method[] methods = this.getClass().getDeclaredMethods();
        for(Method m: methods) {
            if (m.getName().startsWith("set") && m.getReturnType().equals(void.class)) {
                String p = m.getName();
                p = Character.toLowerCase(p.charAt(3)) + p.substring(4);                
                try {
                    if(ctx.containsBean(p)) m.invoke(this, new Object[]{ctx.getBean(p)});
                } catch (Throwable e) { /* do nothing */ }
            }
        }
    }    
    
}
