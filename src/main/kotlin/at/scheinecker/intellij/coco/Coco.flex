package at.scheinecker.intellij.coco;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static at.scheinecker.intellij.coco.psi.CocoTypes.*;

%%

%{
  private IElementType embeddedCodeEndToken;
  private int resolverDepth = 0;
  private boolean globalsStarted = false;
  private boolean hasDirectiveValue = false;

  public CocoLexer() {
    this((java.io.Reader)null);
  }
%}

%s STATE_COMPILER
%s STATE_COMPILER_NAME
%s STATE_GLOBAL_FIELDS_AND_METHODS
%s STATE_EMBEDDED_CODE
%s STATE_RESOLVER_EMBEDDED_CODE
%s STATE_ATTRIBUTES
%s STATE_DIRECTIVE

%public
%class CocoLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

hexValue = ([:digit:]|[A-F]|[a-f])
hexCharValue = \\u{hexValue}{4}
escapesequences = (\\\' | \\\" | \\\\ | \\0 | \\r | \\n | \\t | \\v | \\f | \\a | \\b | {hexCharValue})
EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+
STRING=\"([^\"\n\\]|{escapesequences})*?\"
CHAR='([^"'"]|{escapesequences}|" ")'
WHITE_SPACE=[ \t\n\x0B\f\r]+
LINE_COMMENT="//".*
BLOCK_COMMENT="/"\*([^\*]|\*+[^/\*])*\*+"/"
NUMBER=[:digit:]+
IDENT=[:letter:]([:letter:]|[:digit:])*
DIRECTIVE_NAME=\$[:letter:]+
ANY_CHAR=.

%%

<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {LINE_COMMENT}       { return LINE_COMMENT; }
  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }
  {DIRECTIVE_NAME}     { hasDirectiveValue = false; yybegin(STATE_DIRECTIVE); return DIRECTIVE_NAME; }

  "COMPILER"           { yybegin(STATE_COMPILER_NAME); return KEYWORD_COMPILER; }

  [^]                  {
                         yypushback(yylength());
                         embeddedCodeEndToken = KEYWORD_COMPILER;
                         yybegin(STATE_EMBEDDED_CODE);
                       }
}

