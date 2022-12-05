import {ClientObject} from "../ClientObject";
import {DtoTemplate} from "../generated";

export interface Template<C extends DtoTemplate = DtoTemplate> extends ClientObject<C> {
	render: (data: any) => string;
}