<script setup lang="ts">
import { ref, onMounted } from 'vue'

const colorMode = useColorMode()
const icon = ref('solar:moon-bold')

const moonIcon = 'solar:moon-bold'
const sunIcon = 'ph:sun-fill'

onMounted(() => {
  icon.value = colorMode.value === 'dark' ? moonIcon : sunIcon
})

const toggleDarkMode = () => {
  colorMode.value = colorMode.value === 'dark' ? 'light' : 'dark'
  icon.value = colorMode.value === 'dark' ? moonIcon : sunIcon
}
</script>

<template>
  <UButton class="rounded-full" :icon="icon" @click="toggleDarkMode" />
</template>
