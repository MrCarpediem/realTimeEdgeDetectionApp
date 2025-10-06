export class StatsOverlay {
    constructor(fpsEl, resEl) {
        this.fpsEl = fpsEl;
        this.resEl = resEl;
    }
    updateFPS(frameTimeMs) {
        const fps = (1000 / frameTimeMs).toFixed(1);
        this.fpsEl.textContent = fps;
    }
    updateResolution(width, height) {
        this.resEl.textContent = `${width}x${height}`;
    }
}
