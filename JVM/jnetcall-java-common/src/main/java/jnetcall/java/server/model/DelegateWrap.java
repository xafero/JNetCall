package jnetcall.java.server.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jnetbase.java.meta.Reflect;
import jnetcall.java.server.api.IHosting;

public final class DelegateWrap implements InvocationHandler {

    private final IHosting _host;
    private final short _id;
    private final Class<?> _delType;

    public DelegateWrap(IHosting host, short id, Class<?> delType) {
        _host = host;
        _id = id;
        _delType = delType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Class<?> owner = method.getDeclaringClass();
        if (owner.equals(_delType)) {
            Class<?> retType = method.getReturnType();
            Object res = _host.goDynInvoke(retType, _id, args);
            return res;
        }
        if (owner.equals(Object.class)) {
            String name = method.getName();
            if (name.equals("toString"))
                return getId();
            if (name.equals("equals"))
                return isEqual(args[0]);
        }
        throw new RuntimeException(_id + ", " + method + ", " + args);
    }

    private String getId() {
        return "Delegate<" + _delType.getName() + ">#" + _id;
    }

    private boolean isEqual(Object obj) {
    	InvocationHandler handler = Reflect.getProxyHandler(obj);
        if (handler instanceof DelegateWrap) {
        	DelegateWrap other = (DelegateWrap)handler;
            if (getId().equals(other.getId()))
                return true;
        }
        return false;
    }
}
