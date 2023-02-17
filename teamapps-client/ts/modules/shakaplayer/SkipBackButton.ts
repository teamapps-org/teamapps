export class SkipBackButton extends shaka.ui.Element {

  private button:HTMLButtonElement;

  constructor(parent : HTMLElement , controls : shaka.ui.Controls) {
    super(parent, controls);

    this.button = document.createElement('button');
    this.button.classList.add('material-icons-round');
    this.button.classList.add('shaka-skip-button', 'shaka-skip-back-button');
    this.button.textContent = "skip_previous";
    this.parent.appendChild(this.button);
    this.button.setAttribute("aria-label", "Skip back");

    this.eventManager.listen(this.button, 'click', () => {
      this.skipBack();
    });
  }

  skipBack() {
    if (!this.video.duration) {
      return;
    }
    this.player.getMediaElement().currentTime = 0;
  }
}

export class SkipBackButtonFactory {
  create(rootElement : HTMLElement , controls : shaka.ui.Controls) {
    return new SkipBackButton(rootElement, controls);
  }
}

shaka.ui.Controls.registerElement('skip_back', new SkipBackButtonFactory());
