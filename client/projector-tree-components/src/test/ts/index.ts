import {ComboBox} from '../../main/ts/index'; // Adjust path if needed

document.addEventListener('DOMContentLoaded', () => {
	const appDiv = document.getElementById('app');

	const myComponentInstance = new ComboBox(null, null);
	appDiv.appendChild(myComponentInstance.getMainElement());
});