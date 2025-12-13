# Agent Guide for Projector

## Overview
Projector is a Java/JavaScript frontend framework. UI components are managed on the server side and projected to the client (browser).
Every component has a Java (server-side) and TypeScript (client-side) implementation. 

## Modules

* `client/projector-client-object-api`: Provides the main API for application developers, both for the server side (Java) and client side (TypeScript). It provides base classes and interfaces for component development as well as the `SessionContext` implementation.

## Build Commands
- **Java (Maven)**: `mvn clean install` (root), `mvn clean package` (build), `mvn test` (all tests), `mvn test -Dtest=ClassName#methodName` (single test)
- **TypeScript/Client**: `pnpm install` (install), `pnpm -F projector-<module> build` (build module), `pnpm -F projector-<module> check` (type check)
- Client modules located in `client/projector-*`, use pnpm workspace with filter `-F` flag
- The maven build of any of the `client/projector-*` modules will also trigger the corresponding TypeScript build using `frontend-maven-plugin`.

## Code Style - Java
- **License Header**: Include Apache 2.0 license header block (see existing files for template)
- **Package**: `org.teamapps.projector.*` namespace
- **Imports**: Group by package, standard library first, then third-party, then project imports
- **Naming**: PascalCase for classes/interfaces, camelCase for methods/variables, SCREAMING_SNAKE_CASE for constants
- **Types**: Use Java 21 features, prefer interfaces (`Color`, `IconProvider`), use `@Nullable` annotations where applicable
- **Logging**: Use SLF4J via `LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())`
- **Testing**: JUnit 5 (`@Test`), AssertJ for assertions, Mockito for mocking
- **Test Method Naming**: Use method names like `methodX_shouldDoY_whenZ`

## Code Style - TypeScript
- **License Header**: Include Apache 2.0 license header block (same as Java)
- **Config**: Strict TypeScript with `noUnusedLocals`, `noUnusedParameters`, target ES2020
- **Imports**: Type imports use `type` keyword, absolute imports from workspace packages (`projector-*`)
- **Naming**: PascalCase for classes/types, camelCase for variables/functions, prefix DOM elements with `$` (e.g., `$main`)
- **Types**: Strict typing, use DTOs (e.g., `DtoNotification`), define event types explicitly
- **Framework**: Components extend `AbstractComponent`, use `ProjectorEvent` for events, `parseHtml` for DOM creation
