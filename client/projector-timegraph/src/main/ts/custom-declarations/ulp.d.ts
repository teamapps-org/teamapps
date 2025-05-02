declare module "ulp" {

    let ulp: {
        nextUp: (x: number) => number;
        nextDown: (x: number) => number;
        ulp: (x: number) => number;
        monkeypatch: () => void;
    };
    export = ulp;

}
