import { StatsOverlay } from './utils/stats.js';


const frameEl = document.getElementById("frame") as HTMLImageElement;
const fpsEl = document.getElementById("fps") as HTMLElement;
const resolutionEl = document.getElementById("resolution") as HTMLElement;

const frames: string[] = [];

// Match your actual filenames — update count if you have more
for (let i = 1; i <= 2; i++) {
  frames.push(`frames/frame_00${i}.jpeg`);
}



let index = 0;
const overlay = new StatsOverlay(fpsEl, resolutionEl);

function nextFrame() {
  const start = performance.now();
  frameEl.src = frames[index]!;
  index = (index + 1) % frames.length;

  frameEl.onload = () => {
    overlay.updateResolution(frameEl.naturalWidth, frameEl.naturalHeight);
    overlay.updateFPS(performance.now() - start);
  };
}

setInterval(nextFrame, 100);
