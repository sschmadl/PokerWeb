<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useColorMode } from '@vueuse/core'

const props = defineProps({
  primaryColor: {
    type: Array as () => [string, string],
    required: false,
  },
  secondaryColor: {
    type: Array as () => [string, string],
    required: false,
  },
})

const colorMode = useColorMode()

const primaryColor = computed(() => {
  if (props.primaryColor) {
    return colorMode.value === 'dark' ? props.primaryColor[1] : props.primaryColor[0]
  }
  return colorMode.value === 'dark' ? '#0b3b28' : '#228760'
})

const secondaryColor = computed(() => {
  if (props.secondaryColor) {
    return colorMode.value === 'dark' ? props.secondaryColor[1] : props.secondaryColor[0]
  }
  return colorMode.value === 'dark' ? '#07261a' : '#15543c'
})

// Checkerboard setup
const rectangles = ref<{ x: number; y: number; color: string; textColor: string }[]>([])
const rectSize = 300
const fontSize = rectSize / 2
const speed = 1
let columns = 0
let rows = 0
let animationFrameId: number | null = null

function calculateGridSize() {
  columns = Math.ceil(window.innerWidth / rectSize) + 2
  rows = Math.ceil(window.innerHeight / rectSize) + 2
}

function createCheckerboard() {
  rectangles.value = []
  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < columns; col++) {
      const isPrimary = (row + col) % 2 === 0
      rectangles.value.push({
        x: col * rectSize,
        y: row * rectSize,
        color: isPrimary ? primaryColor.value : secondaryColor.value,
        textColor: isPrimary ? secondaryColor.value : primaryColor.value,
      })
    }
  }
}

function animateCheckerboard() {
  for (const rect of rectangles.value) {
    rect.x += speed
    rect.y -= speed

    if (rect.x >= (columns - 1) * rectSize) {
      rect.x -= columns * rectSize
    }
    if (rect.y <= -rectSize) {
      rect.y += rows * rectSize
    }
  }
  animationFrameId = requestAnimationFrame(animateCheckerboard)
}

function handleResize() {
  if (animationFrameId) cancelAnimationFrame(animationFrameId)
  calculateGridSize()
  createCheckerboard()
  animateCheckerboard()
}

watch(
    () => colorMode.value,
    () => {
      createCheckerboard()
    }
)

// Sigma floating sigmas setup
const suits = ['♠', '♥', '♦', '♣']

function randomSuit() {
  return suits[Math.floor(Math.random() * suits.length)]
}

const sigmas = ref<
    {
      size: number
      suit: string
      startX: number
      startY: number
      currentX: number
      currentY: number
      ease: number
      rotation: number
      rotationSpeed: number
    }[]
>([])

const mouseX = ref(window.innerWidth / 2)
const mouseY = ref(window.innerHeight / 2)
const attract = ref(true)

function onMouseMove(event: MouseEvent) {
  mouseX.value = event.clientX
  mouseY.value = event.clientY
}

function onTouchMove(event: TouchEvent) {
  if (event.touches && event.touches.length > 0) {
    mouseX.value = event.touches[0].clientX
    mouseY.value = event.touches[0].clientY
  }
}

function toggleAttraction() {
  attract.value = !attract.value
}

function createSigma() {
  const size = 70 + Math.random() * 50
  const startX = Math.random() * window.innerWidth
  const startY = Math.random() * window.innerHeight
  const rotationSpeed = (Math.random() - 0.5) * 1.2

  return {
    size,
    suit: randomSuit(),
    startX,
    startY,
    currentX: startX,
    currentY: startY,
    ease: 0.009 + Math.random() * 0.009,
    rotation: Math.random() * 360,
    rotationSpeed,
  }
}

function animateSigmas() {
  for (const sigma of sigmas.value) {
    if (attract.value) {
      const distX = mouseX.value - sigma.currentX
      const distY = mouseY.value - sigma.currentY
      const distance = Math.sqrt(distX * distX + distY * distY)

      if (distance <= 400) {
        sigma.currentX += distX * sigma.ease
        sigma.currentY += distY * sigma.ease
      } else {
        const backDistX = sigma.startX - sigma.currentX
        const backDistY = sigma.startY - sigma.currentY
        sigma.currentX += backDistX * (sigma.ease / 2)
        sigma.currentY += backDistY * (sigma.ease / 2)
      }
    } else {
      const backDistX = sigma.startX - sigma.currentX
      const backDistY = sigma.startY - sigma.currentY
      sigma.currentX += backDistX * (sigma.ease / 3)
      sigma.currentY += backDistY * (sigma.ease / 3)
    }

    sigma.rotation += sigma.rotationSpeed
    if (sigma.rotation >= 360) sigma.rotation -= 360
    else if (sigma.rotation < 0) sigma.rotation += 360
  }
  requestAnimationFrame(animateSigmas)
}

onMounted(() => {
  calculateGridSize()
  createCheckerboard()
  animateCheckerboard()

  sigmas.value = Array.from({length: 100}, () => createSigma())
  animateSigmas()

  window.addEventListener('resize', handleResize)
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('touchmove', onTouchMove)
  window.addEventListener('click', toggleAttraction)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('touchmove', onTouchMove)
  window.removeEventListener('click', toggleAttraction)
  if (animationFrameId) cancelAnimationFrame(animationFrameId)
})
</script>

<template>
  <div class="background">
    <!-- Checkerboard rectangles -->
    <div
        v-for="(rect, index) in rectangles"
        :key="'rect-' + index"
        class="rectangle"
        :style="{
        width: rectSize + 'px',
        height: rectSize + 'px',
        backgroundColor: rect.color,
        color: rect.textColor,
        transform: `translate(${rect.x}px, ${rect.y}px)`,
        fontSize: fontSize + 'px',
      }"
    >
      <span class="rectangle-text" :style="{ transform: 'translateY(' + -rectSize / 10 + 'px)' }">∑</span>
    </div>

    <!-- Floating Sigmas -->
    <svg
        v-for="(sigma, index) in sigmas"
        :key="'sigma-' + index"
        class="sigma"
        :style="{
        width: sigma.size + 'px',
        height: sigma.size + 'px',
        position: 'absolute',
        top: (sigma.currentY - sigma.size / 2) + 'px',
        left: (sigma.currentX - sigma.size / 2) + 'px',
        opacity: 0.6,
        strokeWidth: 6,
        animation: 'none',
        transform: `rotate(${sigma.rotation}deg)`
      }"
        viewBox="0 0 100 100"
        xmlns="http://www.w3.org/2000/svg"
        aria-hidden="true"
    >
      <!-- Sigma symbol -->
      <text
          x="50"
          y="55"
          font-size="90"
          fill="currentColor"
          text-anchor="middle"
          dominant-baseline="middle"
          style="font-family: 'Times New Roman', serif;"
      >
        Σ
      </text>
      <!-- Random card suit inside Sigma -->
      <text
          x="50"
          y="55"
          font-size="40"
          fill="#e63946"
          text-anchor="middle"
          dominant-baseline="middle"
          style="font-family: 'Segoe UI Symbol', 'Arial Unicode MS', Arial, sans-serif;"
      >
        {{ sigma.suit }}
      </text>
    </svg>
  </div>
</template>

<style scoped>
.background {
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: -10;
  background-color: #121212;
  cursor: crosshair;
  user-select: none;
}

.rectangle {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  will-change: transform;
  pointer-events: none;
  user-select: none;
}

.sigma {
  user-select: none;
  pointer-events: none;
  color: #fff;
  z-index: 10;
}
</style>
