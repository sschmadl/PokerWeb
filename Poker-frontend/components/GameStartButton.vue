<script setup lang="ts">
import {useGameSocket} from "~/stores/useGameSocket";

const gameSocket = useGameSocket();

function sendStartGameRequest() {
  const message = {
    command: 'game-start',
  }
  gameSocket.sendMessage(JSON.stringify(message));
}
</script>

<template>
  <UButton class="start-button text-4xl" @click="sendStartGameRequest">
    Start Game
  </UButton>
</template>

<style scoped>

</style>