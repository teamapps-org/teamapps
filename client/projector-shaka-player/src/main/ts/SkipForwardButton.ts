export class SkipForwardButton extends shaka.ui.Element {

    private button:HTMLButtonElement;

    constructor(parent : HTMLElement , controls : shaka.ui.Controls) {
        super(parent, controls);

        this.button = document.createElement('button');
        this.button.classList.add('material-icons-round');
        this.button.classList.add('shaka-skip-button', 'shaka-skip-forward-button');
        this.button.textContent = "skip_next";
        this.parent.appendChild(this.button);
        this.button.setAttribute("aria-label", "Skip forward");

        this.eventManager.listen(this.button, 'click', () => {
            // nothing to do here... (this must be handled elsewhere)
        });
    }
}

export class SkipForwardButtonFactory {
    create(rootElement : HTMLElement , controls : shaka.ui.Controls) {
        return new SkipForwardButton(rootElement, controls);
    }
}

shaka.ui.Controls.registerElement('skip_forward', new SkipForwardButtonFactory());
