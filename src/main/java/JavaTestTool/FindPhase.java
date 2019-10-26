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

public class FindPhase extends JavaParserBaseListener {
    ParseTreeProperty<Scope> scopes;
    GlobalScope globals;
    Scope currentScope;
    Map<String, List<FunctionSymbol>> classMap;
    ParseTreeProperty<Symbol.Type> expressionMap;
    Map<String, String> classInstance;

    public FindPhase(GlobalScope globals, ParseTreeProperty<Scope> scopes, Map<String, List<FunctionSymbol>> classMap, Map<String, String> classInstance){
        this.scopes = scopes;
        this.globals = globals;
        this.classMap = classMap;
        this.classInstance = classInstance;
        expressionMap = new ParseTreeProperty<>();
    }
    @Override
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        currentScope = globals;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        currentScope = scopes.get(ctx);
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterBlock(JavaParser.BlockContext ctx) {
        // enter local scope, like for loop or just {}
        if(!(ctx.getParent() instanceof JavaParser.MethodBodyContext)){
            currentScope = scopes.get(ctx);
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
    public void enterSingleExpression(JavaParser.SingleExpressionContext ctx) {
        //literal
        if(ctx.primary().literal() != null){
            if(ctx.primary().literal().STRING_LITERAL() != null){
                expressionMap.put(ctx, Symbol.Type.tString);
            }
        }else if(ctx.primary().IDENTIFIER() != null){
            //check identifier
            String identifier = ctx.primary().IDENTIFIER().getText();
            if(currentScope.resolve(identifier) != null){
                saveExpression(ctx, currentScope.resolve(identifier).getType());

            }
        }

    }


    @Override
    public void exitCallExpression(JavaParser.CallExpressionContext ctx) {
        String name = ctx.expression().getText();
        if(classInstance.get(name) != null || classMap.containsKey(name)){
            // get class name
            if(classInstance.get(name) != null)name = classInstance.get(name);
            String methodName = ctx.methodCall().getChild(0).getText();
            for(FunctionSymbol method: classMap.get(name)){
                if(method.getName().equals(methodName)){
                    expressionMap.put(ctx, method.getType());
                }
            }
        }
    }

    public boolean checkExpression(JavaParser.ExpressionContext ctx){
        if(expressionMap.get(ctx) == Symbol.Type.tString)return true;
        return false;
    }

    void saveExpression(ParserRuleContext ctx, Symbol.Type type){
        expressionMap.put(ctx, type);
    }

    boolean findStringMethod(String className){
        List<FunctionSymbol> methodList = classMap.get(className);
        for(FunctionSymbol method: methodList){
            if(method.getType() == Symbol.Type.tString)return true;
        }
        return false;
    }
}
