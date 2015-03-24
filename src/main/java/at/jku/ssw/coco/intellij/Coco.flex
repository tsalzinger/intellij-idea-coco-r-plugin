package at.jku.ssw.coco.intellij;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.TokenType;

%%

%class CocoLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
    StringBuffer blockComment = new StringBuffer();
    int blockCommentLevel = 0;
    int stateBeforeComment = 0;
    int stateBeforeSemAction = 0;
    int stateBeforeString = 0;
    int stateBefore = 0;
    int stateStart = 0;

    private void startComplexToken(int state) {
        stateBefore = yystate();
        int length = yylength();
        stateStart = zzStartRead;
        if (state == GLOBAL) {
            stateStart += length;
        }
        yybegin(state);
    }

    private IElementType endComplexToken(IElementType value) {
        zzStartRead = stateStart;
        yybegin(stateBefore);
        return value;
    }

    private IElementType endOfJavaCode(IElementType value) {
        if (yystate() == GLOBAL) {
            int length = yylength();
            zzStartRead = stateStart;
            yypushback(length);
            yybegin(YYINITIAL);
            return CocoTypes.JAVACODE;
        }

        yybegin(YYINITIAL);
        return value;
    }
%}


CRLF = (\n|\r|\r\n)
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT=("//")[^\r\n]*
//SEM_ACTION_SYMBOLS = "(." .*? ".)"

/* comments */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
BlockComment = {TraditionalComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*


ident = [:jletter:] [:jletterdigit:]*
hexValue = ([:digit:]|"A"|"B"|"C"|"D"|"E"|"F")
hexCharValue = \\u{hexValue}{hexValue}{hexValue}{hexValue}
escapesequences = (\\\\ | \\0 | \\r | \\n | \\t | \\v | \\f | \\a | \\b | {hexCharValue})
char = "'" ([^"'"] | {escapesequences} | \\\' | " ") "'"


importpath=[:jletter:]+([\.][:jletter:]+)*[\.]([:jletter:]+|[\*])";"

javacode=[^("IGNORECASE"|"CHARACTERS"|"TOKENS")]*?
SEM_ACTION= !([^]* "\.)" [^]*) "\.)"
//([^"\.)"]|{CRLF})*?
SEM_ACTION_INCL_START_STOP = "(\." ~"\.)"
// make javacode regex not match any top level keywords... (TOKEN_IGNORECASE, TOKEN, PRAGMA, ...)

//%state WAITING_VALUE
%state BLOCK_COMMENT
%xstate STRING
%state IMPORT_STATE
%state COMPILER
%state GLOBAL
%state SCANNER, TOKENS, PRAGMAS, COMMENTDECL, WHITESPACEDECL
%state PARSER
%state SEM_ACTION
%state ATTRIBUTES


%%

//{END_OF_LINE_COMMENT}                           { return CocoTypes.LINE_COMMENT; }
//
//<BLOCK_COMMENT> {
//    [^("/*"|"*/")]+ {}
//    "*/" {
////        blockCommentLevel--;
////        if (blockCommentLevel == 0) {
//            return endComplexToken(CocoTypes.BLOCK_COMMENT);
////        }
//    }
//}
//"/*" {
//    startComplexToken(BLOCK_COMMENT);
//}

<YYINITIAL> {
    {BlockComment}      { return CocoTypes.BLOCK_COMMENT; }
    {EndOfLineComment}  { return CocoTypes.LINE_COMMENT; }
    "\""                { startComplexToken(STRING); }
    "import"            { yybegin(YYINITIAL); return CocoTypes.IMPORT; }
    {importpath}        { yybegin(YYINITIAL); return CocoTypes.IMPORTPATH; }
    {SEM_ACTION_INCL_START_STOP} { return CocoTypes.SEM_ACTION_; }
}

<STRING> {
    [^\n\r\"\\]+ {}
    {escapesequences}      {}
    \\\"      {}
    "\"" {
        return endComplexToken(CocoTypes.STRING);
    }
}

<YYINITIAL> {
    "COMPILER" {
        yybegin(COMPILER);
        return CocoTypes.TOKEN_COMPILER;
    }
}

"CHARACTERS"        { return endOfJavaCode(CocoTypes.TOKEN_CHARACTERS); }
"TOKENS"            { return endOfJavaCode(CocoTypes.TOKEN_TOKENS); }
"CONTEXT"           { return endOfJavaCode(CocoTypes.TOKEN_CONTEXT); }
"IGNORECASE"        { return endOfJavaCode(CocoTypes.TOKEN_IGNORECASE); }
"PRAGMAS"           { return endOfJavaCode(CocoTypes.TOKEN_PRAGMAS); }
"PRODUCTIONS"       { return endOfJavaCode(CocoTypes.TOKEN_PRODUCTIONS); }
"END"               { return endOfJavaCode(CocoTypes.TOKEN_END); }

"COMMENTS"  { return CocoTypes.COMMENTS; }
"IGNORE"    { return CocoTypes.IGNORE; }

"FROM"      { return CocoTypes.FROM; }
"TO"        { return CocoTypes.TO; }
"NESTED"    { return CocoTypes.NESTED; }

"SYNC"      { return CocoTypes.SYNC; }
"WEAK"      { return CocoTypes.WEAK; }
"IF"        { return CocoTypes.IF; }

<COMPILER> {ident}  {
    System.out.println("COMPILER " + yytext());
    startComplexToken(GLOBAL);
    return CocoTypes.IDENT;
}

<GLOBAL> {
    {char} {}
    {ident} {}
    .. | {LineTerminator} {}
    .{LineTerminator} {}
    . {
        endComplexToken(null);
        yybegin(YYINITIAL);
        return CocoTypes.JAVACODE;
    }
}

<ATTRIBUTES> {
    [^(">")]+ {return CocoTypes.ARBITRARY_TEXT;}
}


{WHITE_SPACE}+                                              {return TokenType.WHITE_SPACE; }
{CRLF}+                                              {return TokenType.WHITE_SPACE; }

".." {return CocoTypes.RANGE;}
"." {return CocoTypes.TERMINATOR;}
"=" {return CocoTypes.ASSIGNMENT;}
"+" {return CocoTypes.PLUS;}
"-" {return CocoTypes.MINUS;}
"<" {yybegin(ATTRIBUTES); return CocoTypes.SMALLER_THEN;}
">" {yybegin(YYINITIAL); return CocoTypes.GREATER_THEN;}
"|" {return CocoTypes.PIPE;}
"(" {return CocoTypes.PAR_OPEN;}
")" {return CocoTypes.PAR_CLOSE;}
"[" {return CocoTypes.BRACK_OPEN;}
"]" {return CocoTypes.BRACK_CLOSE;}
"{" {return CocoTypes.CURL_OPEN;}
"}" {return CocoTypes.CURL_CLOSE;}
"ANY" { return CocoTypes.ANY; }

{char} { return CocoTypes.CHAR; }
{ident} {
    return CocoTypes.IDENT;
}

<<EOF>> {
    if (yystate() == GLOBAL) {
        endComplexToken(null);
        yybegin(YYINITIAL);
        return CocoTypes.JAVACODE;
    }

    return null;
}

.                                                           {return TokenType.BAD_CHARACTER; }