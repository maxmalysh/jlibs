/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.swing;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Santhosh Kumar T
 */
public class SwingUtil{
    public static void setInitialFocus(Window window, final Component comp){
        window.addWindowFocusListener(new WindowAdapter(){
            @Override
            public void windowGainedFocus(WindowEvent we){
                comp.requestFocusInWindow();
                we.getWindow().removeWindowFocusListener(this);
            }
        });
    }

    @SuppressWarnings({"UnusedAssignment"})
    public static void doAction(JTextField textField){
        String command = null;
        if(textField.getAction()!=null)
            command = (String)textField.getAction().getValue(Action.ACTION_COMMAND_KEY);
        ActionEvent event = null;
        
        for(ActionListener listener: textField.getActionListeners()){
            if(event==null)
                event = new ActionEvent(textField, ActionEvent.ACTION_PERFORMED, command, System.currentTimeMillis(), 0);
            listener.actionPerformed(event);
        }
    }

    public static void setText(JTextComponent textComp, String text){
        if(text==null)
            text = "";
        
        if(textComp.getCaret() instanceof DefaultCaret){
            DefaultCaret caret = (DefaultCaret)textComp.getCaret();
            int updatePolicy = caret.getUpdatePolicy();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            try{
                textComp.setText(text);
            }finally{
                caret.setUpdatePolicy(updatePolicy);
            }
        }else{
            int mark = textComp.getCaret().getMark();
            int dot = textComp.getCaretPosition();
            try{
                textComp.setText(text);
            }finally{
                int len = textComp.getDocument().getLength();
                if(mark>len)
                    mark = len;
                if(dot>len)
                    dot = len;
                textComp.setCaretPosition(mark);
                if(dot!=mark)
                    textComp.moveCaretPosition(dot);
            }
        }
    }
}
