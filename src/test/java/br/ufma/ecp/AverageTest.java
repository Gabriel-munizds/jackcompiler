package br.ufma.ecp;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class AverageTest {
    @Test
    public void average () {
        var input = """
                    class Main {
                     function void main() {
                     var Array a;
                     var int length;
                     var int i, sum;
                     let length = Keyboard.readInt("How many numbers? ");
                     let a = Array.new(length);
                     let i = 0;
                     while (i < length) {
                     let a[i] = Keyboard.readInt("Enter the next number: ");
                     let i = i + 1;
                     }
                     let i = 0; let sum = 0;
                     while (i < length) {
                     let sum = sum + a[i];
                     let i = i + 1;
                     }
                     do Output.printString("The average is: ");
                     do Output.printInt(sum / length);
                     do Output.println();
                     return;
                     }
                     }
                """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void seven () {
        var input = """
                    /** Computes the value of 1 + (2 * 3)
                      * and prints the result at the top-left
                      * corner of the screen. */
                      class Main {
                      function void main() {
                      do Output.printInt(1 + (2 * 3));
                      return;
                      }
                      }
                """;;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parse();
        String actual = parser.VMOutput();
        String expected = "";
        assertEquals(expected, actual);
    }


}
