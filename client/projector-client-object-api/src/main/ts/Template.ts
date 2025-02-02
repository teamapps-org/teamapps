import {ClientObject} from "./ClientObject";

export interface Template extends ClientObject {
	render: (data: any) => string;
}