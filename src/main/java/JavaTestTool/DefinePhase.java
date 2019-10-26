package JavaTestTool;

import JavaTestTool.AntlrParser.JavaParser;
import JavaTestTool.AntlrParser.JavaParserBaseListener;
import JavaTestTool.Scope.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinePhase extends JavaParserBaseListener {
    ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();
    GlobalScope globals;
    Scope currentScope;
    Map<String, List<FunctionSymbol>> classMap = new HashMap<>();
    Map<String, String> classInstance = new HashMap<>();

    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        globals = new GlobalScope(null);
        currentScope = globals;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String className = ctx.IDENTIFIER().getText();
        classMap.put(className, new ArrayList<>());
        ClassSymbol classSymbol = new ClassSymbol(className, currentScope);
        currentScope.define(classSymbol);
        saveScope(ctx, classSymbol);
        currentScope = classSymbol;
    }
    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }



    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        String className = currentScope.getScopeName();
        String methodName = ctx.IDENTIFIER().getText();
        Symbol.Type retType = ctx.typeTypeOrVoid().getText().equals("String") ? Symbol.Type.tString : Symbol.Type.tNotString;
        FunctionSymbol function = new FunctionSymbol(methodName, retType, currentScope);
        classMap.get(className).add(function);
        currentScope.define(function);
        saveScope(ctx, function);
        currentScope = function;
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterBlock(JavaParser.BlockContext ctx) {
        // enter local scope, like for loop or just {}
        if(!(ctx.getParent() instanceof JavaParser.MethodBodyContext)){
            currentScope = new LocalScope(currentScope);
            saveScope(ctx, currentScope);
        }
    }

    @Override
    public void exitBlock(JavaParser.BlockContext ctx) {
        // enter local scope, like for loop or just {}
        if(!(ctx.getParent() instanceof JavaParser.MethodBodyContext)){
            currentScope = currentScope.getEnclosingScope();
        }
    }

    @Override
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        // check primitive
        if(ctx.typeType().classOrInterfaceType() != null || ctx.typeType().primitiveType() != null){
            Symbol.Type type = Symbol.Type.tVoid;
            String typeName = ctx.typeType().classOrInterfaceType() == null ? ctx.typeType().primitiveType().getText() : ctx.typeType().classOrInterfaceType().getText();
            switch (typeName){
                case "String":
                    type = Symbol.Type.tString;
                    break;
                default:
                    // class instance
                    if(classMap.containsKey(typeName)){
                        for(int i = 0; i < ctx.variableDeclarators().variableDeclarator().size(); i ++){
                            classInstance.put(ctx.variableDeclarators().variableDeclarator().get(i).getText(), typeName);
                        }
                    }
                    type = Symbol.Type.tNotString;
                    break;
            }
            if(typeName.matches("String\\[.*\\]")){
                type = Symbol.Type.tStringArray;
            }
            for(int i = 0; i < ctx.variableDeclarators().variableDeclarator().size(); i ++){
                VariableSymbol var = new VariableSymbol(ctx.variableDeclarators().variableDeclarator(i).variableDeclaratorId().getText(), type);
                currentScope.define(var);
            }
        }
    }

    void saveScope(ParserRuleContext ctx, Scope s) { scopes.put(ctx, s); }

}
