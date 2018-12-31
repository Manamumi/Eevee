package xyz.eevee.eevee.parser;

import java.util.Stack;

public class Tokenizer {
    private static final char[] WHITESPACE_CHARACTERS = {' ', '\n', '\t'};
    private int currentIndex;
    private Stack<Integer> indexStack;
    private String[] tokenList;
    private char[] str;

    public Tokenizer(String str) {
        currentIndex = 0;
        indexStack = new Stack<>();
        tokenList = new String[str.length()];
        this.str = str.toCharArray();
    }

    public String next() {
        if (currentIndex >= str.length) {
            return null;
        }

        if (tokenList[currentIndex] != null) {
            String token = tokenList[currentIndex];
            currentIndex += token.length();
            return token;
        }

        final char DOUBLE_QUOTE = '"';
        final char SINGLE_QUOTE = '\'';
        final char ESCAPE_CHAR = '\\';

        boolean escape = false;
        boolean doubleQuote = false;
        boolean singleQuote = false;

        StringBuilder token = new StringBuilder();

        for (; currentIndex < str.length; currentIndex++) {
            if (!escape && str[currentIndex] == DOUBLE_QUOTE && !singleQuote) {
                doubleQuote = !doubleQuote;

                if (token.length() != 0 && currentIndex + 1 < str.length && !isWhitespace(str[currentIndex + 1])) {
                    token.append(str[currentIndex]);
                }
            } else if (!escape && str[currentIndex] == SINGLE_QUOTE && !doubleQuote) {
                singleQuote = !singleQuote;

                if (token.length() != 0 && currentIndex + 1 < str.length && !isWhitespace(str[currentIndex + 1])) {
                    token.append(str[currentIndex]);
                }
            } else if (!escape && !doubleQuote && !singleQuote && isWhitespace(str[currentIndex])) {
                if (token.length() != 0) {
                    break;
                }
            } else if (!escape && str[currentIndex] == ESCAPE_CHAR) {
                escape = true;
            } else {
                token.append(str[currentIndex]);
                escape = false;
            }
        }

        String finalToken = token.toString();

        for (int n = currentIndex - finalToken.length(); n < currentIndex; n++) {
            tokenList[n] = finalToken;
        }

        return finalToken;
    }

    public boolean hasNext() {
        return this.currentIndex < str.length;
    }

    public void stash() {
        indexStack.push(currentIndex);
    }

    public void pop() {
        currentIndex = indexStack.pop();
    }

    public void reset() {
        currentIndex = 0;
    }

    private boolean isWhitespace(char c) {
        for (char whitespace : Tokenizer.WHITESPACE_CHARACTERS) {
            if (whitespace == c) {
                return true;
            }
        }

        return false;
    }
}
