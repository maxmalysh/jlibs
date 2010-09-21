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

package jlibs.nblr.rules;

import jlibs.nblr.matchers.Matcher;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Santhosh Kumar T
 */
public class Path extends ArrayList<Object>{
    public Paths paths;
    
    public Path(ArrayDeque<Object> stack){
        super(stack);
        Collections.reverse(this);
    }

    public List<Node> nodes(){
        ArrayList<Node> nodes = new ArrayList<Node>();
        for(Object obj: this){
            if(obj instanceof Node)
                nodes.add((Node)obj);
        }
        return nodes;
    }

    public Matcher matcher(){
        if(size()>1)
            return ((Edge)get(size()-2)).matcher;
        else
            return null;
    }

    public int depth(){
        int depth = 0;
        if(paths!=null){
            for(Path path: paths)
                depth = Math.max(depth, path.depth());
        }
        return depth+1;
    }

    @Override
    public String toString(){
        Matcher matcher = matcher();
        if(matcher==null)
            return "<EOF>";
        else if(matcher.name!=null)
            return '<'+matcher.name+'>';
        else
            return matcher.toString();
    }

    void toString(ArrayDeque<Path> pathStack, StringBuilder buff){
        pathStack.push(this);
        if(paths==null){
            List<Path> list = new ArrayList<Path>(pathStack);
            Collections.reverse(list);
            if(buff.length()>0)
                buff.append(" OR ");
            for(Path path: list)
                buff.append(path);
        }else
            paths.toString(pathStack, buff);
        pathStack.pop();
    }
}