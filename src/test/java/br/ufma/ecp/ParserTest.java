package br.ufma.ecp;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static br.ufma.ecp.TestSupport.fromFile;
import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test
    public void testParseTermInteger () {
        var input = "10;";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseTerm();
        var expectedResult =  """
        <term>
        <integerConstant> 10 </integerConstant>
        </term>
        """;

        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        result = result.replaceAll("\r", ""); // no codigo em linux não tem o retorno de carro
        assertEquals(expectedResult, result);

    }


    @Test
    public void testParseTermIdentifer() {
        var input = "varName;";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseTerm();

        var expectedResult =  """
          <term>
          <identifier> varName </identifier>
          </term>
          """;

        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        result = result.replaceAll("\r", ""); // no codigo em linux não tem o retorno de carro
        assertEquals(expectedResult, result);

    }



    @Test
    public void testParseTermString() {
        var input = "\"Hello World\"";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseTerm();

        var expectedResult =  """
          <term>
          <stringConstant> Hello World </stringConstant>
          </term>
          """;

        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        result = result.replaceAll("\r", ""); // no codigo em linux não tem o retorno de carro
        assertEquals(expectedResult, result);

    }

    @Test
    public void testParseExpressionSimple() {
        var input = "10+20";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();

        var expectedResult =  """
          <expression>
          <term>
          <integerConstant> 10 </integerConstant>
          </term>
          <symbol> + </symbol>
          <term>
          <integerConstant> 20 </integerConstant>
          </term>
          </expression>
          """;

        var result = parser.XMLOutput();
        result = result.replaceAll("\r", "");
        expectedResult = expectedResult.replaceAll("  ", "");
        assertEquals(expectedResult, result);

    }

    @Test
    public void testParseLetSimple() {
        var input = "let var1 = 10+20;";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseLet();
        var expectedResult =  """
	     <letStatement>
        <keyword> let </keyword>
        <identifier> var1 </identifier>
        <symbol> = </symbol>
        <expression>
          <term>
          <integerConstant> 10 </integerConstant>
          </term>
          <symbol> + </symbol>
          <term>
          <integerConstant> 20 </integerConstant>
          </term>
          </expression>
        <symbol> ; </symbol>
      </letStatement> 
				""";
        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        result = result.replaceAll("\r", ""); // no codigo em linux não tem o retorno de carro
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseSubroutineCall() {
        var input = "hello()";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseSubroutineCall();

        var expectedResult =  """
          <identifier> hello </identifier>
          <symbol> ( </symbol>
          <symbol> ) </symbol
          """;

        var result = parser.XMLOutput();
        result = result.replaceAll("\r", "");
        expectedResult = expectedResult.replaceAll("  ", "");
        assertEquals(expectedResult, result);

    }

    @Test
    public void testParseDo() {
        var input = "do hello();";
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseDo();

        var expectedResult = """
            <doStatement>
            <keyword> do </keyword>
            <identifier> hello </identifier>
            <symbol> ( </symbol>
            <symbol> ) </symbol>
            <symbol> ; </symbol>
          </doStatement>
                """;
        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        result = result.replaceAll("\r", ""); // no codigo em linux não tem o retorno de carro
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParserWithLessSquareGame() throws IOException {
        var input = fromFile("ExpressionLessSquare/SquareGame.jack");
        var expectedResult =  fromFile("ExpressionLessSquare/SquareGame.xml");

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParserWithSquareGame() throws IOException {
        var input = fromFile("Square/SquareGame.jack");
        var expectedResult =  fromFile("Square/SquareGame.xml");

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        assertEquals(expectedResult, result);
    }


    @Test
    public void testParserWithSquare() throws IOException {
        var input = fromFile("Square/Square.jack");
        var expectedResult =  fromFile("Square/Square.xml");

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        var result = parser.XMLOutput();
        expectedResult = expectedResult.replaceAll("  ", "");
        assertEquals(expectedResult, result);
    }

    @Test
    public void testInt () {
        var input = """
            10
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10       
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleExpression () {
        var input = """
            10 + 30
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10
                push constant 30
                add       
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testLiteralString () {
        var input = """
            "OLA"
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 3
                call String.new 1
                push constant 79
                call String.appendChar 2
                push constant 76
                call String.appendChar 2
                push constant 65
                call String.appendChar 2
                    """;
        assertEquals(expected, actual);
    }
    @Test
    public void testFalse () {
        var input = """
            false
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0       
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testNull () {
        var input = """
            null
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0       
                    """;
        assertEquals(expected, actual);
    }


    @Test
    public void testTrue () {
        var input = """
            true
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0
                not       
                    """;
        assertEquals(expected, actual);
    }


    @Test
    public void testThis () {
        var input = """
            this
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push pointer 0
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testNot () {
        var input = """
            ~ false
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0   
                not    
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testMinus () {
        var input = """
            - 10
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseExpression();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10   
                neg    
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testReturn () {
        var input = """
            return;
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
                push constant 0
                return       
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testReturnExpr () {
        var input = """
            return 10;
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
                push constant 10
                return       
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testIf () {
        var input = """
            if (false) {
                return 10;
            } else {
                return 20;
            }
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
            push constant 0
            if-goto IF_TRUE0
            goto IF_FALSE0
            label IF_TRUE0
            push constant 10
            return
            goto IF_END0
            label IF_FALSE0
            push constant 20
            return
            label IF_END0 
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void testWhile () {
        var input = """
            while (false) {
                return 10;
            } 
            """;

        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parseStatement();
        String actual = parser.VMOutput();
        String expected = """
label WHILE_EXP0
push constant 0
not
if-goto WHILE_END0
push constant 10
return
goto WHILE_EXP0
label WHILE_END0
                    """;
        assertEquals(expected, actual);
    }



    @Test
    public void testSimpleFunctions () {
        var input = """
            class Main {
 
                function int soma (int x, int y) {
                        return  30;
                 }
                
                 function void main () {
                        var int d;
                        return;
                  }
                
                }
            """;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.soma 0
            push constant 30
            return
            function Main.main 1
            push constant 0
            return    
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void testSimpleFunctionWithVar () {
        var input = """
            class Main {

                 function int funcao () {
                        var int d;
                        return d;
                  }
                
                }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.funcao 1
            push local 0
            return
            """;
        assertEquals(expected, actual);
    }
    @Test
    public void testLet () {
        var input = """
            class Main {
            
              function void main () {
                  var int x;
                  let x = 42;
                  return;
              }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 42
            pop local 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void arrayTest () {
        var input = """
            class Main {
                function void main () {
                    var Array v;
                    let v[2] = v[3] + 42;
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 2
            push local 0
            add
            push constant 3
            push local 0
            add
            pop pointer 1
            push that 0
            push constant 42
            add
            pop temp 0
            pop pointer 1
            push temp 0
            pop that 0
            push constant 0
            return        
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void callFunctionTest() {

        var input = """
            class Main {
                function int soma (int x, int y) {
                       return  x + y;
                }
               
                function void main () {
                       var int d;
                       let d = Main.soma(4,5);
                       return;
                 }
               
               }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();


        String actual = parser.VMOutput();
        String expected = """
            function Main.soma 0
            push argument 0
            push argument 1
            add
            return
            function Main.main 1
            push constant 4
            push constant 5
            call Main.soma 2
            pop local 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);


    }

    @Test
    public void methodTest () {
        var input = """
            class Main {
                function void main () {
                    var Point p;
                    var int x;
                    let p = Point.new (10, 20);
                    let x = p.getX();
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 2
            push constant 10
            push constant 20
            call Point.new 2
            pop local 0
            push local 0
            call Point.getX 1
            pop local 1
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void doStatement () {
        var input = """
            class Main {
                function void main () {
                    var int x;
                    let x = 10;
                    do Output.printInt(x);
                    return;
                }
            }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Main.main 1
            push constant 10
            pop local 0
            push local 0
            call Output.printInt 1
            pop temp 0
            push constant 0
            return
                """;
        assertEquals(expected, actual);
    }
    @Test
    public void methodsConstructorTest () {
        var input = """
            class Point {
                field int x, y;
            
                method int getX () {
                    return x;
                }
            
                method int getY () {
                    return y;
                }
            
                method void print () {
                    do Output.printInt(getX());
                    do Output.printInt(getY());
                    return;
                }
            
                constructor Point new(int Ax, int Ay) { 
                  var int w;             
                  let x = Ax;
                  let y = Ay;
                  let w = 42;
                  let x = w;
                  return this;
               }
              }
            """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = """
            function Point.getX 0
            push argument 0
            pop pointer 0
            push this 0
            return
            function Point.getY 0
            push argument 0
            pop pointer 0
            push this 1
            return
            function Point.print 0
            push argument 0
            pop pointer 0
            push pointer 0
            call Point.getX 1
            call Output.printInt 1
            pop temp 0
            push pointer 0
            call Point.getY 1
            call Output.printInt 1
            pop temp 0
            push constant 0
            return
            function Point.new 1
            push constant 2
            call Memory.alloc 1
            pop pointer 0
            push argument 0
            pop this 0
            push argument 1
            pop this 1
            push constant 42
            pop local 0
            push local 0
            pop this 0
            push pointer 0
            return            
                """;
        assertEquals(expected, actual);
    }

}
