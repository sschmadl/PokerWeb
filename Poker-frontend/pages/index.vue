<script setup lang="ts">
import PlayerStats from '~/components/PlayerStatMenu.vue' // or wherever it's located
import {useRequestURL} from "#app";

function onPlayPressed() {
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
      <PlayerStats
          :menu-width="300"
          :cards="cards"
          :player-action="'Raise: 500€'"
          :player-money="'1250'"
          :player-name="'Sigmabrand'"

      />
      <PlayerStats
          :menu-width="500"
          :cards="cards"
          :player-money="'2250'"
          :player-name="'Gazelle'"
      />
      <PlayerStats
          :menu-width="700"
          :cards="cards"
          :player-action="'Check'"
          :player-money="'10'"
          :player-name="'Weimarer'"
      />

      <UButton class="text-8xl py-4 px-20" variant="solid" @click="onPlayPressed">Play</UButton>
    </div>
  </div>

</template>

<style scoped>

</style>