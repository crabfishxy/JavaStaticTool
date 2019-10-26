package JavaTestTool;

import JavaTestTool.AntlrParser.JavaParser;
import JavaTestTool.AntlrParser.JavaParserBaseListener;
import JavaTestTool.Scope.Symbol;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class CheckPhase extends JavaParserBaseListener {
    ParseTreeProperty<Symbol.Type> expressionMap;

    public CheckPhase(ParseTreeProperty<Symbol.Type> expressionMap){
        this.expressionMap = expressionMap;
    }

    @Override
    public void enterEqualExpression(JavaParser.EqualExpressionContext ctx) {
        if(expressionMap.get(ctx.expression(0)) == Symbol.Type.tString || expressionMap.get(ctx.expression(1)) == Symbol.Type.tString){
            System.out.println("Line " + ctx.start.getLine() + ":" + ctx.getText());
        }
    }
}
