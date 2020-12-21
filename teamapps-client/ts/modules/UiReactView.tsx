/*
 * Copyright (c) 2020 teamapps.org (see code comments for author's name)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@less/components/UiReactTestComponent.less";

import * as React from "react";
import {RefObject} from "react";
import * as ReactDOM from "react-dom";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiReactViewCommandHandler, UiReactViewConfig} from "../generated/UiReactViewConfig";
import JsxParser from "react-jsx-parser";
import {TProps} from "react-jsx-parser/dist/components/JsxParser";
import {UiComponent} from "./UiComponent";

class TeamappsJsxParser extends JsxParser {
	constructor(props: TProps, context: any) {
		super(props, context);
	}

	componentDidMount() {
		(this.props.bindings as any).updateCallback();
	}

	componentDidUpdate(nextProps: Readonly<TProps>, nextState: Readonly<{}>, nextContext: any) {
		(nextProps.bindings as any).updateCallback();
	}
}

export class UiReactView extends AbstractUiComponent<UiReactViewConfig> implements UiReactViewCommandHandler {

	private $main: HTMLElement;

	private refs: { [name: string]: RefObject<HTMLElement> } = {};

	constructor(config: UiComponentConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = document.createElement('div');
		this.$main.classList.add('UiReactView');
		this.update();
	}

	private update() {
		for (const refName in this._config.componentByRefName) {
			if (this.refs[refName] == null) {
				this.refs[refName] = React.createRef<HTMLElement>();
			}
		}
		let bindings = {
			...this._config.propValueByPropName,
			...this.refs,
			updateCallback: () => {
				for (const [refName, component] of Object.entries(this._config.componentByRefName)) {
					if (bindings[refName].current && !bindings[refName].current.contains((component as AbstractUiComponent).getMainElement())) {
						bindings[refName].current.appendChild((component as AbstractUiComponent).getMainElement());
					}
				}
			}
		} as any;
		let div = <TeamappsJsxParser
			autoCloseVoidElements
			disableKeyGeneration
			bindings={bindings}
			jsx={`<div className="react-content-wrapper" key={null}>${this._config.jsx}</div>`}/>;
		ReactDOM.render(div, this.$main);
	}

	protected doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addComponent(refName: string, component: unknown): void {
		if (this.refs[refName].current != null) {
			this.refs[refName].current.innerHTML = "";
		}
		this._config.componentByRefName[refName] = component;
		this.update();
	}

	removeComponent(component: unknown): void {
		(component as UiComponent).getMainElement().remove();
		this.update();
	}

	setProp(propName: string, value: string): void {
		this._config.propValueByPropName[propName] = value;
		this.update();
	}

}


TeamAppsUiComponentRegistry.registerComponentClass("UiReactView", UiReactView);