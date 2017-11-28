package com.randioo.compare_collections_server.util.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSInvoker {
    private static JSInvoker jsInvoker = null;
    private ScriptEngine engine;
    private File file;

    private JSInvoker() {

    }

    public synchronized static JSInvoker getInstance() {
        if (jsInvoker == null) {
            jsInvoker = new JSInvoker();
            jsInvoker.init("./function.js");
        }
        return jsInvoker;
    }

    private void init(String filename) {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");

        file = new File(filename);
        this.refreshScript();
    }

    public void refreshScript() {
        try {
            FileReader reader = new FileReader(file);
            engine.eval(reader);
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object invoke(String functionName, Object... param) {
        try {
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                Object obj = invoke.invokeFunction(functionName, param);
                return obj;
            }

        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        JSInvoker invoker = JSInvoker.getInstance();
        List<CardConfig> list1 = new ArrayList<>();
        List<CardConfig> list2 = new ArrayList<>();
        
        list1.add(c(1,1,1));
        list1.add(c(4,2,4));
        list1.add(c(2,1,2));
        list1.add(c(3,1,3));
        list1.add(c(5,2,5));
        list2.add(c(1,2,6));
        list2.add(c(4,1,9));
        list2.add(c(2,3,7));
        list2.add(c(3,4,8));
        list2.add(c(5,1,11));
        Object value= invoker.invoke("compare",1,1,list1,list2);
        System.out.println(value);
      
        

    }
    private static CardConfig c(int id,int hua,int value){
    	CardConfig c1 = new CardConfig();
    	c1.id = id;
        c1.hua = hua;
        c1.value = value;
        return c1;
    }
}
