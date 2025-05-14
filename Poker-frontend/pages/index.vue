<script setup lang="ts">

function onPlayPressed() {
  navigateTo('lobby-selection');
}

</script>

<template>
  <div>
    <BackgroundAnimation/>
    <NavBar/>
    <div class="flex flex-col justify-center items-center text-9xl gap-10">
      <h1>∑ - Pσker</h1>
      <UButton class="text-8xl py-4 px-20" variant="solid" @click="onPlayPressed">Play</UButton>
    </div>
  </div>

</template>

<style scoped>

</style>