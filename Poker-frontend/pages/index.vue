<script setup lang="ts">

function onPlayPressed() {
  const webSocket = new WebSocket('ws://127.0.0.1:8080'); // change this
  webSocket.onopen = async () => {
    console.log(webSocket.readyState);
  }

  navigateTo('lobby-selection')
}

let faceDown = ref(true);

function flip() {
  faceDown.value = !faceDown.value;
  console.log('flip')
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
    <Card :front-image="'/cards_default/AS.svg'" :face-down="faceDown" :height="200"></Card>
  </div>
  <button @click="flip">Flip</button>
  <PlayerStatMenu :menu-width="200" />
  <br>
  <PlayerStatMenu :menu-width="400" />
  <br>
  <PlayerStatMenu :menu-width="600" />
  <br>
</template>

<style scoped>

</style>