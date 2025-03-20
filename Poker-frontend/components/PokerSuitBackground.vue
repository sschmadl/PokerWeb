<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const rectangles = ref<{ width: number; height: number; color: string; textColor: string, x: number; y: number; }[]>([]);
const rect_size = 100;
const font_size = rect_size / 2.5;
const speed = 1;
let rect_amount = calculateRectAmount();

function calculateRectAmount() {
  return window.innerHeight > window.innerWidth ?
      Math.ceil(window.innerHeight / rect_size) + 3 :
      Math.ceil(window.innerWidth / rect_size) + 3;
}

function createRectangles() {
  rectangles.value = [];
  for (let i = 0; i < rect_amount; i++) {
    for (let j = 0; j < rect_amount; j++) {
      const color = calculateColor(i, j);
      const textColor = calculateColor(i, j + 1);
      rectangles.value.push({
        width: rect_size,
        height: rect_size,
        color: color,
        textColor: textColor,
        x: rect_size * j,
        y: rect_size * i,
      });
    }
  }
}

function calculateColor(i: number, j: number) {
  return (i + j) % 2 === 0 ? '#071f0d' : '#083012';
}

function animate() {
  for (let idx = 0; idx < rectangles.value.length; idx++) {
    const rect = rectangles.value[idx];
    rect.x += speed;
    rect.y -= speed;

    // Round the x and y positions to avoid subpixel rendering
    rect.x = Math.round(rect.x);
    rect.y = Math.round(rect.y);

    // Wrap around logic
    if (rect.x > window.innerWidth) {
      rect.x -= rect_amount * rect_size;
      const gridY = Math.floor(rect.y / rect_size);
      const gridX = Math.floor(rect.x / rect_size);
      rect.color = calculateColor(gridY, gridX);
      rect.textColor = calculateColor(gridY, gridX + 1);
    }
    if (rect.y < -rect_size) {
      rect.y += rect_amount * rect_size;
      const gridY = Math.floor(rect.y / rect_size);
      const gridX = Math.floor(rect.x / rect_size);
      rect.color = calculateColor(gridY, gridX);
      rect.textColor = calculateColor(gridY, gridX + 1);
    }
  }

  requestAnimationFrame(animate);
}

function handleResize() {
  rect_amount = calculateRectAmount();
  createRectangles();
}

onMounted(() => {
  createRectangles();
  animate();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<template>
  <div class="background" style="display: inline-flex">
    <div
        v-for="(rect, index) in rectangles"
        :key="index"
        class="rectangle"
        :style="{
        width: rect.width + 'px',
        height: rect.height + 'px',
        backgroundColor: rect.color,
        left: rect.x + 'px',
        top: rect.y + 'px',
        fontSize: font_size + 'px',
        color: rect.textColor,
      }"
    >∑
    </div>
  </div>
</template>

<style scoped>
.background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -10;
  pointer-events: none;
  overflow: hidden;
}

.rectangle {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}
</style>