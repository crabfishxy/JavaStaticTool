package JavaTestTool.Scope;

public class Symbol {
    public static enum Type {tINVALID, tString, tNotString, tStringArray, tCollection, tINT, tFLOAT, tDouble, tVoid}

    String name;
    Type type;
    Scope scope;

    public Symbol(String name) { this.name = name; }
    public Symbol(String name, Type type) { this(name); this.type = type; }
    public String getName() { return name; }
    public Type getType() {return type; }

    public String toString() {
        if ( type!=Type.tINVALID ) return '<'+getName()+":"+type+'>';
        return getName();
    }
}
