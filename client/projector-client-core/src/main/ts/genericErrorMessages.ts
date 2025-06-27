import {parseHtml, type Showable} from "projector-client-object-api";

export function	createGenericErrorMessageShowable(title: string, message: string, showErrorIcon: boolean): Showable {
	let $div = parseHtml(`
		<div style="position: absolute; inset: 0 0 0 0; z-index: 1000000; display: flex; justify-content: center; align-items: center; background-color: rgba(0, 0, 0, 0.5);">
			<div style="width: 370px; height: 200px; max-width: 100%; max-height: 100%; box-shadow: 3px 10px 70px rgb(0 0 0 / 85%);">
				<div style="width: 100%; height: 100%; background-color: white; margin: 0;">
					<div style="min-height: 32px; border-bottom: 1px solid rgba(0, 0, 0, 0.09); padding: 0 1px 0 8px; display: flex; align-items: center;">Session Expired</div>
					<div style="padding: 15px">
							<div style="text-align: justify; margin-bottom: 15px">${message}</div>
							<div style="display: flex; justify-content: space-around;">
								<div class="ok" style="display: inline-block; text-align: center; cursor: pointer; padding: 5px; border-radius: 3px; user-select: none; border: 1px solid rgba(0, 0, 0, 0.09);">OK</div>
								<div class="reload" style="display: inline-block; text-align: center; cursor: pointer; padding: 5px; border-radius: 3px; user-select: none; border: 1px solid rgba(0, 0, 0, 0.09);">Reload</div>
							</div>
					</div>
				</div>
			</div>
		</div>
		`);
	$div.querySelector<HTMLElement>(':scope .ok').addEventListener('click', () => $div.remove());
	$div.querySelector<HTMLElement>(':scope .reload').addEventListener('click', () => window.location.reload());
	return {
		show: () => document.body.append($div)
	};
}