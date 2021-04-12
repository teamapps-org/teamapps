import {DropDownComponent, SelectionDirection} from "./DropDownComponent";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {QueryFunction} from "../TrivialCore";
import {TrivialTreeBox} from "../TrivialTreeBox";

type TreeBoxDropdownConfig<E> = {
	queryFunction: QueryFunction<E>;
	textHighlightingEntryLimit: number;
	preselectionMatcher: (query: string, entry: E) => boolean;
};

export class TreeBoxDropdown<E> implements DropDownComponent<E> {

	public readonly onValueChanged: TeamAppsEvent<{ value: E; finalSelection: boolean }> = new TeamAppsEvent(this);

	private treeBox: TrivialTreeBox<E>;
	private config: TreeBoxDropdownConfig<E>;

	constructor(config: TreeBoxDropdownConfig<E>, treeBox: TrivialTreeBox<E>) {
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
			let selectedEntry = this.treeBox.selectNextEntry(event.code == "ArrowUp" ? -1 : 1, true);
			if (selectedEntry != null) {
				this.onValueChanged.fire({value: this.treeBox.getSelectedEntry(), finalSelection: false});
				return true;
			} else {
				return false;
			}
		} else if (["ArrowRight", "ArrowLeft"].indexOf(event.code) !== -1) {
			return this.treeBox.setSelectedNodeExpanded(event.code == "ArrowRight");
		}
	}

	async handleQuery(query: string, selectionDirection: SelectionDirection): Promise<boolean> {
		let results = await this.config.queryFunction(query) ?? [];
		this.treeBox.setEntries(results);
		this.getMainDomElement().scrollIntoView({block: "start"}); // make sure we scroll up
		this.treeBox.highlightTextMatches(results.length <= this.config.textHighlightingEntryLimit ? query : null);
		if (selectionDirection === 0) {
			this.treeBox.setSelectedEntryById(null)
		} else {
			let selectedEntry = this.treeBox.selectNextEntry(1, false, true, (entry) => this.config.preselectionMatcher(query, entry));
			if (selectedEntry == null) {
				this.treeBox.selectNextEntry(1, false);
			}
		}
		return results.length > 0;
	}

	setValue(value: E): void {
		this.handleQuery("", 0);
	}

	destroy(): void {
		this.treeBox.destroy();
	}

	getComponent(): TrivialTreeBox<E> {
		return this.treeBox;
	}

}