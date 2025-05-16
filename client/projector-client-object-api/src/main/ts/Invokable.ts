export interface Invokable {
	invoke(functionName: string, parameters: any[]): Promise<any>;
}