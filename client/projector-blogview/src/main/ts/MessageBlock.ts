import {type DtoMessageBlock} from "./generated";
import {parseHtml, removeClassesByFunction, type ServerObjectChannel, type Template} from "projector-client-object-api";
import {removeDangerousTags, ToolButton} from "projector-client-core-components";
import {AbstractBlockComponent} from "./AbstractBlockComponent";
import {fixed_partition} from "image-layout";

export class MessageBlock extends AbstractBlockComponent<DtoMessageBlock> {
	private $main: HTMLElement;
	private $toolButtons: Element;
	private $topRecord: HTMLElement;
	private $htmlContainer: HTMLElement;
	private $images: HTMLElement;
	private images: {
		$img: HTMLImageElement,
		width: number,
		height: number
	}[] = [];

	private readonly minIdealImageHeight = 250;

	constructor(config: DtoMessageBlock, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="pageview-block MessageBlock">
	<div class="tool-buttons"></div>
	<div class="top-record"></div>
	<div class="html"></div>
	<div class="images"></div>
</div>`);
		this.$toolButtons = this.$main.querySelector(":scope .tool-buttons");
		this.$topRecord = this.$main.querySelector(":scope .top-record");
		this.$htmlContainer = this.$main.querySelector(":scope .html");
		this.$images = this.$main.querySelector(":scope .images");

		this.$toolButtons.innerHTML = '';
		config.toolButtons && config.toolButtons.forEach((tb: ToolButton) => {
			this.$toolButtons.appendChild(tb.getMainElement());
		});

		removeClassesByFunction(this.$topRecord.classList, className => className.startsWith("align-"));
		this.$topRecord.classList.add("align-" + config.topRecordAlignment);
		this.$topRecord.innerHTML = config.topRecord != null ? (config.topTemplate as Template).render(config.topRecord.values) : "";

		this.$htmlContainer.innerHTML = config.html != null ? removeDangerousTags(config.html) : "";

		if (config.imageUrls && config.imageUrls.length > 0) {
			for (var i = 0; i < this.config.imageUrls.length; i++) {
				const $image = new Image();
				let image = {
					width: this.minIdealImageHeight,
					height: this.minIdealImageHeight,
					$img: $image
				};
				this.images.push(image);
				$image.classList.add("image");
				$image.onload = (event: Event) => {
					image.width = (event.target as HTMLImageElement).naturalWidth;
					image.height = (event.target as HTMLImageElement).naturalHeight;
					this.reLayout();
				};
				$image.src = this.config.imageUrls[i];
				this.$images.appendChild($image);
			}
		}

	}

	reLayout() {
		if (this.images.length > 0) {
			let availableWidth = this.$images.clientWidth;
			let layout = fixed_partition(this.images, {
				containerWidth: availableWidth,
				idealElementHeight: Math.max(this.minIdealImageHeight, availableWidth / 3),
				align: 'center',
				spacing: 10
			});
			for (let i = 0; i < this.images.length; i++) {
				this.images[i].$img.style.left = layout.positions[i].x + "px";
				this.images[i].$img.style.top = layout.positions[i].y + "px";
				this.images[i].$img.style.width = layout.positions[i].width + "px";
				this.images[i].$img.style.height = layout.positions[i].height + "px";
			}
			this.$images.style.height = layout.height + "px";
		} else {
			this.$images.style.height = "0";
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
		// nothing to do
	}

}
