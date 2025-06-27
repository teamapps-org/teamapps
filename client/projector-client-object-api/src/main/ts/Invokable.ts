export interface Invokable {

	invoke?(functionName: string, parameters: any[]): Promise<any>;

	executeCommand?(name: "invoke", params: any[]): Promise<any>;
	
}