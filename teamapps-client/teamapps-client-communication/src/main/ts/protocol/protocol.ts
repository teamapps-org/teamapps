import {DtoClientInfo, DtoSessionClosingReason} from "../generated";

export interface DtoAbstractClientMessage {
	_type?: string;
	sessionId: string
}
export interface DtoAbstractClientPayloadMessage extends DtoAbstractClientMessage {
	_type?: string;
	id: number
}
export interface DtoAbstractServerMessage {
	_type?: string;
}
export interface DtoCMD_REQ extends DtoAbstractClientMessage {
	_type?: string;
	lastReceivedCommandId: number;
	maxRequestedCommandId: number
}
export interface DtoCMD_RES extends DtoAbstractClientPayloadMessage {
	_type?: string;
	cmdId: number;
	result: any
}
export interface DtoCMD {
	id: number, // sequence number
	lid: string|null, // library uuid
	cid: string|null, // client object id
	c: DtoCommand, // uiCommand
	r: boolean    // awaitsResult
}
export interface DtoCommand {
	_type?: string;
}
export interface DtoEvent {
	_type?: string;
	componentId?: string
}
export interface DtoEVT extends DtoAbstractClientPayloadMessage {
	_type?: string;
	uiEvent: DtoEvent
}
export interface DtoINIT extends DtoAbstractClientMessage {
	_type?: string;
	clientInfo: DtoClientInfo;
	maxRequestedCommandId: number
}
export interface DtoINIT_NOK extends DtoAbstractServerMessage {
	_type?: string;
	reason: DtoSessionClosingReason
}
export interface DtoINIT_OK extends DtoAbstractServerMessage {
	_type?: string;
	minRequestedCommands: number;
	maxRequestedCommands: number;
	sentEventsBufferSize: number;
	keepaliveInterval: number
}
export interface DtoKEEPALIVE extends DtoAbstractClientMessage {
	_type?: string;
}
export interface DtoMULTI_CMD extends DtoAbstractServerMessage {
	_type?: string;
	cmds: any[]
}
export interface DtoPING extends DtoAbstractServerMessage {
	_type?: string;
}
export interface DtoQRY extends DtoAbstractClientPayloadMessage {
	_type?: string;
	uiQuery: DtoQuery
}
export interface DtoQRY_RES extends DtoAbstractServerMessage {
	_type?: string;
	queryId: number;
	result: any
}
export interface DtoQuery {
	_type?: string;
	componentId?: string
}
export interface DtoREINIT extends DtoAbstractClientMessage {
	_type?: string;
	lastReceivedCommandId: number;
	maxRequestedCommandId: number
}
export interface DtoREINIT_NOK extends DtoAbstractServerMessage {
	_type?: string;
	reason: DtoSessionClosingReason
}
export interface DtoREINIT_OK extends DtoAbstractServerMessage {
	_type?: string;
	lastReceivedEventId: number
}
export interface DtoSESSION_CLOSED extends DtoAbstractServerMessage {
	_type?: string;
	reason: DtoSessionClosingReason;
	message?: string
}
export interface DtoTERMINATE extends DtoAbstractClientMessage {
	_type?: string;
}




