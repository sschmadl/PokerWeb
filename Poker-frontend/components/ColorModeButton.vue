<script setup lang="ts">
import { useColorMode } from '@vueuse/core'

const colorMode = useColorMode()

const moonIcon = 'solar:moon-bold'
const sunIcon = 'ph:sun-fill'

// Automatically change the icon based on the color mode
const icon = computed(() => colorMode.value === 'dark' ? moonIcon : sunIcon)

const toggleDarkMode = () => {
  colorMode.value = colorMode.value === 'dark' ? 'light' : 'dark'
}
</script>

<template>
  <UButton class="rounded-full" :icon="icon" @click="toggleDarkMode" />
</template>
