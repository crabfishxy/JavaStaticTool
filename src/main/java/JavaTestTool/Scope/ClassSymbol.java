package JavaTestTool.Scope;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassSymbol extends Symbol implements Scope{
    Map<String, Symbol> arguments = new LinkedHashMap<String, Symbol>();
    Scope enclosingScope;

    public ClassSymbol(String name, Scope enclosingScope) {
        super(name);
        this.enclosingScope = enclosingScope;
    }

    public Symbol resolve(String name) {
        Symbol s = arguments.get(name);
        if ( s!=null ) return s;
        return null; // not found
    }

    public void define(Symbol sym) {
        arguments.put(sym.name, sym);
        sym.scope = this; // track the scope in each symbol
    }

    public Scope getEnclosingScope() { return enclosingScope; }
    public String getScopeName() { return name; }

    public String toString() { return "class"+super.toString()+":"+arguments.values(); }
}
