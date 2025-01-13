package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class Parser {
    private VMWriter vmWriter = new VMWriter();

    private static class ParseError extends RuntimeException {}

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    private StringBuilder xmlOutput = new StringBuilder();

    public Parser(byte[] input) {
        scan = new Scanner(input);
        nextToken();
    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }


    public void parse () {

    }

    // funções auxiliares
    public String XMLOutput() {
        return xmlOutput.toString();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }


    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

    private void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        throw error(peekToken, "Expected a statement");

    }

    private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw error(peekToken, "Expected "+type.name());
        }
    }


    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
    }


    private ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
        return new ParseError();
    }

    void parseTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case STRING:
                expectPeek(TokenType.STRING);
                var strValue = currentToken.lexeme;
                vmWriter.writePush(VMWriter.Segment.CONST, strValue.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    vmWriter.writePush(VMWriter.Segment.CONST, strValue.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;
            case IDENT:
                expectPeek(IDENT);
                break;
            case NUMBER:
                expectPeek(TokenType.NUMBER);
                vmWriter.writePush(VMWriter.Segment.CONST, Integer.parseInt(currentToken.lexeme));
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE, NULL, TRUE);
                vmWriter.writePush(VMWriter.Segment.CONST, 0);
                if (currentToken.type == TRUE)
                    vmWriter.writeArithmetic(VMWriter.Command.NOT);
                break;
            case THIS:
                expectPeek(THIS);
                vmWriter.writePush(VMWriter.Segment.POINTER, 0);
                break;
            default:
                throw error(peekToken, "term expected");
        }

        printNonTerminal("/term");
    }
    static public boolean isOperator(String op) {
        return op!= "" && "+-*/<>=~&|".contains(op);
    }

    void parseExpression() {
        printNonTerminal("expression");
        parseTerm ();
        while (isOperator(peekToken.lexeme)) {
            var ope = peekToken.type;
            expectPeek(ope);
            parseTerm();
            compileOperators(ope);
        }
        printNonTerminal("/expression");
    }

    public void compileOperators(TokenType type) {

        if (type == ASTERISK) {
            vmWriter.writeCall("Math.multiply", 2);
        } else if (type == SLASH) {
            vmWriter.writeCall("Math.divide", 2);
        } else {
            vmWriter.writeArithmetic(typeOperator(type));
        }
    }

    private VMWriter.Command typeOperator(TokenType type) {
        if (type == PLUS)
            return VMWriter.Command.ADD;
        if (type == MINUS)
            return VMWriter.Command.SUB;
        if (type == LT)
            return VMWriter.Command.LT;
        if (type == GT)
            return VMWriter.Command.GT;
        if (type == EQ)
            return VMWriter.Command.EQ;
        if (type == AND)
            return VMWriter.Command.AND;
        if (type == OR)
            return VMWriter.Command.OR;
        return null;
    }

    void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENT);

        if (peekTokenIs(LBRACKET)) {
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }

        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");
    }

    void parseSubroutineCall() {
        expectPeek(IDENT);
        expectPeek(LPAREN);
        expectPeek(RPAREN);
    }

    public void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }

    public String VMOutput() {
        return vmWriter.vmOutput();
    }
}
