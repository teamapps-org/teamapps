
declare module "image-layout"  {
	export function fixed_partition(images: {width: number, height: number}[], options: {
		spacing: number,
		containerWidth: number,
		idealElementHeight: number,
		align?: 'center'
	}): {
		width: number,
		height: number,
		positions: {
			x: number,
			y: number,
			width: number,
			height: number
		}[]
	}
}