<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref} from 'vue';
import ActionButtons from '~/components/action-buttons.vue';
import Chat from '~/components/Chat.vue';
import {useUsername} from "~/composables/states";
import {navigateTo} from "#app";
import {getCardByString} from "~/utils/getCardByString";

const gameSocket = useGameSocket();
const selfUsername = useUsername().value;
let gameRunning = ref(false);

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
  highlighted: boolean;
  admin: boolean;
}

const playerInfo = ref<PlayerInfo[]>([]);

const playerCards = ref<Card[]>([
  {faceDown: true, highlighted: false,},
  {faceDown: true, highlighted: false,},
]);

const cards = ref<Card[]>([
  {faceDown: true, highlighted: false},
  {faceDown: true, highlighted: false},
  {faceDown: true, highlighted: false},
  {faceDown: true, highlighted: false},
  {faceDown: true, highlighted: false},
]);

function fetchProfilePictureUrl(name: string): string {
  return `/user-info/${name}/profile-picture`;
}


const cardHeight = ref(tableDiameter.value / 7);

function calculatePlayerPositions() {
  const radius = tableDiameter.value / 2;
  const numPlayers = playerInfo.value.length;

  const positions = [];
  for (let i = 0; i < numPlayers; i++) {
    const angle = (2 * Math.PI * i) / numPlayers - Math.PI / 2; // start at top center
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    positions.push({x, y});
  }

  playerPositions.value = positions;
}

function updateSizes() {
  tableDiameter.value = window.innerWidth / 2.5;
  playerWidth.value = tableDiameter.value / 3.5;
  cardHeight.value = tableDiameter.value / 7;
  calculatePlayerPositions();
}

watch(playerInfo, () => {
  calculatePlayerPositions();
}, {deep: true});

async function fetchCurrentPlayers() {
  const message = {
    command: 'current-players-info'
  }
  gameSocket.sendMessage(JSON.stringify(message));
}

function highlightPlayer(name: string): void {
  console.log('Highlight');
  playerInfo.value = playerInfo.value.map(player => ({
    ...player,
    highlighted: player.name === name,
  }));
}


gameSocket.onMessage((data) => {
  console.log(data.command);
  switch (data.command) {
    case 'player-joined-game':
      playerInfo.value.push({
        name: data.name,
        credits: data.credits,
        action: '',
        cards: [{}, {}],
        highlighted: false,
        admin: false,
      })
      break;
    case 'player-left':
      playerInfo.value = playerInfo.value.filter(player => player.name !== data.name);
      break;
    case 'current-players-info':
      const adminUser = data.admin;
      console.log(adminUser);
      let playerData = data.players as Player[];
      console.log('Player data: ', playerData);

      playerData = [
        ...playerData.filter(p => p.name === selfUsername),
        ...playerData.filter(p => p.name !== selfUsername),
      ];

      playerInfo.value = playerData.map(player => ({
        name: player.name,
        credits: player.credits,
        action: '',
        cards: [{}, {}],
        highlighted: false,
        admin: false,
      }));



      const adminPlayer = playerInfo.value.find(p => p.name === adminUser);
      if (adminPlayer) {
        adminPlayer.admin = true
      }
      console.log('Admin player: ', adminPlayer);
      break;
    case 'player-next-turn':
      highlightPlayer(data.name);
      break;
    case 'player-move-check':

      break;
    case 'player-move-fold':

      break;
    case 'player-move-call':

      break;
    case 'player-move-raise': // Raise / Bet

      break;
    case 'player-cards':
      const cardStrings = data.cards as Array<string>;
      const selfUser = playerInfo.value.find(p => p.name === selfUsername);
      if (selfUser) {
        cardStrings.forEach((cardStr, i) => {
          selfUser.cards[i] = {
            ...selfUser.cards[i],
            frontImage: getCardByString(cardStr),
            faceDown: false,
          };
        });
      }
      break;

    case 'update-game-state':
      gameRunning.value = data.gameRunning;
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
  const message = {
    command: 'leave-game',
  }
  gameSocket.sendMessage(JSON.stringify(message));
  navigateTo('/lobby-selection');
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

        <div class="start-button-container z-50" v-if="playerInfo.find(p => p.name === selfUsername)?.admin && !gameRunning">
          <GameStartButton />
        </div>

        <div class="community-cards" v-if="gameRunning">
          <Card
              v-for="(card, index) in cards"
              :key="index"
              :face-down="card.faceDown"
              :front-image="card.frontImage"
              :height="cardHeight"
              :highlighted="card.highlighted"
          />
        </div>

        <UButton @click="flipCards" :style="{ zIndex: 30 }">Flip</UButton>
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
            :highlighted="player.highlighted"
            :is-admin="player.admin"
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
    <ActionButtons :style="{zIndex: 40}"/>
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
  z-index: 20;
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
  z-index: 30;
  user-select: none;
  pointer-events: none;
}

.side_menu {
  position: absolute;
  bottom: 30%;
  left: 0;
  width: 15%;
  z-index: 50; /* higher than others */
  padding: 1rem 0;
}

</style>
