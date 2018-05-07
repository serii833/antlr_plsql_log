import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.List;

class RewriterListner extends PlSqlParserBaseListener
{
    TokenStreamRewriter rewriter;
    TokenStream tokens;

    String packageName = "";

    public RewriterListner(TokenStream tokens) {
        this.rewriter = new TokenStreamRewriter(tokens);
        this.tokens = tokens;
    }


    @Override
    public void enterCreate_package_body(PlSqlParser.Create_package_bodyContext ctx) {
        super.enterCreate_package_body(ctx);
        packageName = ctx.package_name(0).getText();
    }


    @Override
    public void enterProcedure_body(PlSqlParser.Procedure_bodyContext ctx) {
        super.enterProcedure_body(ctx);
        addLogging(ctx.identifier(), ctx.body(), ctx.parameter());
    }

    @Override
    public void enterFunction_body(PlSqlParser.Function_bodyContext ctx) {
        super.enterFunction_body(ctx);
        addLogging(ctx.identifier(), ctx.body(), ctx.parameter());
    }


    private void addLogging(PlSqlParser.IdentifierContext identifierCtx, PlSqlParser.BodyContext bodyCtx, List<PlSqlParser.ParameterContext> paramsCtx) {
        injectDeclareLog(bodyCtx);

        injectBeginLog(packageName, identifierCtx.getText(), bodyCtx, paramsCtx);

        injectEndLog(bodyCtx);

        injectExceptionsLogging(bodyCtx);
    }


    private void injectDeclareLog(PlSqlParser.BodyContext bodyCtx) {
        String declareLogText = "\tlog tLog;\n";
        rewriter.insertBefore(bodyCtx.getStart(), declareLogText);
    }


    private void injectBeginLog(String packageName, String procName, PlSqlParser.BodyContext body, List<PlSqlParser.ParameterContext> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\tlog := new tLog('%s', '%s');\n", packageName.toLowerCase(), procName.toLowerCase()));

        for(PlSqlParser.ParameterContext p : params) {
            String paramName = p.parameter_name().getText();
            sb.append(String.format("\tlog.add_proc_param('%s', %s);\n", paramName.toLowerCase(), paramName));
        }

        sb.append("\tlog.info('start');\n");


        rewriter.insertAfter(body.getStart(), "\n" + sb.toString());
    }


    private void injectExceptionsLogging(PlSqlParser.BodyContext body) {
        if(body.exception_handler().size() == 0)
            return;

        for(PlSqlParser.Exception_handlerContext exc : body.exception_handler()) {
            Token exceptionStatements = exc.seq_of_statements().getStart();

            rewriter.insertBefore(exceptionStatements, "log.error('');\n\t\t");
        }

    }


    private void injectEndLog(PlSqlParser.BodyContext body)
    {
        String endLogText = "\n\tlog.info('end');\n";

        Token token = null;

        if (body.exception_handler().size() == 0) {
            token = body.getStop();
            if (!token.getText().equals("END"))
                token = getPrevToken(token.getTokenIndex());
        }
        else {
            token = body.exception_handler(0).getStart();
            token = getPrevToken(token.getTokenIndex());
        }

        rewriter.insertBefore(token, endLogText + "\n");
    }



    private Token getPrevToken(int tokenIndex) {
        Token token;
        while(true) {
            tokenIndex = tokenIndex - 1;
            token = tokens.get(tokenIndex);
            if (token.getChannel() == 0)
                break;
        }
        return token;
    }

}