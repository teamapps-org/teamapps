import {DropDownComponent, SelectionDirection} from "./DropDownComponent";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {HighlightDirection, QueryFunction, TrivialComponent} from "../TrivialCore";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {TrivialTreeBox} from "../TrivialTreeBox";

export class TreeBoxDropdown<E> implements DropDownComponent<E> {

	public readonly onValueChanged: TeamAppsEvent<{ value: E; finalSelection: boolean }> = new TeamAppsEvent(this);

	private treeBox: TrivialTreeBox<E>;
	private config: { queryFunction: QueryFunction<E>; textHighlightingEntryLimit: number };

	constructor(treeBox: TrivialTreeBox<E>, config: {
		queryFunction: QueryFunction<E>,
		textHighlightingEntryLimit: number,
	}) {
		this.treeBox = treeBox;
		this.config = config;
		this.treeBox.onSelectedEntryChanged.addListener(entry => this.onValueChanged.fire({value: entry, finalSelection: true}));
	}

	getMainDomElement(): HTMLElement {
		return this.treeBox.getMainDomElement();
	}

	getValue(): E {
		return this.treeBox.getSelectedEntry();
	}

	handleKeyboardInput(event: KeyboardEvent): boolean {
		if (["ArrowUp", "ArrowDown"].indexOf(event.code) !== -1) {
			this.treeBox.selectNextEntry(event.code == "ArrowUp" ? -1 : 1, true);
			this.onValueChanged.fire({value: this.treeBox.getHighlightedEntry(), finalSelection: false});
			return true;
		} else if (["ArrowRight", "ArrowLeft"].indexOf(event.code) !== -1) {
			this.treeBox.setHighlightedNodeExpanded(event.code == "ArrowRight");
			return true;
		}
	}

	async handleQuery(query: string, selectionDirection: SelectionDirection): Promise<boolean> {
		let results = await this.config.queryFunction(query) ?? [];
		this.treeBox.updateEntries(results);
		this.treeBox.highlightTextMatches(results.length <= this.config.textHighlightingEntryLimit ? query : null);
		if (selectionDirection === 0) {
			this.treeBox.setSelectedEntryById(null)
		} else if (query.length > 0) {
			this.treeBox.selectNextMatchingEntry(1, false, false);
		} else {
			this.treeBox.selectNextEntry(1, false);
		}
		return results.length > 0;
	}

	setValue(value: E): void {
		// no need to do anything here...
	}

	destroy(): void {
		this.treeBox.destroy();
	}

	getComponent(): TrivialTreeBox<E> {
		return this.treeBox;
	}

}