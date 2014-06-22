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
%}


CRLF = (\n|\r|\r\n)
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT=("//")[^\r\n]*
//SEM_ACTION = "(." .*? ".)"

ident = [:jletter:] [:jletterdigit:]*
string="\""[^"\""]*"\""
// TODO support escape characters in string
char = "'" [^"'"] "'"
hexValue = ([:digit:]|"A"|"B"|"C"|"D"|"E"|"F")
hexCharValue = '\\u{hexValue}{hexValue}{hexValue}{hexValue}'
escapesequences = ('\\\\' | '\\r' | '\\\"' | '\\0' | '\\r' | '\\n' | '\\t' | '\\v' | '\\f' | '\\a' | '\\b' | {hexCharValue})
importpath=[:jletter:]+([\.][:jletter:]+)*[\.]([:jletter:]+|[\*])";"

javacode=[^("IGNORECASE"|"CHARACTERS"|"TOKENS")]*?
SEM_ACTION= ([^"\.)"]|{CRLF})*?
// make javacode regex not match any top level keywords... (IGNORECASE, TOKEN, PRAGMA, ...)

//%state WAITING_VALUE
%state BLOCK_COMMENT
%state IMPORT_STATE
%state COMPILER
%state GLOBAL
%state SCANNER, CHARACTERS, TOKENS, PRAGMAS, COMMENTDECL, WHITESPACEDECL
%state PARSER
%state SEM_ACTION
%state ATTRIBUTES
%%

{END_OF_LINE_COMMENT}                           { return CocoTypes.LINE_COMMENT; }

"/*" {blockCommentLevel++; if (blockCommentLevel == 1) {stateBeforeComment = yystate(); yybegin(BLOCK_COMMENT); return CocoTypes.BLOCK_COMMENT;}}
<BLOCK_COMMENT> {
  [^("/*"|"*/")]+ {}
  "*/" {blockCommentLevel--; if (blockCommentLevel == 0) {yybegin(stateBeforeComment);}}
}


<YYINITIAL> "import"                                        { yybegin(YYINITIAL); return CocoTypes.IMPORT; }

<YYINITIAL> {importpath}                                { yybegin(YYINITIAL); return CocoTypes.IMPORTPATH; }
<YYINITIAL> {WHITE_SPACE}+                                { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }


"COMPILER"                              { yybegin(COMPILER); return CocoTypes.COMPILER; }
"CHARACTERS"                              { yybegin(CHARACTERS); return CocoTypes.CHARACTERS_; }
"TOKENS"                              { yybegin(TOKENS); return CocoTypes.TOKENS_; }
"CONTEXT"                              { yybegin(CHARACTERS); return CocoTypes.CONTEXT; }
"IGNORECASE" { yybegin(SCANNER); return CocoTypes.IGNORECASE; }
"PRAGMAS"                              { yybegin(PRAGMAS); return CocoTypes.PRAGMAS_; }
"PRODUCTIONS" {yybegin(PARSER); return CocoTypes.PRODUCTIONS;}
"END"                              { yybegin(YYINITIAL); return CocoTypes.END; }
<COMPILER> {ident}                                { yybegin(GLOBAL); return CocoTypes.IDENT; }


<PRAGMAS, PARSER> {
    "(." {yybegin(SEM_ACTION); return CocoTypes.SEM_ACTION_START;  }
}

<SEM_ACTION> {
   {WHITE_SPACE}+ {return CocoTypes.SEM_ACTION_; }
   ".)" {yybegin(PRAGMAS); return CocoTypes.SEM_ACTION_END; }
   {SEM_ACTION} { return CocoTypes.SEM_ACTION_; }
}

<GLOBAL> {
    {javacode} { return CocoTypes.JAVACODE; }
}

<GLOBAL, SCANNER, PRAGMAS, COMMENTDECL> {
    "COMMENTS" {yybegin(COMMENTDECL); return CocoTypes.COMMENTS; }
    "IGNORE" {yybegin(WHITESPACEDECL); return CocoTypes.IGNORE; }
}

<COMMENTDECL> {
    "FROM" { return CocoTypes.FROM; }
    "TO" { return CocoTypes.TO; }
    "NESTED" { return CocoTypes.NESTED; }
}

<PARSER> {
    "SYNC" { return CocoTypes.ANY; }
    "WEAK" {return CocoTypes.WEAK;}
    "IF" {return CocoTypes.IF;}
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
">" {yybegin(PARSER); return CocoTypes.GREATER_THEN;}
"|" {return CocoTypes.PIPE;}
"(" {return CocoTypes.PAR_OPEN;}
")" {return CocoTypes.PAR_CLOSE;}
"[" {return CocoTypes.BRACK_OPEN;}
"]" {return CocoTypes.BRACK_CLOSE;}
"{" {return CocoTypes.CURL_OPEN;}
"}" {return CocoTypes.CURL_CLOSE;}
"ANY" { return CocoTypes.ANY; }

{string} { return CocoTypes.STRING; }
{char} { return CocoTypes.CHAR; }
{escapesequences} { return CocoTypes.CHAR; }
{ident} { return CocoTypes.IDENT; }

.                                                           { return TokenType.BAD_CHARACTER; }