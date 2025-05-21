# UiPlayground Component

---

# Repo Log 

## 2025-05-21 Ui Playground Component anlegen 

1. Write `UiPlayground.dto` in `teamapps-ui-api` project 
   1. Run `clean + install` for `teamapps-ui-api` project

2. Create new java class `teamapps-ux/src/main/java/org/teamapps/ux/component/playground/Playground.java`

3. Create new ts class `teamapps-client/ts/modules/UiPlayground.ts`

4. Open `teamapps-client/ts/modules/index.ts`
   1. Add `export {UiPlayground} from "./UiPlayground";`

=> Start Jetty & webpack dev server 
=> current state: error! :(