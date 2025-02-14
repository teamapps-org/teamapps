grammar TeamAppsDto;

@header {
package org.teamapps.projector.dsl;
}

SEMICOLON: ';';
IMPORT: 'import';
EXTERNAL: 'external';
INTERFACE: 'interface';
EXTENDS: 'extends';
IMPLEMENTS: 'implements';
COMMAND: 'command';
EVENT: 'event';
QUERY: 'query';
RETURNS: 'returns';
Identifier : [a-zA-Z_][a-zA-Z0-9_]*;

classCollection : packageDeclaration importDeclaration* typeDeclaration*;

packageDeclaration : 'package' StringLiteral ':' packageName SEMICOLON+ ;

packageName : Identifier | packageName '.' Identifier;

importDeclaration : IMPORT externalInterfaceTypeModifier? qualifiedTypeName SEMICOLON+;
externalInterfaceTypeModifier : EXTERNAL;

typeDeclaration : classDeclaration | interfaceDeclaration | enumDeclaration;

enumDeclaration : notGeneratedAnnotation? 'enum' Identifier '{' (enumConstant (',' enumConstant)*)? SEMICOLON* '}' ;
enumConstant : Identifier ('=' StringLiteral)?;

classDeclaration : notGeneratedAnnotation? abstractModifier? 'class' Identifier superClassDecl? implementsDecl? '{'
	(propertyDeclaration|commandDeclaration|eventDeclaration|queryDeclaration)*
'}';
interfaceDeclaration : notGeneratedAnnotation? 'interface' Identifier superInterfaceDecl? '{'
	(propertyDeclaration|commandDeclaration|eventDeclaration|queryDeclaration)*
'}';
superClassDecl: 'extends' typeName;
superInterfaceDecl: 'extends' classList;
implementsDecl: 'implements' classList;
classList: ((typeName ',')* typeName)?;
propertyDeclaration : requiredModifier? mutableModifier? type Identifier SEMICOLON+;

commandDeclaration : staticModifier? 'command' Identifier '(' ((formalParameter ',')* formalParameter)? ')' ('returns' type)? SEMICOLON+;
eventDeclaration : staticModifier? 'event' Identifier '(' ((formalParameter ',')* formalParameter)? ')' SEMICOLON+;
queryDeclaration : staticModifier? 'query' Identifier '(' ((formalParameter ',')* formalParameter)? ')' 'returns' type SEMICOLON+;

formalParameter : type Identifier;

type : typeReference | primitiveType ;

typeReference : typeName pointerModifier? typeArguments?;
pointerModifier : '*';

typeName : Identifier ;
qualifiedTypeName : packageName '.' Identifier ;

//typeParameters : '<' typeParameterList '>' ;
//typeParameterList : typeParameter (',' typeParameter)* ;
//typeParameter : typeName typeBound? ;
//typeBound : 'extends' typeName;

typeArguments :   '<' typeArgument (',' typeArgument)* '>' ;
typeArgument : type;

primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

notGeneratedAnnotation : '@NotGenerated';
abstractModifier : 'abstract';
requiredModifier : 'required';
mutableModifier : 'mutable';
staticModifier : 'static';

// EXPRESSIONS

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;


fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

// §3.10.5 String Literals

StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

NullLiteral
    :   'null'
    ;

WS : [ \t\n]+ -> skip;
COMMENT : '/*' .*? '*/' -> skip;
LINE_COMMENT :   '//' ~[\r\n]* -> skip;