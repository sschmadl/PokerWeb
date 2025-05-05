<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import ActionButtons from '~/components/action-buttons.vue'; // Import the action buttons component
import Chat from '~/components/Chat.vue';
import {navigateTo} from "#app"; // Import the Chat component

const gameSocket = useGameSocket();

// Poker game logic
const tableWidth = ref(window.innerWidth / 2.5);
const tableHeight = ref(window.innerHeight / 2);

const cards = ref([
  { frontImage: '/cards_default/TS.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/TH.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/TC.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/TD.svg', faceDown: false, highlighted: false },
  { frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false },
]);

const cardHeight = ref(tableHeight.value / 3);

function updateSizes() {
  tableWidth.value = window.innerWidth / 2.5;
  tableHeight.value = window.innerHeight / 2;
  cardHeight.value = tableHeight.value / 3;
}

onMounted(() => {
  if (!gameSocket.isConnected) {
    navigateTo('/lobby-selection');
  }
  window.addEventListener('resize', updateSizes);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateSizes);
  gameSocket.disconnect();
});

function flipCards() {
  cards.value.forEach((card) => {
    card.faceDown = !card.faceDown;
  });
}
</script>

<template>
  <div class="game-container">
    <PokerhandInfoButton class="absolute top-0 right-0 m-5 z-20" />
    <!-- Poker Table Wrapper -->
    <div class="table-wrapper">
      <div class="poker-table-container">
        <div class="left-edge rounded-full poker-table" :style="{
          width: tableHeight + 'px',
          height: tableHeight + 'px',
          marginRight: tableWidth + 'px'
        }"></div>

        <div class="middle-part poker-table" :style="{
          width: tableWidth + 'px',
          height: tableHeight + 'px',
        }"></div>

        <div class="right-edge rounded-full poker-table" :style="{
          width: tableHeight + 'px',
          height: tableHeight + 'px',
          marginLeft: tableWidth + 'px',
        }"></div>

        <div class="community-cards">
          <Card
              v-for="(card, index) in cards"
              :key="index"
              :face-down="card.faceDown"
              :front-image="card.frontImage"
              :height="cardHeight"
              :highlighted="card.highlighted"
          />
        </div>

        <UButton @click="flipCards" :style="{ zIndex: 20 }">Flip</UButton>
      </div>
    </div>

    <div class="chat-area">
    <!-- Chat component imported and displayed on the right -->
    <Chat />
    </div>
    <div class="bottom-menu">
      <!-- Use the Action Buttons Component for Bottom Menu -->
      <ActionButtons />
    </div>
  </div>
</template>

<style scoped>
.game-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 100vh;
  padding: 1rem;
}
.chat-area {
  z-index: 15;

}

.table-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.poker-table-container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.poker-table {
  background: #35654D;
  position: absolute;
}

.left-edge,
.right-edge {
  border-radius: 50%;
  z-index: -1;
}

.community-cards {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  user-select: none;
  pointer-events: none;
}

.bottom-menu {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  padding: 1rem 0;
}
</style>
