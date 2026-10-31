import {selectGridRowsAndActivate} from "../modules/table/TableSelection";

function createTable(activeCell: { row: number, cell: number } | null, activatableColumns = [0, 1, 2]) {
	const grid = {
		setSelectedRows: jest.fn(),
		getActiveCell: jest.fn(() => activeCell),
		canCellBeActive: jest.fn((_row: number, column: number) => activatableColumns.includes(column)),
		getColumns: jest.fn(() => [{}, {}, {}]),
		setActiveCell: jest.fn()
	};
	return grid;
}

describe("UiTable programmatic row selection", () => {
	test("moves the active cell to a programmatically selected row", () => {
		const grid = createTable({row: 5, cell: 1});

		selectGridRowsAndActivate(grid, [2]);

		expect(grid.setSelectedRows).toHaveBeenCalledWith([2]);
		expect(grid.setActiveCell).toHaveBeenCalledWith(2, 1);
	});

	test("uses the first activatable column if the previous column is not activatable", () => {
		const grid = createTable({row: 5, cell: 2}, [1]);

		selectGridRowsAndActivate(grid, [2]);

		expect(grid.setActiveCell).toHaveBeenCalledWith(2, 1);
	});

	test("does not move the active cell for multiple row selection", () => {
		const grid = createTable({row: 5, cell: 1});

		selectGridRowsAndActivate(grid, [2, 3]);

		expect(grid.setSelectedRows).toHaveBeenCalledWith([2, 3]);
		expect(grid.setActiveCell).not.toHaveBeenCalled();
	});

	test("does not activate a row for an unknown record", () => {
		const grid = createTable({row: 5, cell: 1});

		selectGridRowsAndActivate(grid, [-1]);

		expect(grid.setSelectedRows).toHaveBeenCalledWith([-1]);
		expect(grid.setActiveCell).not.toHaveBeenCalled();
	});
});
