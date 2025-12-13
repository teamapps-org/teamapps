# Agent Guide for Projector

## Overview
Projector is a Java/JavaScript frontend framework. UI components are managed on the server side and projected to the client (browser).
Every component has a Java (server-side) and TypeScript (client-side) implementation. 

## Modules

### Core Infrastructure
* `projector-common`: Common utilities and base types (e.g., `Color`)
* `projector-dto-base`: Base DTOs for client-server communication protocol
* `projector-dto-dsl`: DSL parser and code generators for component DTOs (Java & TypeScript)
* `projector-maven-plugin`: Maven plugin for component development (DTO generation, resource bundling)
* `projector-icon`: Icon system with registry, providers, encoders/decoders, and caching
* `projector-material-icon-provider`: Material Design icon library provider

### Server-Side
* `projector-server-core`: Core server implementation (session management, WebSocket communication, servlets)
* `projector-server-jetty-embedded`: Embedded Jetty server integration
* `projector-server-undertow-embedded`: Embedded Undertow server integration
* `projector-application`: Application-level abstractions and utilities
* `projector-session-stats-app`: Session statistics and monitoring application
* `projector-component-test-harness`: Testing harness for UI components

### Client-Side Core
* `client/projector-client-communication`: Client-server communication protocol implementation
* `client/projector-client-object-api`: Core API module providing base classes, interfaces, and utilities for both server-side (Java) and client-side (TypeScript) component development. Contains `SessionContext` (server session management, client object registry, navigation, i18n), `AbstractComponent`/`Component` base classes, event system (`ProjectorEvent`), data binding, templates, field abstractions, CSS utilities, and helper functions (parseHtml, animations, debounce, etc.). This is the foundation for building custom UI components.
* `client/projector-client-core`: Core client runtime and UI session management
* `client/projector-client-core-components`: Core UI components (buttons, fields, panels, etc.)
* `client/projector-script`: Dynamic script execution component
* `client/projector-stylesheet`: Dynamic stylesheet management

### UI Components - Layout
* `client/projector-workspace-layout`: Workspace/window management layout
* `client/projector-mobile-layout`: Mobile-optimized layouts
* `client/projector-elegant-panel`: Elegant panel component
* `client/projector-side-drawer`: Side drawer/navigation component
* `client/projector-collapsible`: Collapsible panel component

### UI Components - Templates & Display
* `client/projector-grid-template`: Grid-based template rendering
* `client/projector-mustache-template`: Mustache template rendering
* `client/projector-notification`: Notification component
* `client/projector-notification-bar`: Notification bar component
* `client/projector-progress-indicator`: Progress indicator component
* `client/projector-progress-display`: Progress display component
* `client/projector-blogview`: Blog-style view component

### UI Components - Data & Forms
* `client/projector-tree-components`: Tree view components
* `client/projector-infinite-scrolling-components`: Infinite scrolling list components
* `client/projector-grid-form`: Grid-based form layout
* `client/projector-file-field`: File upload field component
* `client/projector-calendar`: Calendar component
* `client/projector-timegraph`: Time graph/timeline visualization
* `client/projector-chart`: Chart/graphing component

### UI Components - Media & Rich Content
* `client/projector-video-player`: Video player component
* `client/projector-shaka-player`: Shaka Player integration (adaptive streaming)
* `client/projector-mediasoup-client`: MediaSoup client for WebRTC
* `client/projector-audio-level-indicator`: Audio level visualization
* `client/projector-document-viewer`: Document viewer component
* `client/projector-map`: Map component
* `client/projector-chat`: Chat component

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

## Code Generation
- **DTO Files**: Located in `src/main/dto/`, define component interfaces using custom DSL (ANTLR-based grammar in `TeamAppsDto.g4`)
- **Naming Convention**: `Button.dto` → generates `DtoButton.java` + `DtoButton.ts`, while `Button.java` + `Button.ts` contain actual implementations
- **DSL Syntax**: Classes, interfaces, enums, properties, commands (server→client calls), events (client→server notifications), queries (request-response)
- **Type Modifiers**: `*` pointer modifier for client object references (e.g., `DtoTemplate* template`), `required`, `mutable`, `abstract`, `static`
- **Generation Pipeline**: `.dto` files → ANTLR parser → `IntermediateDtoModel` → StringTemplate 4 → Java/TypeScript code
- **Maven Goals**: `generate-java-dtos` (generates to `target/generated-sources/projector-dto/`), `generate-typescript-dtos` (generates to `src/main/ts/generated/`)
- **Generated Java**: Data classes with Jackson annotations, getters/setters, command handlers, event wrappers, `ClientObject` interface for pointer types
- **Generated TypeScript**: Interfaces for DTOs, command handlers, event sources, `ServerObjectChannel` types
- **Import Dependencies**: Configure `<dtoDependencies>` in pom.xml to import DTOs from other modules
- **Key Classes**: `JavaDtoGenerator`, `TypeScriptDtoGenerator`, `TypeReferenceWrapper`, `ClassOrInterfaceWrapper`, template files in `projector-dto-dsl/src/main/resources/`