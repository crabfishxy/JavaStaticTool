package JavaTestTool;

import JavaTestTool.AntlrParser.JavaLexer;
import JavaTestTool.AntlrParser.JavaParser;
import com.sun.tools.javac.comp.Check;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class CheckTool {

    public void process(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.compilationUnit();
        // show tree in text form
//        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        DefinePhase def = new DefinePhase();
        walker.walk(def, tree);
        // create next phase and feed symbol table info from def to ref phase
        FindPhase find = new FindPhase(def.globals, def.scopes, def.classMap);
        walker = new ParseTreeWalker();
        walker.walk(find, tree);
        walker = new ParseTreeWalker();
        CheckPhase check = new CheckPhase(find.expressionMap);
        walker.walk(check, tree);
    }

    public static void main(String[] args) throws Exception {
        new CheckTool().process(args);
    }
}
