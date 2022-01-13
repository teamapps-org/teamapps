/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
declare module "d3-flextree" {

	import {HierarchyNode, HierarchyPointNode} from "d3-hierarchy";

	export interface FlexTreeLayout<Datum> {
		/**
		 * Lays out the specified root hierarchy.
		 * You may want to call `root.sort` before passing the hierarchy to the tree layout.
		 *
		 * @param root The specified root hierarchy.
		 */
		(root: HierarchyNode<Datum>): HierarchyPointNode<Datum>;

		/**
		 * If children is specified, sets the specified children accessor function.
		 * If children is not specified, returns the current children accessor function,
		 *  which by default assumes that the input data is an object with a children property,
		 *  whose value is either an array or null if there are no children: data => data.children
		 *
		 * Note that unlike the other accessors, this takes a data node as an argument.
		 * This is used only in the creation of a hierarchy, prior to computing the layout, by the layout.hierarchy method.
		 */
		children(): (d: Datum) => Datum[];

		children(children: (d: Datum) => Datum[]): this;


		/**
		 * If nodeSize is specified as a two-element array [xSize, ySize], then this sets that as the fixed size for every node in the tree.
		 * If nodeSize is a function, then that function is passed the hierarchy node as an argument, and should return a two-element array.
		 * If nodeSize is not specified, this returns the current setting.
		 *
		 * The default nodeSize assumes that a node's size is available as a property on the data item:
		 * node => node.data.size
		 */
		nodeSize(nodeSize: [number, number]): this;

		nodeSize(nodeSize: (node: HierarchyNode<Datum>) => [number, number]): this;

		nodeSize(): [number, number] | ((d: Datum) => [number, number]);

		/**
		 * If a spacing argument is given as a constant number, then the layout will insert the given fixed spacing between every adjacent node.
		 * If it is given as a function, then that function will be passed two nodes, and should return the minimum allowable spacing between those nodes.
		 * If spacing is not specified, this returns the current spacing, which defaults to 0.
		 *
		 * To increase the spacing for nodes as the distance of their relationship increases, you could use, for example:
		 * layout.spacing((nodeA, nodeB) => nodeA.path(nodeB).length);
		 */
		spacing(spacing: number): this;

		spacing(spacing: (node1: HierarchyNode<Datum>, node2: HierarchyNode<Datum>) => number): this;

		spacing(): number | ((node1: HierarchyNode<Datum>, node2: HierarchyNode<Datum>) => number);
	}

	/**
	 * Creates a new tree layout with default settings.
	 */
	export function flextree<Datum>(): FlexTreeLayout<Datum>;
}

