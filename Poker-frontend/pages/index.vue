<script setup lang="ts">

import {Card} from "#components";

function onPlayPressed() {
  const webSocket = new WebSocket('ws://127.0.0.1:8080'); // change this
  webSocket.onopen = async () => {
    console.log(webSocket.readyState);
  }

  navigateTo('lobby-selection')
}

let faceDown = ref(true);

let cards = ref([
  { frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/AC.svg', faceDown: false, highlighted: false },
]);

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