<script setup lang="ts">
import {onBeforeUnmount, onMounted, ref} from 'vue';
import Chat from '~/components/Chat.vue';
import {useUsername} from "~/composables/states";
import {navigateTo} from "#app";
import {getCardByString} from "~/utils/getCardByString";

const gameSocket = useGameSocket();
const selfUsername = useUsername().value;
const gameRunning = ref(false);


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
  winner: boolean;
  folded: boolean;
}

const playerInfo = ref<PlayerInfo[]>([]);

const communityCards = ref<Card[]>([
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
    const angle = (2 * Math.PI * i) / numPlayers - Math.PI / 2;
    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    positions.push({x, y});
  }

  playerPositions.value = positions;
}

function updateSizes() {
  tableDiameter.value = window.innerWidth / 2.5;
  playerWidth.value = tableDiameter.value / 3;
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
  playerInfo.value = playerInfo.value.map(player => ({
    ...player,
    highlighted: player.name === name,
  }));
}

function setActionText(name: string, action: string, amount?: number): void {
  let actionStr = action;
  if (amount) actionStr += ': ' + amount;
  
  const player = playerInfo.value.find(p => p.name === name);
  if (player) player.action = actionStr;
  else console.log('Cannot set action to player: ' + name + ', player doesnt exist.');
}

function toggleAllPlayerCards(faceDown: boolean) {
  for (let i = 0; i < playerInfo.value.length; i++) {
    for (let j = 0; j < playerInfo.value[i].cards.length; j++) {
      const card = playerInfo.value[i].cards[j];
      card.faceDown = faceDown;
    }
  }
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
        winner: false,
        folded: false,
      })
      break;
    case 'player-left':
      playerInfo.value = playerInfo.value.filter(player => player.name !== data.name);
      break;
    case 'current-players-info':
      const adminUser = data.admin;
      let playerData = data.players as Player[];

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
        winner: false,
        folded: false,
      }));

      const adminPlayer = playerInfo.value.find(p => p.name === adminUser);
      if (adminPlayer) {
        adminPlayer.admin = true
      }
      break;
    case 'player-next-turn':
      highlightPlayer(data.name);
      break;
    case 'player-action': {
      const name = data.name;
      const action = data.action;
      const raiseAmount = data.amount;
      
      setActionText(name, action, raiseAmount);
      
      if (action === 'FOLD') {
        const foldedPlayer = playerInfo.value.find(p => p.name === name);
        if (foldedPlayer) foldedPlayer.folded = true;
      }
      
      break;
    }
    case 'new-credits': {
      const name = data.name;
      const credits = data.amount;
      
      const player = playerInfo.value.find(p => p.name === name);
      if (player) {
        player.credits = credits;
      }
      
      break;
    }
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
      if (data.gameRunning) {
        toggleAllPlayerCards(true);
        for (let i = 0; i < communityCards.value.length; i++) {
          communityCards.value[i].faceDown = true;
        }
      }
      break;
    case 'reveal-all-cards': {
      const players: {name: string, cards: Array<string>}[] = data.players;
      players.forEach(({name, cards}) => {
        const player = playerInfo.value.find(p => p.name === name)
        if (player) {
          player.cards[0].frontImage = getCardByString(cards[0]);
          player.cards[1].frontImage = getCardByString(cards[1]);
        }
      });
      
      toggleAllPlayerCards(false);
      break;
    }
    case 'flop':
      const flopCards = data.cards as Array<string>;
      flopCards.forEach((cardStr, i) => {
        communityCards.value[i] = {
          ...communityCards.value[i],
          frontImage: getCardByString(cardStr),
          faceDown: false,
        }
      });
      break;
    case 'turn':
      const turnCard = data.card as string;
      const turnIndex = 3;
      communityCards.value[turnIndex] = {
        ...communityCards.value[turnIndex],
        frontImage: getCardByString(turnCard),
        faceDown: false,
      }
      
      break;
    case 'river':
      const riverCard = data.card as string;
      const riverIndex = 4;
      communityCards.value[riverIndex] = {
        ...communityCards.value[riverIndex],
        frontImage: getCardByString(riverCard),
        faceDown: false,
      }
      break;
  }
});

onMounted(() => {
  if (!gameSocket.isConnected) {
    navigateTo('/lobby-selection');
  }
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
        <br>
        <div class="community-cards" v-if="gameRunning">
          <Card
              v-for="(card, index) in communityCards"
              :key="index"
              :face-down="card.faceDown"
              :front-image="card.frontImage"
              :height="cardHeight"
              :highlighted="card.highlighted"
          />
        </div>
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
            :is-winner="player.winner"
            :is-folded="player.folded"
        />
      </div>
    </div>

  </div>
  <div class="chat-area">
    <Chat/>
  </div>
  <div class="side_menu">
    <ActionMenu class="z-40"/>
  </div>
  <div class="pot-area">
    <PotDisplay />
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
  top: 50%;
  left: 2%;
  width: 15%;
  z-index: 50;
  padding: 1rem 0;
  transform: translateY(-50%);
}

.pot-area {
  position: absolute;
  top: 5%;
  right: 2%;
  transform: translateY(50%);
}

</style>
