const fs = require('fs');
const path = require('path');
const less = require('less');
const postcss = require('postcss');

const roles = ['primary', 'info', 'success', 'warning', 'danger', 'neutral'];
let palettes;

beforeAll(async () => {
    const root = path.resolve(__dirname, '../../less');
    const file = path.join(root, 'teamapps.less');
    const css = postcss.parse((await less.render(fs.readFileSync(file, 'utf8'), {filename: file})).css);
    palettes = [':root', '.theme-dark'].map(selector => {
        const rule = css.nodes.find(node => node.type === 'rule' && node.selector === selector);
        return Object.fromEntries(rule.nodes.filter(n => n.type === 'decl').map(n => [n.prop, n.value]));
    });
});

function luminance(hex) {
    if (hex.length === 4) hex = '#' + [...hex.slice(1)].map(c => c + c).join('');
    const channels = hex.slice(1).match(/../g).map(c => parseInt(c, 16) / 255)
        .map(c => c <= .04045 ? c / 12.92 : Math.pow((c + .055) / 1.055, 2.4));
    return channels.reduce((sum, c, i) => sum + c * [.2126, .7152, .0722][i], 0);
}

test('the standalone client supplies complete light and dark status palettes', () => {
    for (const palette of palettes) {
        for (const role of roles) {
            for (const part of ['text', 'text-emphasis', 'bg', 'border']) {
                expect(palette[`--ta-state-${role}-${part}`]).toBeTruthy();
            }
        }
    }
});

test('informational status text reaches 4.5:1 on its paired surfaces', () => {
    for (const palette of palettes) {
        for (const role of roles.filter(role => role !== 'primary')) {
            const background = luminance(palette[`--ta-state-${role}-bg`]);
            for (const part of ['text', 'text-emphasis']) {
                const foreground = luminance(palette[`--ta-state-${role}-${part}`]);
                const contrast = (Math.max(foreground, background) + .05) / (Math.min(foreground, background) + .05);
                expect(contrast).toBeGreaterThanOrEqual(4.5);
            }
        }
    }
});
