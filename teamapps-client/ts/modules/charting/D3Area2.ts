/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {CurveFactory, CurveGenerator, curveLinear, line} from "d3-shape";
import {path, Path} from "d3-path";

type DatumToValue<D, V> = (d: D, i: number, dArr: D[]) => V;

function constant<T>(x: T) {
	return () => x;
}

export class D3Area2<D> {
	private _x0: DatumToValue<D, number> = (_: D) => (_ as any)[0];
	private _x1: DatumToValue<D, number> = null;
	private _y0: DatumToValue<D, number> = constant(0);
	private _y1: DatumToValue<D, number> = (_: D) => (_ as any)[1];
	private _context: any = null;
	private _curve: CurveFactory = curveLinear;
	private _output: CurveGenerator = null;

	public writePath(dataMin: any[], dataMax: any[]) {
		let buffer: Path;
		if (this._context == null) {
			this._output = this._curve(buffer = path());
		}

		this._output.areaStart();
		this._output.lineStart();
		for (let i = 0; i < dataMax.length; i++) {
			this._output.point(+this._x0(dataMax[i], i, dataMax), +this._y0(dataMax[i], i, dataMax));
		}
		this._output.lineEnd();
		this._output.lineStart();
		for (let i = dataMin.length - 1; i >= 0; i--) {
			this._output.point(+this._x1(dataMin[i], i, dataMin), +this._y1(dataMin[i], i, dataMin));
		}
		this._output.lineEnd();
		this._output.areaEnd();

		if (buffer) {
			return this._output = null, buffer + "" || null;
		}
	}

	private arealine() {
		return line<D>().curve(this._curve).context(this._context);
	}

	public x(_: DatumToValue<D, number>) {
		this._x0 = this._x1 = typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public x0(_: DatumToValue<D, number>) {
		this._x0 = typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public x1(_: DatumToValue<D, number>) {
		this._x1 = _ == null ? null : typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public y(_: DatumToValue<D, number>) {
		this._y0 = this._y1 = typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public y0(_: DatumToValue<D, number>) {
		this._y0 = typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public y1(_: DatumToValue<D, number>) {
		this._y1 = _ == null ? null : typeof _ === "function" ? _ : constant(+_);
		return this;
	};

	public lineX0() {
		return this.arealine().x(this._x0).y(this._y0);
	};

	public lineY0 = this.lineX0;

	public lineY1() {
		return this.arealine().x(this._x0).y(this._y1);
	};

	public lineX1() {
		return this.arealine().x(this._x1).y(this._y0);
	};

	public curve(_: CurveFactory) {
		this._curve = _;
		this._context != null && (this._output = this._curve(this._context));
		return this;
	};

	public context(_: any) {
		_ == null ? this._context = this._output = null : this._output = this._curve(this._context = _);
		return this;
	};

}
