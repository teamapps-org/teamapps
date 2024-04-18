import {ClientObject} from "../ClientObject";
import {DtoTemplate} from "../generated";

export interface Template extends ClientObject {
	render: (data: any) => string;
}