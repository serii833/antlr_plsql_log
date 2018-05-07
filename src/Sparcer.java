import com.sun.scenario.effect.impl.prism.ps.PPSBlend_ADDPeer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


class MyListner extends PlSqlParserBaseListener
{
    private CharStream input;

    MyListner(CharStream input) {
        this.input = input;
    }

    @Override
    public void visitTerminal(TerminalNode node) {
//        int a = node.getSymbol().getStartIndex();
//        int b = node.getSymbol().getStopIndex();
//
//        Interval interval = new Interval(a,b);
//        System.out.println(input.getText(interval));

//        System.out.println(node.getText());
    }


    @Override
    public void enterCreate_package_body(PlSqlParser.Create_package_bodyContext ctx) {
        super.enterCreate_package_body(ctx);
//        System.out.println(ctx.schema_object_name().getText());
    }


    @Override
    public void enterProcedure_body(PlSqlParser.Procedure_bodyContext ctx) {
        super.enterProcedure_body(ctx);

        //System.out.println(ctx.getText());


//        int a = ctx.start.getStartIndex();
//        int b = ctx.stop.getStopIndex();
//        Interval interval = new Interval(a,b);
//
//        System.out.println(input.getText(interval));


//        int a = ctx.start.getStartIndex();
//        int b = ctx.stop.getStopIndex();
//        Interval interval = new Interval(a,b);
//        input.getText(interval);


//        System.out.println("enter procedure body");
//        System.out.println();
//
//        System.out.println("name : " + ctx.identifier().getText());
//
//        List<PlSqlParser.ParameterContext> params = ctx.parameter();
//
//        System.out.println("params: ");
//        for(PlSqlParser.ParameterContext param : params) {
//            System.out.println("  " + param.parameter_name().getText() + " : " + param.type_spec().getText());
//        }
//
//        if(ctx.body().exception_handler().size() > 0) {
//            System.out.println("exception_handler: " + ctx.body().exception_handler().size());
//            System.out.println("EXCEPTION token line #: " + ctx.body().EXCEPTION().getSymbol().getLine() );
//            System.out.println("start = " + ctx.body().exception_handler(0).getStart() );
//            if(ctx.body().exception_handler().size() > 1)
//                System.out.println("start = " + ctx.body().exception_handler(1).getStart() );
//        }
//
//        System.out.println(ctx.body().getStart());
//        System.out.println(ctx.body().getStop());
//



//        System.out.println(ctx.parameter(0).parameter_name().getText());
//        System.out.println(ctx.parameter(0).parameter_name().identifier().id_expression().regular_id().getStart());
//        System.out.println(ctx.parameter(0).parameter_name().identifier().id_expression().regular_id().getStop());
//
//        System.out.println(ctx.parameter(0).parameter_name().getStart());
//        System.out.println(ctx.parameter(0).parameter_name().getStop());
//
//
//        System.out.println(ctx.body().seq_of_statements().getStop());
//
//        System.out.println("--- exceprion ---");
//        System.out.println(ctx.body().exception_handler(0).getStart());
//        System.out.println(ctx.body().EXCEPTION().getSymbol().getStartIndex());
    }
}


class EmptyListner extends PlSqlParserBaseListener{};







public class Sparcer {


    public static void tryToReadAllPackages(String pathToPackages) throws IOException {
        Stream<Path> files = Files.list(Paths.get(pathToPackages)).filter(path-> Files.isRegularFile(path) && path.toString().endsWith(".pkb"));

        for(Path f:files.toArray(Path[]::new)) {
            System.out.println(f);
            String content = new String(Files.readAllBytes(f));
            CaseInsensitiveInputStream input = new CaseInsensitiveInputStream(content);
            PlSqlLexer lexer = new PlSqlLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PlSqlParser parser = new PlSqlParser(tokens);

            ParseTree tree = parser.sql_script();
            EmptyListner emptyListner = new EmptyListner();
            ParseTreeWalker walker = new ParseTreeWalker();

            walker.walk(emptyListner, tree);

        }
    }


    public static void main(String[] args) throws IOException {

        String packages_pps = "/home/serii/work/pps/sql/packages/";
        String packages_asur = "/home/serii/work/pps/sql/packages/";

//        tryToReadAllPackages(packages_pps);

        tryToReadAllPackages(packages_asur);

//        test1();
    }


    public static void test1() throws IOException {

//        String content = new String(Files.readAllBytes(Paths.get("/home/serii/tmp/pps.pgservicepu6.pkb")));
//        String content = new String(Files.readAllBytes(Paths.get("/home/serii/tmp/sample.pkb")));
        String content = new String(Files.readAllBytes(Paths.get("/home/serii/tmp/Pgr1f1fund.pkb")));



//        ANTLRInputStream input = new ANTLRInputStream(content);
        CaseInsensitiveInputStream input = new CaseInsensitiveInputStream(content);

        PlSqlLexer lexer = new PlSqlLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PlSqlParser parser = new PlSqlParser(tokens);

        ParseTree tree = parser.sql_script();

        MyListner myListner = new MyListner(input);

        ParseTreeWalker walker = new ParseTreeWalker();

//        walker.walk(myListner, tree);



        RewriterListner rewriterListner = new RewriterListner(tokens);
        walker.walk(rewriterListner, tree);


//        Files.write(Paths.get("/tmp/Pgr1f1fund_with_log.pkb"), rewriterListner.rewriter.getText().getBytes());
        System.out.println(rewriterListner.rewriter.getText());
    }
}
