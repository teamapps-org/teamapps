/**
 * Basic Problem: JS Floating Point Math is highly inaccurate.
 * Found this thread: https://stackoverflow.com/questions/11832914/how-to-round-to-at-most-2-decimal-places-if-necessary
 *
 * Best solution: Use a library with correct implementation of floating point math.
 *
 * - mathjs (CAUTION: not math.js!): https://www.npmjs.com/package/mathjs
 *   - is basically complete, but is FAT: 732 kB, 188 kB minified
 *   - see https://bundlephobia.com/package/mathjs@13.1.1
 *
 * - decimal.js: https://www.npmjs.com/package/decimal.js
 *   - still 31.1 kB, 12.3kB minified
 *   - good: last Publish in 2022 (today: 2024-09-09)
 *
 * - currency.js: https://www.npmjs.com/package/currency.js
 *   - 2.2kB, 1kB minified
 *   - no dependencies
 *   - BUT: only compatible with TS allowSyntheticDefaultImports: true + default import
 *
 * Second best solution: Use a custom implementation of roundToPrecision
 * bjesuiter selected (on 2024-09-09):
 * https://stackoverflow.com/questions/11832914/how-to-round-to-at-most-2-decimal-places-if-necessary#:~:text=Solution%202%3A%20purely%20mathematical%20(Number.EPSILON)
 *
 */

// Decimal round (half away from zero)
export function roundToPrecision(value: number, precision: number): number {
    const p = Math.pow(10, precision || 0);
    return Math.round(value * p) / p;
}

// Decimal ceil
export function ceilToPrecision(value: number, precision: number): number {
    const p = Math.pow(10, precision ?? 0);
    return Math.ceil(value * p) / p;
}

// Decimal floor
export function floorToPrecision(value: number, precision: number): number {
    const p = Math.pow(10, precision ?? 0);
    return Math.floor(value * p) / p;
}

// Decimal trunc
export function truncToPrecision(value: number, precision: number): number {
    if (value < 0) {
        return ceilToPrecision(value, precision);
    } else {
        return floorToPrecision(value, precision);
    }
}

// Format using fixed-point notation
export function toFixed(value: number, precision: number): string {
    return roundToPrecision(value, precision).toFixed(precision);
}