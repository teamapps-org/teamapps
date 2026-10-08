/** Opt-in completed recordings only. Never store microphone data before the user records. */
export interface AudioRecordingDraft {
    id: string;
    blob: Blob;
    extension: string;
    settings: any;
}
export class AudioRecordingDraftStore {
    constructor(private key: string) {}
    private open(): Promise<IDBDatabase> {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open("teamapps-audio-drafts", 1);
            request.onupgradeneeded = () => request.result.createObjectStore("recordings");
            request.onsuccess = () => resolve(request.result);
            request.onerror = () => reject(request.error);
            request.onblocked = () => reject(new Error("Draft database is blocked"));
        });
    }
    async get(): Promise<AudioRecordingDraft> {
        const db = await this.open();
        try { return await new Promise<AudioRecordingDraft>((resolve, reject) => {
            const tx = db.transaction("recordings", "readonly");
            const request = tx.objectStore("recordings").get(this.key);
            tx.oncomplete = () => resolve(request.result);
            tx.onerror = tx.onabort = () => reject(tx.error || new Error("Cannot read draft"));
        }); } finally { db.close(); }
    }
    async put(draft: AudioRecordingDraft): Promise<void> {
        const db = await this.open();
        try { await new Promise<void>((resolve, reject) => {
            const tx = db.transaction("recordings", "readwrite");
            const store = tx.objectStore("recordings");
            const request = store.get(this.key);
            request.onsuccess = () => {
                // A second tab must not silently overwrite an existing recording.
                if (request.result && request.result.id !== draft.id) tx.abort();
                else store.put(draft, this.key);
            };
            tx.oncomplete = () => resolve();
            tx.onerror = tx.onabort = () => reject(tx.error || new Error("Another draft already exists"));
        }); } finally { db.close(); }
    }
    async remove(id: string): Promise<void> {
        const db = await this.open();
        try { await new Promise<void>((resolve, reject) => {
            const tx = db.transaction("recordings", "readwrite");
            const store = tx.objectStore("recordings");
            const request = store.get(this.key);
            request.onsuccess = () => { if (request.result?.id === id) store.delete(this.key); };
            tx.oncomplete = () => resolve();
            tx.onerror = tx.onabort = () => reject(tx.error || new Error("Cannot clear draft"));
        }); } finally { db.close(); }
    }
}
