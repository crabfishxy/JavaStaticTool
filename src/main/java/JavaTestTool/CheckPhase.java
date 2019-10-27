package JavaTestTool;

import JavaTestTool.AntlrParser.JavaParser;
import JavaTestTool.AntlrParser.JavaParserBaseListener;
import JavaTestTool.Scope.FunctionSymbol;
import JavaTestTool.Scope.Symbol;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckPhase extends JavaParserBaseListener {
    ParseTreeProperty<Symbol.Type> expressionMap;
    Map<String, List<FunctionSymbol>> classMap = new HashMap<>();


    public CheckPhase(ParseTreeProperty<Symbol.Type> expressionMap, Map<String, List<FunctionSymbol>> classMap){
        this.expressionMap = expressionMap;
        this.classMap = classMap;
    }

    @Override
    public void enterEqualExpression(JavaParser.EqualExpressionContext ctx) {
        if(expressionMap.get(ctx.expression(0)) == Symbol.Type.tString || expressionMap.get(ctx.expression(1)) == Symbol.Type.tString){
            System.out.println("Line " + ctx.start.getLine() + ": " + ctx.getText() + " Avoid using \"==\" or \"!=\" to compare strings");
        }
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String className = ctx.IDENTIFIER().getText();
        List<FunctionSymbol> methodList = classMap.get(className);
        boolean equalsMethod = false, hashMethod = false;
        for(FunctionSymbol method : methodList){
            if(method.getName().equals("equals")){
                equalsMethod = true;
            }
            if(method.getName().equals("hashCode")){
                hashMethod = true;
            }
        }
        if(equalsMethod && ! hashMethod){
            System.out.println("Line " + ctx.start.getLine() + ": " + "class " + className + " Class defines equals() but not hashCode()");
        }
    }

    @Override
    public void exitStatement(JavaParser.StatementContext ctx) {
        // if statement
        if(ctx.getChild(0).getText().equals("if")){
            // only "{}"
            if(ctx.getChild(2).getChild(0).getChildCount() == 2){
                System.out.println("Line " + ctx.start.getLine() + ": " + " Avoid empty if statement.");
            }
        }
    }
}
