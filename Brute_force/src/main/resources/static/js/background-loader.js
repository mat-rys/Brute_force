/**
 * BACKGROUND BENCHMARK LOADER - MODUŁ KESZOWANIA DANYCH W TLE
 * Pozwala na pobranie dużych zbiorów wyników (JSON) zaraz po starcie aplikacji
 * i zapisanie ich w IndexedDB, aby zakładka "Porównanie środowisk" działała błyskawicznie.
 */

const BackgroundBenchmarkLoader = {
    dbName: 'BruteForceBenchmarkCache',
    storeName: 'benchmarks',
    version: 1,
    files: ['/data/external/benchmark_results_pc1', '/data/external/benchmark_results_pc3'],

    /**
     * Inicjalizacja bazy danych IndexedDB
     */
    async initDB() {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open(this.dbName, this.version);
            
            request.onupgradeneeded = (event) => {
                const db = event.target.result;
                if (!db.objectStoreNames.contains(this.storeName)) {
                    db.createObjectStore(this.storeName);
                }
            };

            request.onsuccess = () => resolve(request.result);
            request.onerror = (e) => reject(e);
        });
    },

    /**
     * Pobranie danych z cache
     */
    async getFromCache(key) {
        try {
            const db = await this.initDB();
            return new Promise((resolve) => {
                const transaction = db.transaction(this.storeName, 'readonly');
                const store = transaction.objectStore(this.storeName);
                const request = store.get(key);
                
                request.onsuccess = () => resolve(request.result);
                request.onerror = () => resolve(null);
            });
        } catch (e) {
            console.warn('[BackgroundLoader] IndexedDB nie jest dostępny:', e);
            return null;
        }
    },

    /**
     * Zapisanie danych do cache
     */
    async saveToCache(key, data) {
        try {
            const db = await this.initDB();
            return new Promise((resolve, reject) => {
                const transaction = db.transaction(this.storeName, 'readwrite');
                const store = transaction.objectStore(this.storeName);
                const request = store.put(data, key);
                
                request.onsuccess = () => resolve();
                request.onerror = (e) => reject(e);
            });
        } catch (e) {
            console.error('[BackgroundLoader] Błąd zapisu do IndexedDB:', e);
        }
    },

    /**
     * Proces ładowania w tle
     */
    async startPreloading() {
        console.log('%c[Laboratory] Rozpoczynanie wstępnego ładowania danych badawczych...', 'color: #007bff; font-weight: bold;');
        
        for (const file of this.files) {
            try {
                const cached = await this.getFromCache(file);
                if (!cached) {
                    console.log(`[BackgroundLoader] Pobieranie: ${file}...`);
                    const response = await fetch(file);
                    if (response.ok) {
                        const data = await response.json();
                        await this.saveToCache(file, data);
                        console.log(`%c[BackgroundLoader] Pomyślnie skeszowano: ${file}`, 'color: #28a745;');
                    }
                } else {
                    console.log(`[BackgroundLoader] Dane ${file} są już dostępne w lokalnym cache.`);
                }
            } catch (e) {
                console.error(`[BackgroundLoader] Nie udało się pobrać ${file}:`, e);
            }
        }
    }
};

// Inicjalizacja ładowania w tle (nie blokuje renderowania UI)
if (typeof window !== 'undefined') {
    window.addEventListener('load', () => {
        // Opóźnienie startu, aby dać pierwszeństwo krytycznym zasobom strony
        const delay = window.requestIdleCallback ? 2000 : 5000;
        
        if (window.requestIdleCallback) {
            setTimeout(() => {
                window.requestIdleCallback(() => BackgroundBenchmarkLoader.startPreloading());
            }, delay);
        } else {
            setTimeout(() => BackgroundBenchmarkLoader.startPreloading(), delay);
        }
    });
}
