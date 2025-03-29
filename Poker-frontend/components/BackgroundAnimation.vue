<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useColorMode } from '@vueuse/core';

const props = defineProps({
  primaryColor: {
    type: Array,
    validator(value: unknown) {
      return Array.isArray(value) && value.length === 2 && value.every(item => typeof item === 'string');
    },
    required: false,
  },
  secondaryColor: {
    type: Array,
    validator(value: unknown) {
      return Array.isArray(value) && value.length === 2 && value.every(item => typeof item === 'string');
    },
    required: false,
  },
});

const colorMode = useColorMode();

const primaryColor = computed(() => {
  if (props.primaryColor) {
    return colorMode.value === 'dark' ? props.primaryColor[1] : props.primaryColor[0];
  }
  return colorMode.value === 'dark' ? '#0b3b28' : '#228760'; // Default colors
});

const secondaryColor = computed(() => {
  if (props.secondaryColor) {
    return colorMode.value === 'dark' ? props.secondaryColor[1] : props.secondaryColor[0];
  }
  return colorMode.value === 'dark' ? '#07261a' : '#15543c'; // Default colors
});

const rectangles = ref<{ x: number; y: number; color: string; textColor: string }[]>([]);
const rectSize = 300;
const fontSize = rectSize / 2;
const speed = 1; // Movement speed per frame
let columns = 0;
let rows = 0;
let animationFrameId: number | null = null;

function calculateGridSize() {
  columns = Math.ceil(window.innerWidth / rectSize) + 2; // Extra for wrap-around
  rows = Math.ceil(window.innerHeight / rectSize) + 2;
}

function createCheckerboard() {
  rectangles.value = [];
  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < columns; col++) {
      rectangles.value.push({
        x: col * rectSize,
        y: row * rectSize,
        color: (row + col) % 2 === 0 ? primaryColor.value : secondaryColor.value,
        textColor: ((row + col) % 2 === 0 ? primaryColor.value : secondaryColor.value) === primaryColor.value ? secondaryColor.value : primaryColor.value,
      });
    }
  }
}

function animate() {
  // Update the position of each rectangle
  for (const rect of rectangles.value) {
    rect.x += speed;
    rect.y -= speed;

    // Wrap-around logic
    if (rect.x >= (columns - 1) * rectSize) {
      rect.x -= columns * rectSize;
    }
    if (rect.y <= -rectSize) {
      rect.y += rows * rectSize;
    }
  }

  // Request the next frame for continuous animation
  animationFrameId = requestAnimationFrame(animate);
}

function handleResize() {
  cancelAnimationFrame(animationFrameId!);
  calculateGridSize();
  createCheckerboard();
  animate();
}

// Watch for color mode changes and update the colors accordingly
watch(() => colorMode.value, (newColorMode) => {
  primaryColor.value = newColorMode === 'dark' ? '#0b3b28' : '#228760';
  secondaryColor.value = newColorMode === 'dark' ? '#07261a' : '#15543c';
  createCheckerboard(); // Recreate the checkerboard with updated colors
});

onMounted(() => {
  calculateGridSize();
  createCheckerboard();
  animate();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  if (animationFrameId) cancelAnimationFrame(animationFrameId);
});
</script>

<template>
  <div class="background">
    <div
        v-for="(rect, index) in rectangles"
        :key="index"
        class="rectangle"
        :style="{
        width: rectSize + 'px',
        height: rectSize + 'px',
        backgroundColor: rect.color,
        transform: `translate(${rect.x}px, ${rect.y}px)`,
        color: rect.textColor,
      }"
    >
      <span class="rectangle-text" :style="{
        fontSize: fontSize + 'px',
        transform: 'translateY(' + -rectSize / 10 + 'px)'
      }">∑</span>

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
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  will-change: transform; /* Hint the browser to optimize for transform */
}


</style>
