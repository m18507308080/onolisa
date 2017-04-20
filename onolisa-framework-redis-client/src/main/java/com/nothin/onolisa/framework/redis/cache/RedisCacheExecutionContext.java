/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: RedisCacheExecutionContext.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.cache;

/**
 * 
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
class RedisCacheExecutionContext {
	
	private static final ThreadLocal<RedisCacheExecutionContext> executionContext =
	         new ThreadLocal<RedisCacheExecutionContext>() {
	             @Override protected RedisCacheExecutionContext initialValue() {
	                 return new RedisCacheExecutionContext();
	             }
	         };
	
	private static RedisCacheExecutionContext getExecutionContext() {
		return executionContext.get();
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 *
	 * @param func
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static <TResult> TResult execute(RedisCacheExecutionFunc<TResult> func) {
    	RedisCacheExecutionContext context = getExecutionContext();
    	boolean isOutmost = context.enter();
    	boolean forceCleanData = !isOutmost;
    	try {
    		return func.invoke(forceCleanData);
    	}
    	finally {
    		if (isOutmost) {
    			context.exit();
    		}
    	}
	}
	
	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 *
	 * @param action
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static void execute(RedisCacheExecutionAction action) {
    	RedisCacheExecutionContext context = getExecutionContext();
    	boolean isOutmost = context.enter();
    	boolean forceCleanData = !isOutmost;
    	try {
    		action.invoke(forceCleanData);
    	}
    	finally {
    		if (isOutmost) {
    			context.exit();
    		}
    	}		
	}
	
	private boolean isExecuting = false;
	
	private boolean enter() {
		if (isExecuting) {
			return false;
		}
		
		return isExecuting = true;
	}
	
	private void exit() {
		isExecuting = false;
	}
}
