:: ##########################################################################################################
:: This script starts the webpack dev server using the locally installed node, yarn and webpack.
:: This is particularly useful in environments that do not have a global node, yarn and webpack installation.
:: ##########################################################################################################

@ECHO OFF
SETLOCAL

:: add node and yarn to path
set path=%cd%/node;%cd%/node/yarn/dist/bin;%PATH%

if "%~1" NEQ "" set "appServerUrl=%~1"

if "%appServerUrl%" == "" (
    echo appServerUrl is not set! You can set it as the first parameter of this script or via environment variable.
    exit /B 1
) else (
    echo appServerUrl set to %appServerUrl%
)

:: start dev server
yarn dev