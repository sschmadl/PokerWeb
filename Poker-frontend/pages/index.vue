<script setup lang="ts">
import {useJWT, useUsername} from "~/composables/states";

function onPlayPressed() {
  const webSocket = new WebSocket('ws://127.0.0.1:8080'); // change this
  webSocket.onopen = async () => {
    console.log(webSocket.readyState);
  }

  navigateTo('lobby-selection')
}
</script>

<template>
  <div>
    <PokerSuitBackground/>
    <NavBar/>

    <div class="flex flex-col justify-center items-center text-9xl gap-10">
      <h1>Sigma Poker</h1>
      <UButton class="text-8xl py-4 px-20" variant="solid" @click="onPlayPressed">Play</UButton>
    </div>
  </div>
</template>

<style scoped>

</style>