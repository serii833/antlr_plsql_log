import org.antlr.v4.runtime.*;
/**
 *
 * @author Sam Harwell
 */
public class CaseInsensitiveInputStream extends ANTLRInputStream {

    protected char[] lookaheadData;

    public CaseInsensitiveInputStream(String input) {
        super(input);
        lookaheadData = input.toUpperCase().toCharArray();
    }

    @Override
    public int LA(int i) {
        if (i == 0) {
            return 0; // undefined
        }
        if (i < 0) {
            i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
            if ((p + i - 1) < 0) {
                return IntStream.EOF; // invalid; no char before first char
            }
        }

        if ((p + i - 1) >= n) {
            return IntStream.EOF;
        }

        return lookaheadData[p + i - 1];
    }

}