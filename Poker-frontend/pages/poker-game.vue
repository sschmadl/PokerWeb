<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref} from 'vue';
import ActionButtons from '~/components/action-buttons.vue';
import Chat from '~/components/Chat.vue';

const gameSocket = useGameSocket();

const tableDiameter = ref(window.innerWidth / 2.5);
const playerWidth = ref(tableDiameter.value / 3.5);

const playerPositions = ref<{ x: number; y: number }[]>([]);

function calculatePlayerPositions() {
  const radius = tableDiameter.value / 2;

  const positions = [];
  for (let i = 0; i < 10; i++) {
    const angle = (2 * Math.PI * i) / 10 - Math.PI / 2; // top-centered
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    positions.push({ x, y });
  }

  playerPositions.value = positions;
}



let dummyCards = ref([
  {frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/AC.svg', faceDown: false, highlighted: false},
]);

const cards = ref([
  {frontImage: '/cards_default/TS.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TH.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TC.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TD.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false},
]);

const cardHeight = ref(tableDiameter.value / 7);

function updateSizes() {
  tableDiameter.value = window.innerWidth / 2.5;
  playerWidth.value = tableDiameter.value / 3.5;
  cardHeight.value = tableDiameter.value / 7;
  calculatePlayerPositions();
}


onMounted(() => {
  // if (!gameSocket.isConnected) {
  //   navigateTo('/lobby-selection');
  // }
  updateSizes();
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
    <PokerhandInfoButton class="absolute top-0 right-0 m-5 z-20"/>
    <div class="table-wrapper">
      <div class="poker-table-container">


        <div class="poker-table rounded-full" :style="{
          width: tableDiameter + 'px',
          height: tableDiameter + 'px',
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
    <div class="player-wrapper">
      <div
          v-for="(pos, index) in playerPositions"
          :key="index"
          class="players"
          :style="{
            position: 'absolute',
            left: pos.x + 'px',
            top: pos.y + 'px',
            transform: 'translate(-50%, -50%)',
          }"
      >
        <PlayerStatMenu
            :menu-width="playerWidth"
            :cards="dummyCards"
        />
      </div>


    </div>
    <div class="chat-area">
      <!-- Chat component imported and displayed on the right -->
      <Chat/>
    </div>
    <div class="bottom-menu">
      <!-- Use the Action Buttons Component for Bottom Menu -->
      <ActionButtons :style="{zIndex: 40}" />
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
.player-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  transform: translate(-50%, -50%);
  z-index: 30;
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

.community-cards {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  user-select: none;
  pointer-events: none;
}

.chat-area {
  position: fixed; /* important */
  right: 1rem;
  top: 1rem;
  z-index: 50; /* higher than everything else */
}

.bottom-menu {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 50; /* higher than others */
  padding: 1rem 0;
}

</style>
