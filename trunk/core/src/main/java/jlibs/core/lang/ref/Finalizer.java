/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.core.lang.ref;

import jlibs.core.lang.ImpossibleException;

import java.lang.ref.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Santhosh Kumar T
 */
public class Finalizer extends ReferenceQueue implements Runnable{
    public static final Finalizer INSTANCE = new Finalizer();

    private Finalizer(){
        Thread thread = new Thread(this, "JLibsFinalizer");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    @SuppressWarnings({"InfiniteLoopStatement"})
    public void run(){
        while(true){
            try{
                Reference ref = super.remove();
                Runnable runnable = tracked.remove(ref);
                try{
                    if(runnable!=null)
                        runnable.run();
                }catch(ThreadDeath td){
                    throw td;
                }catch(Throwable thr){
                    thr.printStackTrace();
                }
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    private Map<Reference, Runnable> tracked = new HashMap<Reference, Runnable>();

    @SuppressWarnings({"unchecked"})
    public <T> WeakReference<T> track(T obj, Runnable runnable){
        return (WeakReference<T>)track(obj, WeakReference.class, runnable);
    }

    @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
    public <T, R extends Reference<T>> R track(T obj, Class<R> type, Runnable runnable){
        Class clazz = type;
        Reference ref;
        if(clazz==WeakReference.class)
            ref = new WeakReference(obj, this);
        else if(clazz==SoftReference.class)
            ref = new SoftReference(obj, this);
        else if(clazz==PhantomReference.class)
            ref = new PhantomReference(obj, this);
        else
            throw new ImpossibleException();

        tracked.put(ref, runnable);
        return (R)ref;
    }

    public void trackGC(Object obj){
        trackGC(obj, null);
    }

    public void trackGC(Object obj, String message){
        if(message==null)
            message = obj.getClass().getName()+'@'+System.identityHashCode(obj);
        track(obj, new MessagePrinter(message));
    }

    private static class MessagePrinter implements Runnable{
        private String message;

        private MessagePrinter(String message){
            this.message = message;
        }

        @Override
        public void run(){
            System.out.println("JLibsFinalizer: '"+message+"' got garbage collected.");
        }
    }
}
