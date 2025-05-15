<script setup lang="ts">
import { useSettingsStore } from '@/stores/settings'
import { computed } from 'vue'

const settings = useSettingsStore()

// Create a computed getter/setter to bind with the slider
const sigmaCount = computed({
  get: () => settings.sigmaCount,
  set: (val: number) => {
    settings.sigmaCount = val
  }
})
</script>

<template>
  <NavBar />
  <UCard>
    <h1>Sigma Settings</h1>
    <label for="sigmaSlider">Number of Sigmas: {{ sigmaCount }}</label>
    <input
        id="sigmaSlider"
        type="range"
        min="10"
        max="200"
        v-model="sigmaCount"
        step="1"
    />
  </UCard>
</template>

<style scoped>
label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
}
input[type="range"] {
  width: 100%;
  cursor: pointer;
}
</style>
