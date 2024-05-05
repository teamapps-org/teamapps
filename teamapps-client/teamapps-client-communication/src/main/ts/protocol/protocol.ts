// =========== SERVER ============

export interface ServerMessage {
	_type: 'INIT_OK' | 'INIT_NOK' | 'REINIT_OK' | 'REINIT_NOK' | 'PING' | 'SESSION_CLOSED' | 'REGISTER_LIB' | 'CREATE_OBJ' | 'DESTROY_OBJ' | 'TOGGLE_EVT' | 'CMD' | 'QUERY_RES';
}

export interface ReliableServerMessage extends ServerMessage {
	sn: number; // sequenceNumber
}

export interface INIT_OK extends ServerMessage {
	_type: 'INIT_OK';
	minRequestedCommands: number;
	maxRequestedCommands: number;
	sentEventsBufferSize: number;
	keepaliveInterval: number
}

export interface INIT_NOK extends ServerMessage {
	_type: 'INIT_NOK';
	reason: SessionClosingReason
}

export interface REINIT_OK extends ServerMessage {
	_type: 'REINIT_OK';
	lastReceivedEventId: number
}

export interface REINIT_NOK extends ServerMessage {
	_type: 'REINIT_NOK';
	reason: SessionClosingReason
}

export interface PING extends ServerMessage {
	_type: 'PING';
}

export interface SESSION_CLOSED extends ServerMessage {
	_type: 'SESSION_CLOSED';
	reason: SessionClosingReason;
	message?: string
}

export enum SessionClosingReason {
	SESSION_NOT_FOUND, SESSION_TIMEOUT, TERMINATED_BY_CLIENT, SERVER_SIDE_ERROR, COMMANDS_OVERFLOW, REINIT_COMMAND_ID_NOT_FOUND, CMD_REQUEST_TOO_LARGE, WRONG_TEAMAPPS_VERSION, TERMINATED_BY_APPLICATION
}

export interface REGISTER_LIB extends ReliableServerMessage {
	_type: 'REGISTER_LIB';
	lid: string;
	jsUrl: string;
	cssUrl: string | null;
}

export interface CREATE_OBJ extends ReliableServerMessage {
	_type: 'CREATE_OBJ';
	lid: string;
	typeName: string;
	oid: string;
	config: any;
	evtNames: string[];
}

export interface DESTROY_OBJ extends ReliableServerMessage {
	_type: 'DESTROY_OBJ';
	oid: string;
}

export interface TOGGLE_EVT extends ReliableServerMessage {
	_type: 'TOGGLE_EVT';
	lid: string;
	oid: string;
	evtName: string;
	enabled: boolean;
}

export interface CMD extends ReliableServerMessage {
	_type: 'CMD';
	lid: string | null, // library uuid
	oid: string | null, // client object id (or null for 'global' functions)
	name: string, // command name (mostly method name)
	params: any[], // the parameters of the command
	r: boolean    // awaitsResult
}

export interface QUERY_RES extends ReliableServerMessage {
	_type: 'QUERY_RES';
	evtId: number;
	result: any
}


// =========== CLIENT ============

export interface ClientMessage {
	_type: 'INIT' | 'REINIT' | 'KEEPALIVE' | 'TERMINATE' | 'REQN' | 'EVT' | 'QUERY' | 'CMD_RES';
}

export interface ReliableClientMessage extends ClientMessage{
	sn: number; // sequenceNumber
}

export interface INIT extends ClientMessage {
	_type: 'INIT';
	sessionId: string;
	clientInfo: ClientInfo;
	maxRequestedCommandId: number
}

export interface ClientInfo {
	screenWidth?: number;
	screenHeight?: number;
	viewPortWidth?: number;
	viewPortHeight?: number;
	highDensityScreen?: boolean;
	timezoneIana?: string;
	timezoneOffsetMinutes?: number;
	clientTokens?: string[];
	location?: string;
	clientParameters?: {[name: string]: string};
	teamAppsVersion?: string
}

export interface Location {
	href?: string;
	origin?: string;
	protocol?: string;
	host?: string;
	hostname?: string;
	port?: number;
	pathname?: string;
	search?: string;
	hash?: string
}

export interface REINIT extends ClientMessage {
	_type: 'REINIT';
	sessionId: string;
	lastReceivedCommandId: number;
	maxRequestedCommandId: number
}

export interface KEEPALIVE extends ClientMessage {
	_type: 'KEEPALIVE';
}

export interface TERMINATE extends ClientMessage {
	_type: 'TERMINATE';
}

// request_n
export interface REQN extends ClientMessage {
	_type: 'REQN';
	lastReceivedCommandId: number;
	maxRequestedCommandId: number
}

export interface EVT extends ReliableClientMessage {
	_type: 'EVT';
	lid: string | null; // library uuid
	oid: string; // client object id
	name: string; // the name of this event
	params: any[] // the parameters of the event
}

export interface QUERY extends ReliableClientMessage {
	_type: 'QUERY';
	lid: string | null; // library uuid
	oid: string; // client object id
	name: string; // the name of this event
	params: any[] // the parameters of the event
}

export interface CMD_RES extends ReliableClientMessage {
	_type: 'CMD_RES';
	cmdSn: number;
	result: any
}




