package com.look.tsharding.auto.cache;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ObjectUtils;
import com.look.tsharding.utils.MapperUtils;
import com.mogujie.tsharding.filter.InvocationProxy;

public class ProxyCacheMethodInvocation implements ProxyMethodInvocation, Cloneable {
	protected final InvocationProxy invocationProxy;

	protected final Method method;

	protected Object[] arguments;

	private final Class<?> targetClass;

	/**
	 * Lazily initialized map of user-specific attributes for this invocation.
	 */
	private Map<String, Object> userAttributes;
	private CacheMapperHandlerInterceptor interceptor;

	protected ProxyCacheMethodInvocation(InvocationProxy invocationProxy, Method method, Object[] arguments,
			Class<?> targetClass, CacheMapperHandlerInterceptor interceptor) {

		this.invocationProxy = invocationProxy;
		this.targetClass = targetClass;
		this.method = BridgeMethodResolver.findBridgedMethod(method);
		this.arguments = adaptArgumentsIfNecessary(method, arguments);
		this.interceptor = interceptor;
	}

	/**
	 * Adapt the given arguments to the target signature in the given method, if
	 * necessary: in particular, if a given vararg argument array does not match
	 * the array type of the declared vararg parameter in the method.
	 * 
	 * @param method the target method
	 * @param arguments the given arguments
	 * @return a cloned argument array, or the original if no adaptation is needed
	 * @since 4.2.3
	 */
	static Object[] adaptArgumentsIfNecessary(Method method, Object... arguments) {
		if (method.isVarArgs() && !ObjectUtils.isEmpty(arguments)) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length == arguments.length) {
				int varargIndex = paramTypes.length - 1;
				Class<?> varargType = paramTypes[varargIndex];
				if (varargType.isArray()) {
					Object varargArray = arguments[varargIndex];
					if (varargArray instanceof Object[] && !varargType.isInstance(varargArray)) {
						Object[] newArguments = new Object[arguments.length];
						System.arraycopy(arguments, 0, newArguments, 0, varargIndex);
						Class<?> targetElementType = varargType.getComponentType();
						int varargLength = Array.getLength(varargArray);
						Object newVarargArray = Array.newInstance(targetElementType, varargLength);
						System.arraycopy(varargArray, 0, newVarargArray, 0, varargLength);
						newArguments[varargIndex] = newVarargArray;
						return newArguments;
					}
				}
			}
		}
		return arguments;
	}

	@Override
	public final Object getProxy() {
		return null;
	}

	@Override
	public final Object getThis() {
		return this.invocationProxy;
	}

	@Override
	public final AccessibleObject getStaticPart() {
		return this.method;
	}

	/**
	 * Return the method invoked on the proxied interface. May or may not
	 * correspond with a method invoked on an underlying implementation of that
	 * interface.
	 */
	@Override
	public final Method getMethod() {
		return this.method;
	}

	@Override
	public final Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}

	@Override
	public void setArguments(Object... arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object proceed() throws Throwable {
		return invokeJoinpoint();
	}

	protected Object invokeJoinpoint() throws Throwable {
		MapperHander mapperHander = MapperUtils.getMapperHander(invocationProxy);
		return interceptor.doInvoker(invocationProxy, mapperHander);
	}

	/**
	 * This implementation returns a shallow copy of this invocation object,
	 * including an independent copy of the original arguments array.
	 * <p>
	 * We want a shallow copy in this case: We want to use the same interceptor
	 * chain and other object references, but we want an independent value for the
	 * current interceptor index.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MethodInvocation invocableClone() {
		Object[] cloneArguments = null;
		if (this.arguments != null) {
			// Build an independent copy of the arguments array.
			cloneArguments = new Object[this.arguments.length];
			System.arraycopy(this.arguments, 0, cloneArguments, 0, this.arguments.length);
		}
		return invocableClone(cloneArguments);
	}

	/**
	 * This implementation returns a shallow copy of this invocation object, using
	 * the given arguments array for the clone.
	 * <p>
	 * We want a shallow copy in this case: We want to use the same interceptor
	 * chain and other object references, but we want an independent value for the
	 * current interceptor index.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MethodInvocation invocableClone(Object... arguments) {
		// Force initialization of the user attributes Map,
		// for having a shared Map reference in the clone.
		if (this.userAttributes == null) {
			this.userAttributes = new HashMap<String, Object>();
		}

		// Create the MethodInvocation clone.
		try {
			ProxyCacheMethodInvocation clone = (ProxyCacheMethodInvocation) clone();
			clone.arguments = arguments;
			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new IllegalStateException("Should be able to clone object of type [" + getClass() + "]: " + ex);
		}
	}

	@Override
	public void setUserAttribute(String key, Object value) {
		if (value != null) {
			if (this.userAttributes == null) {
				this.userAttributes = new HashMap<String, Object>();
			}
			this.userAttributes.put(key, value);
		} else {
			if (this.userAttributes != null) {
				this.userAttributes.remove(key);
			}
		}
	}

	@Override
	public Object getUserAttribute(String key) {
		return (this.userAttributes != null ? this.userAttributes.get(key) : null);
	}

	/**
	 * Return user attributes associated with this invocation. This method
	 * provides an invocation-bound alternative to a ThreadLocal.
	 * <p>
	 * This map is initialized lazily and is not used in the AOP framework itself.
	 * 
	 * @return any user attributes associated with this invocation (never
	 *         {@code null})
	 */
	public Map<String, Object> getUserAttributes() {
		if (this.userAttributes == null) {
			this.userAttributes = new HashMap<String, Object>();
		}
		return this.userAttributes;
	}

	@Override
	public String toString() {
		// Don't do toString on target, it may be proxied.
		StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
		sb.append(this.method).append("; ");
		if (this.invocationProxy == null) {
			sb.append("target is null");
		} else {
			sb.append("target is of class [").append(this.targetClass.getName()).append(']');
		}
		return sb.toString();
	}

}
