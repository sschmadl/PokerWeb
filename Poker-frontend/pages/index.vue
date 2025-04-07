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

function flip() {
  faceDown.value = !faceDown.value;
  console.log('flip')

  cards.value = [
    { frontImage: '/cards_default/6S.svg', faceDown: false, highlighted: false },
    { frontImage: '/cards_default/9C.svg', faceDown: false, highlighted: false },
  ];
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
    <Card :front-image="'/cards_default/AS.svg'" :face-down="faceDown" :height="200" :highlighted="true"></Card>
  </div>
  <button @click="flip">Flip</button>
  <PlayerStatMenu :menu-width="200" :cards="cards" />
  <br><br><br><br>
  <PlayerStatMenu :menu-width="400" :profile-border-color="'#00FF00'" :cards="cards" />
  <br><br><br><br><br><br><br><br><br><br><br><br>
  <PlayerStatMenu :menu-width="600" :cards="cards" />
  <br><br><br><br>
</template>

<style scoped>

</style>