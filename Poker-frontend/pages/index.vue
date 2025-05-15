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
      <h1 class="title-text">∑ Pσker</h1>
      <UButton class="play-button text-8xl text-center py-10 px-20" variant="solid" @click="onPlayPressed">Play</UButton>
    </div>
  </div>

</template>

<style scoped>

.title-text {
  font-size: 12rem;
  font-weight: bold;
  font-family: 'Noto Sans', 'Segoe UI', sans-serif;
  color: #fff;
  text-shadow:
      4px 4px 6px rgba(0, 0, 0, 0.7),
      0 0 20px #438061;
  letter-spacing: 2px;
  animation: pulse-glow 2s infinite alternate;
}


</style>