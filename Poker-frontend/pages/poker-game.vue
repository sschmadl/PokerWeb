<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref} from 'vue';
import ActionButtons from '~/components/action-buttons.vue';
import Chat from '~/components/Chat.vue';
import {useUsername} from "~/composables/states";

const gameSocket = useGameSocket();

const tableDiameter = ref(window.innerWidth / 2.5);
const playerWidth = ref(tableDiameter.value / 3.5);

const playerPositions = ref<{ x: number; y: number }[]>([]);

export type Card = {
  faceDown?: boolean;
  frontImage?: string;
  highlighted?: boolean;
};

export type Player = {
  name: string;
  credits: number;
};

export type PlayerInfo = Player & {
  action: string;
  cards: [Card, Card];
}


const players = ref<Player[]>([]);
const playerInfo = ref<PlayerInfo[]>([]);


const cards = ref([
  {frontImage: '/cards_default/TS.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TH.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TC.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/TD.svg', faceDown: false, highlighted: false},
  {frontImage: '/cards_default/AS.svg', faceDown: false, highlighted: false},
]);

function fetchProfilePictureUrl(name: string): string {
  return `/user-info/${name}/profile-picture`;
}


const cardHeight = ref(tableDiameter.value / 7);

function calculatePlayerPositions() {
  const radius = tableDiameter.value / 2;
  const numPlayers = players.value.length;

  const positions = [];
  for (let i = 0; i < numPlayers; i++) {
    const angle = (2 * Math.PI * i) / numPlayers - Math.PI / 2; // start at top center
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    positions.push({ x, y });
  }

  playerPositions.value = positions;
}

function updateSizes() {
  tableDiameter.value = window.innerWidth / 2.5;
  playerWidth.value = tableDiameter.value / 3.5;
  cardHeight.value = tableDiameter.value / 7;
  calculatePlayerPositions();
}

watch(players, () => {
  calculatePlayerPositions();
  playerInfo.value = players.value.map(player => ({
    name: player.name,
    credits: player.credits,
    action: '',
    cards: [{}, {}],
  }));
}, { deep: true });

async function fetchCurrentPlayers() {
  const message = {
    command: 'current-players-info'
  }
  gameSocket.sendMessage(JSON.stringify(message));
}

gameSocket.onMessage((data) => {
  console.log(data.command);
  switch(data.command) {
    case 'player-joined-game':
      players.value.push({name: data.name, credits: data.credits})
      break;
    case 'player-left':
      players.value = players.value.filter(player => player.name !== data.name);
      break;
    case 'current-players-info':
      const username = useUsername().value;
      const playerData = data.players as Player[];
      console.log('Player data: ', playerData);

      players.value = [
        ...playerData.filter(p => p.name === username),
        ...playerData.filter(p => p.name !== username),
      ];
      console.log('New Player data: ', players.value);
      break;
  }
});

onMounted(() => {
  // @ToDo Uncomment this in final version!!!
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
fetchCurrentPlayers();
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
          v-for="(player, index) in playerInfo"
          :key="index"
          class="players"
          :style="{
      position: 'absolute',
      left: playerPositions[index]?.x + 'px',
      top: playerPositions[index]?.y + 'px',
      transform: 'translate(-50%, -50%)',
    }"
      >
        <PlayerStatMenu
            :menu-width="playerWidth"
            :cards="player.cards ?? []"
            :player-name="player.name"
            :player-money="player.credits"
            :player-action="player.action"
            :profile-picture="fetchProfilePictureUrl(player.name)"
        />
      </div>
    </div>

  </div>
  <div class="chat-area">
    <!-- Chat component imported and displayed on the right -->
    <Chat/>
  </div>
  <div class="side_menu">
    <!-- Use the Action Buttons Component for Bottom Menu -->
    <ActionButtons :style="{zIndex: 40}" />
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
  z-index: 50;
  position: absolute;
  right: 0;
  bottom: 0;
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

.side_menu {
  position: absolute;
  bottom: 30%;
  left: 0;
  width: 100%;
  z-index: 50; /* higher than others */
  padding: 1rem 0;
}

</style>