<STATE_COMPILER> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "ANY"                { return KEYWORD_ANY; }
  "CASE"               { return KEYWORD_CASE; }
  "CHARACTERS"         { return KEYWORD_CHARACTERS; }
  "COMPILER"           { yybegin(STATE_COMPILER_NAME); return KEYWORD_COMPILER; }
  "COMMENTS"           { return KEYWORD_COMMENTS; }
  "CONTEXT"            { return KEYWORD_CONTEXT; }
  "END"                { return KEYWORD_END; }
  "FROM"               { return KEYWORD_FROM; }
  "IF"                 { yybegin(STATE_RESOLVER_EMBEDDED_CODE); return KEYWORD_IF; }
  "IGNORE"             { return KEYWORD_IGNORE; }
  "IGNORECASE"         { return KEYWORD_IGNORECASE; }
  "NESTED"             { return KEYWORD_NESTED; }
  "out"                { return KEYWORD_OUT; }
  "PRAGMAS"            { return KEYWORD_PRAGMAS; }
  "PRODUCTIONS"        { return KEYWORD_PRODUCTIONS; }
  "SYNC"               { return KEYWORD_SYNC; }
  "TO"                 { return KEYWORD_TO; }
  "TOKENS"             { return KEYWORD_TOKENS; }
  "WEAK"               { return KEYWORD_WEAK; }
  "."                  { return TERMINATOR; }
  "+"                  { return PLUS; }
  "-"                  { return MINUS; }
  ".."                 { return RANGE; }
  "="                  { return ASSIGNMENT; }
  "{"                  { return CURL_OPEN; }
  "}"                  { return CURL_CLOSE; }
  "("                  { return PAR_OPEN; }
  ")"                  { return PAR_CLOSE; }
  "["                  { return BRACK_OPEN; }
  "]"                  { return BRACK_CLOSE; }
  "<."                 { embeddedCodeEndToken = ATTRIBUTES_END; yybegin(STATE_ATTRIBUTES); return ATTRIBUTES_START; }
  ".>"                 { return ATTRIBUTES_END; }
  "<"                  { embeddedCodeEndToken = GREATER_THEN; yybegin(STATE_ATTRIBUTES); return SMALLER_THEN; }
  ">"                  { return GREATER_THEN; }
  "(."                 { embeddedCodeEndToken = SEM_ACTION_END; yybegin(STATE_EMBEDDED_CODE); return SEM_ACTION_START; }
  ".)"                 { return SEM_ACTION_END; }
  "|"                  { return PIPE; }

  {STRING}             { return STRING; }
  {CHAR}               { return CHAR; }
  {LINE_COMMENT}       { return LINE_COMMENT; }
  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }
  {NUMBER}             { return NUMBER; }
  {IDENT}              { return IDENT; }
  {ANY_CHAR}           { return ANY_CHAR; }

  [^]                  { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<STATE_DIRECTIVE> {
  "="                  { return ASSIGNMENT; }
  {WHITE_SPACE}        {
                         yypushback(yylength());
                         yybegin(YYINITIAL);
                         if (hasDirectiveValue) {
                           return DIRECTIVE_VALUE;
                         }
                       }
  [^]                  { hasDirectiveValue = true; }
}

<STATE_ATTRIBUTES> {
    "out"              { return KEYWORD_OUT; }
    {WHITE_SPACE}      { return com.intellij.psi.TokenType.WHITE_SPACE; }
    [^]                { yypushback(yylength()); yybegin(STATE_EMBEDDED_CODE);}
}

<STATE_EMBEDDED_CODE> {
  ">"                  {
                         if (embeddedCodeEndToken == GREATER_THEN) {
                           yypushback(yylength());
                           yybegin(STATE_COMPILER);
                           return EMBEDDED_CODE;
                          }
                       }
  ".>"                 {
                         if (embeddedCodeEndToken == ATTRIBUTES_END) {
                           yypushback(yylength());
                           yybegin(STATE_COMPILER);
                           return EMBEDDED_CODE;
                          }
                       }
  ".)"                 {
                         if (embeddedCodeEndToken == SEM_ACTION_END) {
                           yypushback(yylength());
                           yybegin(STATE_COMPILER);
                           return EMBEDDED_CODE;
                          }
                       }
  "COMPILER"           {
                         if (embeddedCodeEndToken == KEYWORD_COMPILER) {
                           yypushback(yylength());
                           yybegin(STATE_COMPILER);
                           return EMBEDDED_CODE;
                          }
                       }
  // everything is valid in here
  [^]                  { }
}

<STATE_COMPILER_NAME> {
  {IDENT}              { yybegin(STATE_GLOBAL_FIELDS_AND_METHODS); return IDENT; }
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }

  [^]                  { yypushback(yylength()); yybegin(STATE_GLOBAL_FIELDS_AND_METHODS); }
}

<STATE_GLOBAL_FIELDS_AND_METHODS> {
  {WHITE_SPACE}        { if (!globalsStarted) { return com.intellij.psi.TokenType.WHITE_SPACE;} }
  "CHARACTERS"         { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "COMMENTS"           { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "IGNORE"             { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "IGNORECASE"         { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "PRAGMAS"            { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "TOKENS"             { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "PRODUCTIONS"        { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  "CHARACTERS"         { yypushback(yylength()); yybegin(STATE_COMPILER); return EMBEDDED_CODE; }
  {LINE_COMMENT}       { globalsStarted = true; }
  {BLOCK_COMMENT}      { globalsStarted = true; }
  [^]                  { globalsStarted = true; }
}

<STATE_RESOLVER_EMBEDDED_CODE> {
  "("                  { resolverDepth++; if (resolverDepth == 1) { return PAR_OPEN; }}
  ")"                  { resolverDepth--;
                           if (resolverDepth == 0) {
                             yypushback(yylength());
                             yybegin(STATE_COMPILER);
                             return EMBEDDED_CODE;
                           }
                       }

  [^]                  { }
}
