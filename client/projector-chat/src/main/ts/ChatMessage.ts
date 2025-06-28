import {Autolinker} from "autolinker";
import {type DtoChatMessage} from "./generated";
import {humanReadableFileSize, parseHtml} from "projector-client-object-api";
import {removeDangerousTags} from "projector-client-core-components";

const AUTOLINKER = new Autolinker({
	urls: {
		schemeMatches: true,
		wwwMatches: true,
		tldMatches: true
	},
	email: true,
	phone: true,
	mention: false,
	hashtag: false,

	stripPrefix: false,
	stripTrailingSlash: false,
	newWindow: true,

	truncate: {
		length: 70,
		location: 'smart'
	},

	className: ''
});

export class ChatMessage {

	private $main: HTMLElement;
	// @ts-ignore
	private $photos: HTMLElement;
	// @ts-ignore
	private $files: HTMLElement;
	private config: DtoChatMessage;

	constructor(config: DtoChatMessage) {
		this.config = config;
		this.$main = parseHtml(`<div class="message ChatMessage" data-id="${config.id}"></div>`);
		this.update(config);
	}

	public update(config: DtoChatMessage) {
		this.config = config;
		this.$main.classList.toggle("deleted", config.deleted);
		this.$main.innerHTML = "";
		let text = removeDangerousTags(this.config.text);
		text = AUTOLINKER.link(text);
		this.$main.appendChild(parseHtml(`<img class="user-image" src="${this.config.userImageUrl}"></img>`))
		this.$main.appendChild(parseHtml(`<div class="user-nickname">${this.config.userNickname}</div>`))
		this.$main.appendChild(parseHtml(`<div class="text">${text}</div>`))
		this.$main.appendChild(parseHtml(`<div class="photos"></div>`))
		this.$main.appendChild(parseHtml(`<div class="files"></div>`))
		this.$main.appendChild(parseHtml(`<div class="deleted-icon"></div>`))

		this.$photos = this.$main.querySelector(":scope .photos")!;
		this.$files = this.$main.querySelector(":scope .files")!;

		if (this.config.photos != null) {
			this.config.photos.forEach(photo => {
				this.$photos.appendChild(parseHtml(`<img class="photo" src="${photo.imageUrl}">`))
			});
		}
		if (this.config.files != null) {
			this.config.files.forEach(file => {
				this.$files.appendChild(parseHtml(`<a class="file" target="_blank" href="${file.downloadUrl}">
					<div class="file-icon img img-32" style="background-image: url('${file.thumbnailUrl || file.icon}')"> </div>
					<div class="file-name">${file.name}</div>
					<div class="file-size">${humanReadableFileSize(file.length)}</div>
				</a>`))
			});
		}
	}

	public get id() {
		return this.config.id;
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}
}
