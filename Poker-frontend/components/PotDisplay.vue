<script setup lang="ts">
import { ref } from 'vue';

const gameSocket = useGameSocket();

const sidePotVisible = ref(true);
const potCredits = ref(0);
const sidePotCredits = ref(0);

gameSocket.onMessage((data) => {
  switch (data.command) {
    case 'new-betting-round': {
      potCredits.value = data.pot;
    }
  }
});
</script>

<template>
  <div class="main-wrapper">
    <div
        class="pot-container item-container bg-customPrimary-300 dark:bg-customPrimary-600"
        :class="{ 'rounded-bottom': !sidePotVisible }"
    >
      <strong>Pot</strong>
      <span>{{ potCredits }}</span>
    </div>
    <div
        class="side-pot-container item-container bg-customPrimary-200 dark:bg-customPrimary-500"
        :class="{ invisible: !sidePotVisible }"
    >
      <strong>Side Pot</strong>
      <span>{{ sidePotCredits }}</span>
    </div>
  </div>
</template>

<style scoped>
.item-container {
  text-align: center;
  display: flex;
  font-size: 30px;
  line-height: 0.95;
  flex-direction: column;
  padding: 0.5em;
}

.main-wrapper {
  display: flex;
  flex-direction: column;
}

.side-pot-container {
  border-bottom-left-radius: 0.2em;
  border-bottom-right-radius: 0.2em;
}

.pot-container {
  border-top-left-radius: 0.2em;
  border-top-right-radius: 0.2em;
}

/* Dynamically added when side pot is hidden */
.rounded-bottom {
  border-bottom-left-radius: 0.2em;
  border-bottom-right-radius: 0.2em;
}

.invisible {
  visibility: hidden;
}
</style>
